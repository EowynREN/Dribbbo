package com.yuanren.dribbbo.view.shot_detail;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.dribbble.Dribbble;
import com.yuanren.dribbbo.model.Bucket;
import com.yuanren.dribbbo.model.Comment;
import com.yuanren.dribbbo.model.Shot;
import com.yuanren.dribbbo.utils.ModelUtils;
import com.yuanren.dribbbo.view.base.DividerItemDecoration;
import com.yuanren.dribbbo.view.bucket_list.BucketListFragment;
import com.google.gson.reflect.TypeToken;
import 	com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ShotFragment extends Fragment {

    public final static String KEY_SHOT = "shot";
    public static final int REQ_CODE_BUCKET = 100;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    private ShotAdapter adapter;
    private Shot shot;

    public static ShotFragment newInstance(@NonNull Bundle args) {
        ShotFragment fragment = new ShotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycle_view, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        shot = ModelUtils.toObject(getArguments().getString(KEY_SHOT), new TypeToken<Shot>(){});

        adapter = new ShotAdapter(this, shot, new ArrayList<Comment>(), new ShotAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                int page = adapter.getCommentsCount() / Dribbble.COUNT_PER_PAGE + 1;

//                AsyncTaskCompat.executeParallel(new LoadShotCommentsTask(shot.getId(), page));
//                AsyncTaskCompat.executeParallel(new CheckUserLikedComment(shot.getId(), page));
                new LoadShotCommentsTask(shot.getId(), page).execute();
                new CheckUserLikedComment(shot.getId(), page).execute();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(
                        ContextCompat.getDrawable(getContext(), R.drawable.line_divider),
                        getResources().getDimensionPixelOffset(R.dimen.spacing_medium)
                ));
        recyclerView.setAdapter(adapter);

        // Used for dosplay bucket (pink) or unbucket (gray)
//        AsyncTaskCompat.executeParallel(new LoadCollectedBucketIdsTask());
//        AsyncTaskCompat.executeParallel(new CheckUserLikedShot(shot.getId()));
        new LoadCollectedBucketIdsTask().execute();
        new CheckUserLikedShot(shot.getId()).execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // reload shot data (refresh)
//                AsyncTaskCompat.executeParallel(new ReloadShotTask(shot.getId()));
                new ReloadShotTask(shot.getId()).execute();

                // whether this shot is bucketed by user
//                AsyncTaskCompat.executeParallel(new LoadCollectedBucketIdsTask());
                new LoadCollectedBucketIdsTask().execute();

                // whether this shot is liked by user
//                AsyncTaskCompat.executeParallel(new CheckUserLikedShot(shot.getId()));
                new CheckUserLikedShot(shot.getId()).execute();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_BUCKET && resultCode == Activity.RESULT_OK) {
            List<String> chosenBucketIds = data.getStringArrayListExtra(BucketListFragment.KEY_CHOSEN_BUCKET_IDS);
            List<String> addedBucketIds = new ArrayList<>();
            List<String> removedBucketIds = new ArrayList<>();
            List<String> collectedBucketIds = adapter.getReadOnlyCollectedBucketIds();

            for (String chosenBucketId : chosenBucketIds) {
                if (!collectedBucketIds.contains(chosenBucketId)) {
                    addedBucketIds.add(chosenBucketId);
                }
            }

            for (String collectedBucketId : collectedBucketIds) {
                if (!chosenBucketIds.contains(collectedBucketId)) {
                    removedBucketIds.add(collectedBucketId);
                }
            }

//            AsyncTaskCompat.executeParallel(new UpdateBucketTask(addedBucketIds, removedBucketIds));
           new UpdateBucketTask(addedBucketIds, removedBucketIds).execute();
        }
    }


    /* ---------------------------------------------------------------------------------- */
    /* ------------------------------------- shot --------------------------------------- */
    /* ---------------------------------------------------------------------------------- */

    private class LoadCollectedBucketIdsTask extends AsyncTask<Void, Void, List<String>>{

        @Override
        protected List<String> doInBackground(Void... params) {
            try {

                // find the intersection of the bucket contains this shot and the user own bucket
                List<Bucket> userBuckets = Dribbble.getUserBuckets();
                List<Bucket> shotBuckets = Dribbble.getShotBuckets(shot.getId());

                Set<String> userBucketIds = new HashSet<>();
                for (Bucket userBucket : userBuckets){
                    userBucketIds.add(userBucket.getId());
                }

                List<String> collectedBucketIds = new ArrayList<>();
                for (Bucket shotBucket : shotBuckets){
                    if (userBucketIds.contains(shotBucket.getId())){
                        collectedBucketIds.add(shotBucket.getId());
                    }
                }

                // common Ids
                return collectedBucketIds;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> collectedBucketIds) {
            adapter.updateCollectedBucketIds(collectedBucketIds);
        }
    }

    private class UpdateBucketTask extends AsyncTask<Void, Void, Void>{

        private List<String> added;
        private List<String> removed;
        private Exception e;

        public UpdateBucketTask(List<String> added, List<String> removed){
            this.added = added;
            this.removed = removed;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (String addedId : added){
                    Dribbble.addShotToBucket(shot.getId(), addedId);
                }

                for (String removedId : removed){
                    Dribbble.removeShotFromBucket(shot.getId(), removedId);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                this.e = e1;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (e == null){
                adapter.updateCollectedBucketIds(added, removed);
            }
            else {
                Snackbar.make(getView(), "Error", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private class CheckUserLikedShot extends AsyncTask<Void, Void, Boolean>{

        private String id;

        public CheckUserLikedShot(String id){
            this.id = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return Dribbble.checkShotLiked(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            boolean isLiked = result.booleanValue();
            adapter.setShotLiked(isLiked);
        }
    }
    public void likeShot(@NonNull String shotId, boolean like) {
//        AsyncTaskCompat.executeParallel(new LikeShotTask(shotId, like));
        new LikeShotTask(shotId, like).execute();
    }

    private class LikeShotTask extends AsyncTask<Void, Void, Void>{

        private String id;
        private boolean isLiked;

        public LikeShotTask(String id, boolean isLiked){
            this.id = id;
            this.isLiked = isLiked;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (isLiked){
                    Dribbble.unlikeShot(id);
                }
                else {
                    Dribbble.likeShot(id);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.setShotLiked(!isLiked, !isLiked ? shot.getLikesCount() + 1 : shot.getLikesCount() - 1);
        }
    }

    private class ReloadShotTask extends AsyncTask<Void, Void, Shot>{

        private String id;

        public ReloadShotTask(String id){
            this.id = id;
        }

        @Override
        protected Shot doInBackground(Void... params) {
            try {
                return Dribbble.getShot(id);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Shot shot) {
            if (shot != null){
                adapter.setShot(shot);
                // reset showLoading = true after refreshing to avoid endless loading
                adapter.setShowLoading(true);
                swipeRefreshLayout.setRefreshing(false);
            }
            else {
                Snackbar.make(getView(), "Error", Snackbar.LENGTH_LONG).show();
            }
        }
    }


    /* ---------------------------------------------------------------------------------- */
    /* ----------------------------------- comments ------------------------------------- */
    /* ---------------------------------------------------------------------------------- */

    private class LoadShotCommentsTask extends AsyncTask<Void, Void, List<Comment>>{

        private String id;
        private int page;

        public LoadShotCommentsTask(String id, int page){
            this.id = id;
            this.page = page;
        }

        @Override
        protected List<Comment> doInBackground(Void... params) {
            try {
                return Dribbble.getShotComments(id, page);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Comment> comments) {
            adapter.appendComments(comments);

            if (comments.size() < Dribbble.COUNT_PER_PAGE){
                adapter.setShowLoading(false);
            }
        }
    }

    private class CheckUserLikedComment extends AsyncTask<Void, Void, Set<String>>{

        private String shotId;
        private int page;

        public CheckUserLikedComment(String shotId, int page){
            this.shotId = shotId;
            this.page = page;
        }

        @Override
        protected Set<String> doInBackground(Void... params) {

            try {
                Set<String> likedCommentsId = new HashSet<>();
                List<Comment> comments = Dribbble.getShotComments(shotId, page);

                for (Comment comment : comments){
                    boolean isLiked = Boolean.valueOf(Dribbble.checkCommentLiked(shotId, comment.getId()));

                    if (isLiked){
                        likedCommentsId.add(comment.getId());
                    }
                }

                return likedCommentsId;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Set<String> likedCommentsId) {
            adapter.setCommentLiked(likedCommentsId);
        }
    }

    public void likeComment(String shotId, Comment comment){
//        AsyncTaskCompat.executeParallel(new LikeCommentTask(shotId, comment));
        new LikeCommentTask(shotId, comment).execute(   );
    }

    private class LikeCommentTask extends AsyncTask<Void, Void, Void>{

        private String shotId;
        private String commentId;
        private boolean isLiked;

        private Comment comment;

        public LikeCommentTask(String shotId, Comment comment){
            this.shotId = shotId;
            this.commentId = comment.getId();
            this.isLiked = comment.isLiked();
            this.comment = comment;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (isLiked){
                    Dribbble.unlikeShotComment(shotId, commentId);
                }
                else {
                    Dribbble.likeShotComment(shotId, commentId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.setCommentLiked(
                    comment,
                    !isLiked,
                    !isLiked ? comment.getLikesCount() + 1 : comment.getLikesCount() - 1);
        }
    }
}
