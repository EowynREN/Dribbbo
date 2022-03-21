package com.yuanren.dribbbo.model;

import java.util.Date;

public class Bucket {

    private String id;
    private String name;
    private String description;
    private int shots_count;
    private Date create_at;
    private boolean isChoosing;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getShotsCount() {
        return shots_count;
    }

    public Date getCreateAt() {
        return create_at;
    }

    public boolean isChoosing() {
        return isChoosing;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShotsCount(int shots_count) {
        this.shots_count = shots_count;
    }

    public void setCreateAt(Date create_at) {
        this.create_at = create_at;
    }

    public void setChoosing(boolean choosing) {
        isChoosing = choosing;
    }
}

