package com.yuanren.dribbbo.view.base;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
