package com.rammstein.messenger.model;

import android.util.Log;

import com.rammstein.messenger.model.core.Entity;

/**
 * Created by user on 12.05.2017.
 */

public class UserDetails implements Entity {
    private int mId;
    private String mUsername = "test_username321";
    private String mFirstName;
    private String mLastName;
    private long mBirthday = System.currentTimeMillis();
    private Gender mGender = Gender.FEMALE;
    private Chat privateChat;
    private boolean inContactList;

    public UserDetails(int id, String firstName, String lastName) {
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
    }

    public UserDetails(int id, String firstName, String lastName, long birthday, Gender gender){
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
        mBirthday = birthday;
        mGender = gender;
    }

    public int getId() {
        return mId;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public long getBirthday() {
        return mBirthday;
    }

    public void setBirthday(long birthday) {
        mBirthday = birthday;
    }

    public Gender getGender() {
        return mGender;
    }

    public void setGender(Gender gender) {
        mGender = gender;
    }

    public Chat getPrivateChat() {
        return privateChat;
    }

    public void setPrivateChat(Chat privateChat) {
        this.privateChat = privateChat;
    }

    public boolean isInContactList() {
        return inContactList;
    }

    public void setInContactList(boolean inContactList) {
        this.inContactList = inContactList;
    }

    public String getName() {
        String name;
        if (mFirstName == null && mLastName == null){
            name = mUsername;
        } else {
            name = String.format("%s %s", mFirstName, mLastName);
            name = name.trim();
        }
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        if (!(obj instanceof UserDetails)){
            return false;
        }
        UserDetails userDetails = (UserDetails) obj;
        return this.mId == userDetails.getId();
    }
}