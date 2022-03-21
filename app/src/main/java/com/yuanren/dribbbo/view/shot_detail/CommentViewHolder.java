package com.yuanren.dribbbo.view.shot_detail;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.view.base.BaseViewHolder;

import butterknife.BindView;

public class CommentViewHolder extends BaseViewHolder {

    @BindView(R.id.comment_author_picture) ImageView authorPicture;
    @BindView(R.id.comment_author_name) TextView authorName;
    @BindView(R.id.comment_content) TextView content;
    @BindView(R.id.comment_create_time) TextView createAt;
    @BindView(R.id.comment_action_like) ImageButton likeButton;
    @BindView(R.id.comment_like_count) TextView likeCount;

    public CommentViewHolder(View itemView) {
        super(itemView);
    }
}
