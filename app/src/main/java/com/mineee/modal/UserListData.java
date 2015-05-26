package com.mineee.modal;

/**
 * Created by keerthick on 4/30/2015.
 */
public class UserListData {

    private String userId;
    private String name;
    private String profPicName;

    //{"id":"131","name":"Gaurav Jaiswal","uniquename":"gaurav.jaiswal","image_name":"gaurav.jaiswal.904_1393829922.jpeg"}


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfPicName() {
        return profPicName;
    }

    public void setProfPicName(String profPicName) {
        this.profPicName = profPicName;
    }
}
