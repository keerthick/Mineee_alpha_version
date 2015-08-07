package com.mineee.modal;

/**
 * Created by keerthick on 4/4/2015.
 */
public class FeedRowData {

    //private int id;
    private String timeStamp;

    private String profilePhoto;
    private int feedUserId;
    private String name;
    private String unique_Name;
    private String prodImageName;
    private String image_path;
    private String productName;
    private String likeCount;
    private String description;
    private String optionString;
    private boolean isUserLiked;
    private String upId;

    private FeedLikeUnlikeModel likeData;


    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public int getFeedUserId() {
        return feedUserId;
    }

    public void setFeedUserId(int feedUserId) {
        this.feedUserId = feedUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnique_Name() {
        return unique_Name;
    }

    public void setUnique_Name(String unique_Name) {
        this.unique_Name = unique_Name;
    }

    public String getProdImageName() {
        return prodImageName;
    }

    public void setProdImageName(String prodImageName) {
        this.prodImageName = prodImageName;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOptionString() {
        return optionString;
    }

    public void setOptionString(String optionString) {
        int parsedOption = Integer.parseInt(optionString);
        switch (parsedOption){
            case 1:
                this.optionString = "had";
                break;
            case 2:
                this.optionString = "has";
                break;
            case 3:
                this.optionString = "wants";
                break;
            case 4:
                this.optionString = "recommends";
                break;
        }


    }


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

    public FeedLikeUnlikeModel getLikeData() {
        return likeData;
    }

    public void setLikeData(FeedLikeUnlikeModel likeData) {
        this.likeData = likeData;
    }

    @Override
    public String toString() {
        return "FeedRowData{" +
                "timeStamp='" + timeStamp + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", feedUserId=" + feedUserId +
                ", name='" + name + '\'' +
                ", unique_Name='" + unique_Name + '\'' +
                ", prodImageName='" + prodImageName + '\'' +
                ", image_path='" + image_path + '\'' +
                ", productName='" + productName + '\'' +
                ", likeCount='" + likeCount + '\'' +
                ", description='" + description + '\'' +
                ", optionString='" + optionString + '\'' +
                ", isUserLiked=" + isUserLiked +
                ", upId='" + upId + '\'' +
                '}';
    }
}
