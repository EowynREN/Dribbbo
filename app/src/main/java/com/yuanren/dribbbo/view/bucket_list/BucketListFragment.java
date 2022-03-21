package com.yuanren.dribbbo.view.bucket_list;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.snackbar.Snackbar;

import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.dribbble.Dribbble;
import com.yuanren.dribbbo.model.Bucket;
import com.yuanren.dribbbo.view.base.SpaceItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BucketListFragment extends Fragment {

    public static final int REQ_CODE_NEW_BUCKET = 100;

    public static final String KEY_CHOOSING_MODE = "choose_mode";
    public static final String KEY_CHOSEN_BUCKET_IDS = "chosen_bucket_ids";

    @BindView(R.id.bucket_list) RecyclerView recyclerView;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    private BucketListAdapter adapter;
    private boolean isChoosingMode;
    private List<String>  chosenBucketIds;

    public static BucketListFragment newInstance(boolean isChoosingMode, @NonNull ArrayList<String> chosenBucketIds)  {
        Bundle args = new Bundle();
        args.putBoolean(KEY_CHOOSING_MODE, isChoosingMode);
        args.putStringArrayList(KEY_CHOSEN_BUCKET_IDS, chosenBucketIds);

        BucketListFragment bucketListFragment= new BucketListFragment();
        bucketListFragment.setArguments(args);

        return bucketListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bucket_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        isChoosingMode = getArguments().getBoolean(KEY_CHOOSING_MODE);
        chosenBucketIds = getArguments().getStringArrayList(KEY_CHOSEN_BUCKET_IDS);
        if (chosenBucketIds == null){
            chosenBucketIds = new ArrayList<>();
        }

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                AsyncTaskCompat.executeParallel(new LoadBucketsTask(true));
                new LoadBucketsTask(true).execute();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.spacing_medium)));
        adapter = new BucketListAdapter(new ArrayList<Bucket>(), new BucketListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                // this method will be called when the RecyclerView is displayed
                // page starts from 1
//                AsyncTaskCompat.executeParallel(new LoadBucketsTask(adapter.getDataCount() / Dribbble.COUNT_PER_PAGE + 1));
                new LoadBucketsTask(adapter.getDataCount() / Dribbble.COUNT_PER_PAGE + 1).execute();
            }
        }, isChoosingMode);

        recyclerView.setAdapter(adapter);

        // attach onClickListener when insert bucket
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewBucketDialogFragment newBucketDialogFragment = NewBucketDialogFragment.newInstance().newInstance();
                newBucketDialogFragment.setTargetFragment(BucketListFragment.this, REQ_CODE_NEW_BUCKET);
                newBucketDialogFragment.show(getFragmentManager(), NewBucketDialogFragment.TAG);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isChoosingMode){
            inflater.inflate(R.menu.bucket_list_choose_mode_menu, menu);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE_NEW_BUCKET){
            String bucketName = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_NAME);
            String bucketDescription = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_DESCRIPTION);

            if (!TextUtils.isEmpty(bucketName)){
//                AsyncTaskCompat.executeParallel(new NewBucketTask(bucketName, bucketDescription));
                new NewBucketTask(bucketName, bucketDescription).execute();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save){
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra(KEY_CHOSEN_BUCKET_IDS, adapter.getSelectedBucketIds());
            getActivity().setResult(Activity.RESULT_OK, resultIntent);
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadBucketsTask extends AsyncTask<Void, Void, List<Bucket>>{

        int page;

        boolean refresh;

        public LoadBucketsTask(int page){
            this.page = page;
        }

        public LoadBucketsTask(boolean refresh){
            this.refresh = refresh;
        }

        @Override
        protected List<Bucket> doInBackground(Void... params) {

            try {
                return refresh ? Dribbble.getUserBuckets(1) : Dribbble.getUserBuckets(page);
            } catch (IOException e) {
                e.printStackTrace();
                return null;

            }
        }

        @Override
        protected void onPostExecute(List<Bucket> buckets) {
            if (buckets != null){

                if (isChoosingMode){
                    // mark each bucket whether it's been chosen
                    for (Bucket bucket : buckets){
                        if (chosenBucketIds.contains(bucket.getId())){
                            bucket.setChoosing(true);
                        }
                    }
                }

                if (refresh){
                    adapter.setBuckets(buckets);
                    // make sure showLoading = true after refreshing to avoid endless loading
                    adapter.setShowLoading(true);
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    adapter.append(buckets);
                    if (buckets.size() < Dribbble.COUNT_PER_PAGE){
                        adapter.setShowLoading(false);
                    }
                }
                swipeRefreshLayout.setEnabled(true);
            }
            else {
                Snackbar.make(getView(), "Error", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private class NewBucketTask extends AsyncTask<Void, Void, Bucket>{

        private String name;
        private String description;

        public  NewBucketTask(String name, String description){
            this.name = name;
            this.description = description;
        }

        @Override
        protected Bucket doInBackground(Void... params) {
            try {
                return Dribbble.newBucket(name, description);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bucket newBucket) {
            if (newBucket != null){
                adapter.prepend(Collections.singletonList(newBucket));
            }
            else{
                Snackbar.make(getView(), "Error", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
