package com.yuanren.dribbbo.view.shot_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.view.base.BaseViewHolder;

import butterknife.BindView;

public class ShotViewHolder extends BaseViewHolder {

    @BindView(R.id.shot_image) public ImageView image;
    @BindView(R.id.shot_view_count) public TextView viewCount;
    @BindView(R.id.shot_like_count) public TextView likeCount;
    @BindView(R.id.shot_bucket_count) public TextView bucketCount;
    @BindView(R.id.shot_clickable_cover) public View cover;

    public ShotViewHolder(View itemView) {
        super(itemView);
    }
}
