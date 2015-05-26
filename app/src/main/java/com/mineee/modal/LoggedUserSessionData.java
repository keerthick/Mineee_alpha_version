package com.mineee.modal;

/**
 * Created by keerthick on 5/2/2015.
 */
public class LoggedUserSessionData {

   // id":"346","name":"Savitha Ankegowda","gender":"female","oauth_username":"Savitha Ankegowda","oauth_uid":null,"uniquename":"savitha.ankegowda","provider":"1","lastLoggedIn":"2015-05-02 11:35:00","followersNotify":"0","likesNotify":"0"

    private String loggedUserId;
    private String name;
    private String gender;
    private String provider;

    public String getLoggedUserId() {
        return loggedUserId;
    }

    public void setLoggedUserId(String loggedUserId) {
        this.loggedUserId = loggedUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
