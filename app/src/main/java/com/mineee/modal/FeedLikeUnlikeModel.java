package com.mineee.modal;

/**
 * Created by keerthick on 6/18/2015.
 */
public class FeedLikeUnlikeModel {

    private boolean isUserLiked;
    private String upId;
    private String likeCount;

    public boolean isUserLiked() {
        return isUserLiked;
    }

    public void setUserLiked(String isUserLiked) {
        if ("1".equals(isUserLiked))
            this.isUserLiked = true;
        else
            this.isUserLiked = false;
    }

    public String getUpId() {
        return upId;
    }

    public void setUpId(String upId) {
        this.upId = upId;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }
}
