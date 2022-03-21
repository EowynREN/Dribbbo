package com.yuanren.dribbbo.view.shot_list;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.model.Shot;
import com.yuanren.dribbbo.utils.ModelUtils;
import com.yuanren.dribbbo.view.shot_detail.ShotActivity;
import com.yuanren.dribbbo.view.shot_detail.ShotFragment;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShotListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_SHOT = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private List<Shot> data;
    private LoadMoreListener loadMoreListener;
    private boolean showLoading;

    public ShotListAdapter(@NonNull  List<Shot> data, @NonNull LoadMoreListener loadMoreListener){
        this.data = data;
        this.loadMoreListener = loadMoreListener;
        this.showLoading = true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == VIEW_TYPE_SHOT){
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.list_item_shot, parent, false);
            return new ShotViewHolder(view);
        }
        else {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.list_item_loading, parent, false);
            return new RecyclerView.ViewHolder(view){};
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_LOADING){
            loadMoreListener.onLoadMore();
        }
        else {
            final Shot shot = data.get(position);

            ShotViewHolder shotViewHolder = (ShotViewHolder) holder;
            shotViewHolder.viewCount.setText(String.valueOf(shot.getViewsCount()));
            shotViewHolder.likeCount.setText(String.valueOf(shot.getLikesCount()));
            shotViewHolder.bucketCount.setText(String.valueOf(shot.getBucketsCount()));

            // context is needed here to cache image data
            Picasso.with(holder.itemView.getContext())
                    .load(shot.getImageUrl())
                    .placeholder(R.drawable.shot_placeholder)
                    .into(shotViewHolder.image);

            shotViewHolder.cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = holder.itemView.getContext();

                    Intent intent = new Intent(context, ShotActivity.class);
                    intent.putExtra(ShotFragment.KEY_SHOT, ModelUtils.toString(shot, new TypeToken<Shot>(){}));
                    intent.putExtra(ShotActivity.KEY_SHOT_TITLE, shot.getTitle());

                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return showLoading ? data.size() + 1 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position < data.size()
                ? VIEW_TYPE_SHOT
                : VIEW_TYPE_LOADING;
    }

    public int getDataCount() {
        return data.size();
    }

    public void append(List<Shot> moreShots){
        data.addAll(moreShots);

        // android API， notify UI thread after data is updated (won't refresh the whole list)
        notifyDataSetChanged();
    }

    public void setData(List<Shot> shots) {
        data.clear();
        data.addAll(shots);
        notifyDataSetChanged();
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    public interface LoadMoreListener{
        void onLoadMore();
    }
}
