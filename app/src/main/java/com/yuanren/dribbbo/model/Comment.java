package com.yuanren.dribbbo.model;

import java.util.Date;

public class Comment {
    private String id;
    private String body;
    private int likes_count;
    private Date created_at;
    private User user;
    private boolean isLiked;

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public int getLikesCount() {
        return likes_count;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public User getUser() {
        return user;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setLikesCount(int likes_count) {
        this.likes_count = likes_count;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}

