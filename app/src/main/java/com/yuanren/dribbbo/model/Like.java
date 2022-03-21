package com.yuanren.dribbbo.model;

import java.util.Date;

public class Like {

    private String id;
    private Date created_at;
    private Shot shot;
    private Comment comment;

    public String getId() {
        return id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public Shot getShot() {
        return shot;
    }

    public Comment getComment() {
        return comment;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setShot(Shot shot) {
        this.shot = shot;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
