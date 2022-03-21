package com.yuanren.dribbbo.model;

public class User {
    private String name;
    private String avatar_url;

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatarUrl(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
