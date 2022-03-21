package com.yuanren.dribbbo.model;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.Map;

public class Shot {

    public static final String IMAGE_NORMAL = "normal";
    public static final String IMAGE_HIDPI = "hidpi";

    private String id;
    private String title;
    private String description;
    private String html_url;

    private int width;
    private int height;
    private Map<String, String> images;
    private boolean animated;

    private int views_count;
    private int likes_count;
    private int buckets_count;

    private Date created_at;

    private User user;

    private boolean liked;
    private boolean bucketed;

    @Nullable
    public String getImageUrl() {
        if (images == null) {
            return null;
        } else if (animated) {
            return images.get(IMAGE_NORMAL);
        }

        return images.containsKey(IMAGE_HIDPI)
                ? images.get(IMAGE_HIDPI)
                : images.get(IMAGE_NORMAL);
    }

    public void setImageUrl(Map<String, String> images){
        this.images.put(IMAGE_NORMAL, images.get(IMAGE_NORMAL));
        this.images.put(IMAGE_HIDPI, images.get(IMAGE_HIDPI));
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getHtmlUrl() {
        return html_url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<String, String> getImages() {
        return images;
    }

    public boolean isAnimated() {
        return animated;
    }

    public int getViewsCount() {
        return views_count;
    }

    public int getLikesCount() {
        return likes_count;
    }

    public int getBucketsCount() {
        return buckets_count;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public User getUser() {
        return user;
    }

    public boolean isLiked() {
        return liked;
    }

    public boolean isBucketed() {
        return bucketed;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHtmlUrl(String html_url) {
        this.html_url = html_url;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setImages(Map<String, String> images) {
        this.images = images;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public void setViewsCount(int views_count) {
        this.views_count = views_count;
    }

    public void setLikesCount(int likes_count) {
        this.likes_count = likes_count;
    }

    public void setBucketsCount(int buckets_count) {
        this.buckets_count = buckets_count;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setBucketed(boolean bucketed) {
        this.bucketed = bucketed;
    }
}

