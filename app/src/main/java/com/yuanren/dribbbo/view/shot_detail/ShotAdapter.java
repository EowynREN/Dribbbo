package com.yuanren.dribbbo.view.shot_detail;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.model.Comment;
import com.yuanren.dribbbo.model.Like;
import com.yuanren.dribbbo.model.Shot;
import com.yuanren.dribbbo.utils.DateUtils;
import com.yuanren.dribbbo.utils.ImageUtils;
import com.yuanren.dribbbo.view.bucket_list.BucketListFragment;
import com.yuanren.dribbbo.view.bucket_list.ChooseBucketActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ShotAdapter extends RecyclerView.Adapter {

    private final static int VIEW_TYPE_SHOT_IMAGE = 0;
    private final static int VIEW_TYPE_SHOT_INFO = 1;
    private final static int VIEW_TYPE_LOADING = 2;
    private final static int VIEW_TYPE_SHOT_COMMENT = 3;


    private Shot shot;
    private List<Comment> comments;
    private LoadMoreListener loadMoreListener;
    private boolean showLoading;
    private final ShotFragment shotFragment;
    private ArrayList<String> collectedBucketIds;

    public ShotAdapter(@NonNull ShotFragment shotFragment,
                       @NonNull Shot shot,
                       @NonNull List<Comment> comments,
                       @NonNull LoadMoreListener loadMoreListener){
        this.shot = shot;
        this.comments = comments;
        this.loadMoreListener = loadMoreListener;
        this.showLoading = true;

        this.shotFragment = shotFragment;
        this.collectedBucketIds = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType){
            case VIEW_TYPE_SHOT_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shot_item_image, parent, false);
                return new ImageViewHolder(view);
            case VIEW_TYPE_SHOT_INFO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shot_item_info, parent, false);
                return new InfoViewHolder(view);
            case VIEW_TYPE_SHOT_COMMENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shot_item_comment, parent, false);
                return new CommentViewHolder(view);
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loading, parent, false);
                return new RecyclerView.ViewHolder(view){};
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        final Context context = holder.itemView.getContext();

        switch (viewType){
            case VIEW_TYPE_SHOT_IMAGE:
                ImageViewHolder shotImageViewHolder = (ImageViewHolder) holder;
                ImageUtils.loadShotImage(context, shot.getImageUrl(), shotImageViewHolder.imageView);
                break;

            case VIEW_TYPE_SHOT_INFO:
                InfoViewHolder shotDetailViewHolder = (InfoViewHolder) holder;

                // set author's avatar
                ImageUtils.loadUserPicture(context, shot.getUser().getAvatarUrl(), shotDetailViewHolder.authorPicture);

                // set shot details and author's info
                shotDetailViewHolder.title.setText(shot.getTitle());
                shotDetailViewHolder.authorName.setText(shot.getUser().getName());
                shotDetailViewHolder.description.setText(Html.fromHtml(
                        shot.getDescription() == null
                                ? "" : shot.getDescription()));

                // set shot data
                shotDetailViewHolder.viewCount.setText(String.valueOf(shot.getViewsCount()));
                shotDetailViewHolder.likeCount.setText(String.valueOf(shot.getLikesCount()));
                shotDetailViewHolder.bucketCount.setText(String.valueOf(shot.getBucketsCount()));

                // set icon style
                shotDetailViewHolder.bucketButton.setImageDrawable(
                        shot.isBucketed()
                                ? ContextCompat.getDrawable(context, R.drawable.ic_shopping_basket_dribbble_18dp)
                                : ContextCompat.getDrawable(context, R.drawable.ic_shopping_basket_black_18dp)
                );

                shotDetailViewHolder.likeButton.setImageDrawable(
                        shot.isLiked()
                                ? ContextCompat.getDrawable(context, R.drawable.ic_favorite_dribbble_18dp)
                                : ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_black_18dp)
                );

                // set listener for like btn
                shotDetailViewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shotFragment.likeShot(shot.getId(), shot.isLiked());
                    }
                });

                // set listener for bucket btn
                shotDetailViewHolder.bucketButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bucket(v.getContext());
                    }
                });

                // set listener for share btn
                shotDetailViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        share(v.getContext());
                    }
                });

                shotDetailViewHolder.authorPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "author picture clicked", Toast.LENGTH_LONG).show();
                    }
                });

                break;

            case VIEW_TYPE_SHOT_COMMENT:
                CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                // position - 2 = num of comments（exclude SHOT_IMAGE and SHOT_INFO）
                final Comment comment = comments.get(position - 2);

                ImageUtils.loadUserPicture(context, comment.getUser().getAvatarUrl(), commentViewHolder.authorPicture);

                commentViewHolder.authorName.setText(comment.getUser().getName());
                commentViewHolder.content.setText(Html.fromHtml(comment.getBody()));
                commentViewHolder.createAt.setText(DateUtils.dateToString(comment.getCreatedAt()));
                commentViewHolder.likeCount.setText(String.valueOf(comment.getLikesCount()));

                commentViewHolder.likeButton.setImageDrawable(
                        comment.isLiked()
                                ? ContextCompat.getDrawable(context, R.drawable.ic_favorite_dribbble_18dp)
                                : ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_grey_18dp));

                commentViewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(context, "like button clicked", Toast.LENGTH_LONG).show();
                        shotFragment.likeComment(shot.getId(), comment);
                    }
                });

                break;

            case VIEW_TYPE_LOADING:
                loadMoreListener.onLoadMore();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return showLoading
                ? 2 + comments.size() + 1
                : 2 + comments.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return VIEW_TYPE_SHOT_IMAGE;
        }

        if (position == 1){
            return VIEW_TYPE_SHOT_INFO;
        }

        return position - 2 < comments.size()
                ? VIEW_TYPE_SHOT_COMMENT
                : VIEW_TYPE_LOADING;
    }

    public int getCommentsCount(){
        return comments.size();
    }

    public void setShowLoading(boolean showLoading){
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    /* ---------------------------------------------------------------------------------- */
    /* ------------------------------------ shots --------------------------------------- */
    /* ---------------------------------------------------------------------------------- */

    /* this function is used for resetting shot after refreshing */
    public void  setShot(Shot shot) {
        this.shot.setTitle(shot.getTitle());
        this.shot.setImageUrl(shot.getImages());
        this.shot.setViewsCount(shot.getViewsCount());
        this.shot.setLikesCount(shot.getLikesCount());
        this.shot.setBucketsCount(shot.getBucketsCount());
        this.shot.setUser(shot.getUser());
        this.shot.setDescription(shot.getDescription());

        this.comments.clear();

        notifyDataSetChanged();
    }

    /* this function is used for resetting like button  after refreshing */
    public void setShotLiked(boolean liked) {
        this.shot.setLiked(liked);
        notifyDataSetChanged();
    }

    public void setShotLiked(boolean liked, int likeCount) {
        this.shot.setLiked(liked);
        this.shot.setLikesCount(likeCount);
        notifyDataSetChanged();
    }

    /* these three functions below are used for bucketed feature */
    public List<String> getReadOnlyCollectedBucketIds() {
        return Collections.unmodifiableList(collectedBucketIds);
    }

    public void updateCollectedBucketIds(List<String> bucketIds){
        if (collectedBucketIds == null){
            collectedBucketIds = new ArrayList<>();
        }

        collectedBucketIds.clear();
        collectedBucketIds.addAll(bucketIds);

        // set bucket icon color based on whether it's bucketed (pink) or not (grey)
        shot.setBucketed(!collectedBucketIds.isEmpty());
        notifyDataSetChanged();

    }

    public void updateCollectedBucketIds(List<String> added, List<String> removed){
        if (collectedBucketIds == null){
            collectedBucketIds = new ArrayList<>();
        }

        collectedBucketIds.addAll(added);
        collectedBucketIds.removeAll(removed);

        shot.setBucketed(!collectedBucketIds.isEmpty());
        shot.setBucketsCount(shot.getBucketsCount() + added.size() - removed.size());
        notifyDataSetChanged();
    }

    /*  */
    private void bucket(@NonNull Context context){
        // collectedBucketIds == null means we're still loading
        if (collectedBucketIds != null){
            Intent intent = new Intent(context, ChooseBucketActivity.class);
            intent.putStringArrayListExtra(BucketListFragment.KEY_CHOSEN_BUCKET_IDS,
                    collectedBucketIds);
            shotFragment.startActivityForResult(intent, ShotFragment.REQ_CODE_BUCKET);
        }

    }

    private void share(@NonNull Context context){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shot.getTitle() + " " + shot.getHtmlUrl());
        shareIntent.setType("text/plain");

        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_shot)));
    }


    /* ---------------------------------------------------------------------------------- */
    /* ----------------------------------- comments ------------------------------------- */
    /* ---------------------------------------------------------------------------------- */
    public void appendComments(List<Comment> moreComments){
        comments.addAll(moreComments);
        notifyDataSetChanged();
    }

    public void setCommentLiked(Set<String> likedCommentsId){
        for (Comment comment : comments){
            if (likedCommentsId.contains(comment.getId())){
                comment.setLiked(true);
            }
        }
        notifyDataSetChanged();
    }

    public void setCommentLiked(Comment comment, boolean liked, int likeCount){
        comment.setLiked(liked);
        comment.setLikesCount(likeCount);
        notifyDataSetChanged();
    }

    public interface LoadMoreListener{
        void onLoadMore();
    }

}
