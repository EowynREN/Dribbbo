package com.yuanren.dribbbo.view.bucket_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.view.base.BaseViewHolder;

import butterknife.BindView;

public class BucketViewHolder extends BaseViewHolder {

    @BindView(R.id.bucket_name) public TextView bucketame;
    @BindView(R.id.bucket_shot_count) public TextView bucketShotCount;
    @BindView(R.id.bucket_shot_chosen) public ImageView bucketShotChosen;
    @BindView(R.id.bucket_clickable_cover) public View cover;

    public BucketViewHolder(View itemView) {
        super(itemView);
    }
}
