package com.rammstein.messenger.model.local;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user on 20.05.2017.
 */

public class AppUser extends RealmObject{
    @PrimaryKey
    private int mId;
    private UserDetails mUserDetails;
    private RealmList<UserDetails> mFriends;
    private RealmList<Chat> mChats;
    private String mAccessToken;
    private String mPassword;

    public AppUser(){
        mFriends = new RealmList<>();
        mChats = new RealmList<>();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public AppUser(UserDetails userDetails) {
        this();
        mUserDetails = userDetails;
    }

    public UserDetails getUserDetails() {
        return mUserDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        mUserDetails = userDetails;
    }

    public RealmList<UserDetails> getFriends() {
        return mFriends;
    }

    public RealmList<Chat> getChats() {
        return mChats;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }
}
