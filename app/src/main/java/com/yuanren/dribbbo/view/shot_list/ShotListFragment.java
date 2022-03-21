package com.yuanren.dribbbo.view.shot_list;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.dribbble.Dribbble;
import com.yuanren.dribbbo.model.Shot;
import com.yuanren.dribbbo.view.base.SpaceItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ShotListFragment extends Fragment {
    public static final String KEY_LIST_TYPE = "listType";
    public static final String KEY_BUCKET_ID = "bucketId";

    public static final int LIST_TYPE_POPULAR = 1;
    public static final int LIST_TYPE_LIKED = 2;
    public static final int LIST_TYPE_BUCKET = 3;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    private ShotListAdapter adapter;

    private int listType;
    private String bucketId;

    public static ShotListFragment newInstance(int listType) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType);

        ShotListFragment shotListFragment = new ShotListFragment();
        shotListFragment.setArguments(args);

        return  shotListFragment;
    }

    public static ShotListFragment newBucketListInstance(String bucketId){
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, LIST_TYPE_BUCKET);
        args.putString(KEY_BUCKET_ID, bucketId);

        ShotListFragment shotListFragment = new ShotListFragment();
        shotListFragment.setArguments(args);

        return shotListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycle_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listType = getArguments().getInt(KEY_LIST_TYPE);
        bucketId = getArguments().getString(KEY_BUCKET_ID);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

        // disable refresh util all data is loaded
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                Toast.makeText(getContext(), "Refreshing", Toast.LENGTH_LONG).show();
//                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
                new LoadShotsTask(true).execute();
            }
        });

        adapter = new ShotListAdapter(new ArrayList<Shot>(), new ShotListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                // this method will be called when the RecyclerView is displayed
                // page starts from 1
//                AsyncTaskCompat.executeParallel(new LoadShotsTask(adapter.getDataCount() / Dribbble.COUNT_PER_PAGE + 1));
                new LoadShotsTask(adapter.getDataCount() / Dribbble.COUNT_PER_PAGE + 1).execute();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private class LoadShotsTask extends AsyncTask<Void, Void, List<Shot>> {

        int page;

        boolean refresh;

        public LoadShotsTask(int page){
            this.page = page;
        }

        public LoadShotsTask(boolean refresh){
            this.refresh = refresh;
        }

        @Override
        protected List<Shot> doInBackground(Void... params) {
            try {

                switch (listType){
                    case LIST_TYPE_POPULAR:
                        return refresh ? Dribbble.getShots(1) : Dribbble.getShots(page);
                    case LIST_TYPE_LIKED:
                        return refresh ? Dribbble.getLikedShots(1) : Dribbble.getLikedShots(page);
                    case LIST_TYPE_BUCKET:
                        return refresh ? Dribbble.getBucketShots(bucketId, 1) : Dribbble.getBucketShots(bucketId, page);
                    default:
                        return refresh ? Dribbble.getShots(1) : Dribbble.getShots(page);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Shot> shots) {
            if (shots != null){

                if (refresh){
                    adapter.setData(shots);
                    // make sure showLoading = true after refreshing to avoid endless loading
                    adapter.setShowLoading(true);

                    // stop animation for refresh
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    adapter.append(shots);
                    if (shots.size() < Dribbble.COUNT_PER_PAGE){
                        adapter.setShowLoading(false);
                    }
                }

                // enable refresh after all data is loaded and set
                swipeRefreshLayout.setEnabled(true);
            }
            else{
                Snackbar.make(getView(), "Error", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
