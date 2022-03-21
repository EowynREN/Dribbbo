package com.yuanren.dribbbo.view.bucket_list;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.model.Bucket;
import com.yuanren.dribbbo.view.shot_list.ShotListAdapter;
import com.yuanren.dribbbo.view.shot_list.ShotListFragment;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class BucketListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_BUCKET = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private List<Bucket> data;
    private LoadMoreListener loadMoreListener;
    private boolean showLoading;
    private boolean isChoosingMode;

    public BucketListAdapter (@NonNull List<Bucket> data, @NonNull LoadMoreListener loadMoreListener, boolean isChoosingMode){
        this.data = data;
        this.loadMoreListener = loadMoreListener;
        this.showLoading = true;
        this.isChoosingMode = isChoosingMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_BUCKET){
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.list_item_bucket, parent, false);

            return new BucketViewHolder(view);
        } else {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.list_item_loading, parent, false);
            return new RecyclerView.ViewHolder(view){};
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // note the warning for "final int position", it's for recycler view drag and drop
        // after drag and drop onBindViewHolder will not be call again with the new position,
        // that's why we should not assume this position is always fixed.

        // in our case, we do not support drag and drop in bucket list because Dribbble API
        // doesn't support reordering buckets, so using "final int position" is fine

        if (getItemViewType(position) == VIEW_TYPE_LOADING){
            loadMoreListener.onLoadMore();
        }
        else{
            final Bucket bucket = data.get(position);
            BucketViewHolder bucketViewHolder = (BucketViewHolder) holder;
            Context context = bucketViewHolder.itemView.getContext();

            String bucketCountShotString = MessageFormat.format(context
                                                        .getResources()
                                                        .getString(R.string.shot_count),
                    bucket.getShotsCount());

            bucketViewHolder.bucketame.setText(bucket.getName());
            bucketViewHolder.bucketShotCount.setText(bucketCountShotString);

            if (isChoosingMode){
                bucketViewHolder.bucketShotChosen.setVisibility(View.VISIBLE);
                bucketViewHolder.bucketShotChosen.setImageResource(
                        bucket.isChoosing()
                                ? R.drawable.ic_check_box_black_24dp
                                : R.drawable.ic_check_box_outline_blank_black_24dp
                );

                bucketViewHolder.cover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bucket.setChoosing(!bucket.isChoosing());
                        notifyItemChanged(position);
                    }
                });
            }
            else {
                bucketViewHolder.bucketShotChosen.setVisibility(View.GONE);
                bucketViewHolder.cover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // if not in choosing mode, we need to open a new Activity to show
                        // what shots are in this bucket, we will need ShotListFragment here!
                        Context context = v.getContext();

                        Intent intent = new Intent(context, BucketShotListActivity.class);
                        intent.putExtra(ShotListFragment.KEY_BUCKET_ID, bucket.getId());
                        intent.putExtra(BucketShotListActivity.KEY_BUCKET_NAME, bucket.getName());

                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return showLoading ? data.size() + 1 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position < data.size() ? VIEW_TYPE_BUCKET : VIEW_TYPE_LOADING;
    }



    public int getDataCount(){
        return data.size();
    }

    public void append(@NonNull List<Bucket> moreBuckets){
        data.addAll(moreBuckets);
        notifyDataSetChanged();
    }

    public void prepend(@NonNull List<Bucket> newBucket){
        data.addAll(0, newBucket);
        notifyDataSetChanged();
    }

    public void setBuckets(List<Bucket> buckets) {
        data.clear();
        data.addAll(buckets);
        notifyDataSetChanged();
    }

    public void setShowLoading(boolean showLoading){
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedBucketIds(){
        ArrayList<String> selectedBucketIds = new ArrayList<>();

        for (Bucket bucket : data){
            if (bucket.isChoosing()){
                selectedBucketIds.add(bucket.getId());
            }
        }

        return selectedBucketIds;
    }

    public interface LoadMoreListener{
        void onLoadMore();
    }
}
