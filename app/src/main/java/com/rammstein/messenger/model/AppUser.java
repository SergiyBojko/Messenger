package com.rammstein.messenger.model;

import com.rammstein.messenger.model.core.Entity;

import java.util.Set;

/**
 * Created by user on 20.05.2017.
 */

public class AppUser implements Entity {
    private String id;
    private UserDetails mUserDetails;
    private Set<UserDetails> mFriends;
    private Set<Chat> mChats;
    private String mAccessToken;

    public AppUser(UserDetails userDetails) {
        mUserDetails = userDetails;
    }

    public UserDetails getUserDetails() {
        return mUserDetails;
    }

    public Set<UserDetails> getFriends() {
        return mFriends;
    }

    public Set<Chat> getChats() {
        return mChats;
    }

    public String getAccessToken() {
        return mAccessToken;
    }
}
