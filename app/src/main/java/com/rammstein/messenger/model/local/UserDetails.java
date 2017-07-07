package com.rammstein.messenger.model.local;

import com.rammstein.messenger.model.web.response.UserDetailsResponse;
import com.rammstein.messenger.util.SimpleDateUtils;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user on 12.05.2017.
 */

public class UserDetails extends RealmObject {
    public final static String FIRST_NAME = "mFirstName";
    public final static String LAST_NAME = "mLastName";
    public static final String ID = "mId";

    @PrimaryKey
    private int mId;
    private String mUsername;
    private String mFirstName;
    private String mLastName;
    private Date mBirthday;
    private String mGender;
    private Chat mPrivateChat;
    private Date mLastModif;
    private Date mRegistrationDate;
    private boolean isOnline;

    public UserDetails(){

    }

    public UserDetails(int id, String firstName, String lastName) {
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
    }

    public UserDetails(int id, String firstName, String lastName, Date birthday, Gender gender){
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
        mBirthday = birthday;
        mGender = gender.name();
    }

    public UserDetails(UserDetailsResponse response){
        mId = response.getId();
        mFirstName = response.getFirstName();
        mLastName = response.getLastName();
        mLastModif = SimpleDateUtils.parseDateString(response.getLastModif());
        Gender gender = response.getGenderEnum();
        if (gender != null){
            mGender = gender.name();
        }
        mBirthday = SimpleDateUtils.parseDateString(response.getDateOfBirth());
        mRegistrationDate = SimpleDateUtils.parseDateString(response.getRegDate());
    }

    public int getId() {
        return mId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername (String username){
        mUsername = username;
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

    public Date getBirthday() {
        return mBirthday;
    }

    public void setBirthday(Date birthday) {
        mBirthday = birthday;
    }

    public Gender getGender() {
        if (mGender != null){
            return Gender.valueOf(mGender);
        } else {
            return null;
        }
    }

    public void setGender(Gender gender) {
        if (gender == null){
            mGender = null;
        } else {
            mGender = gender.name();
        }
    }

    public Chat getPrivateChat() {
        return mPrivateChat;
    }

    public void setPrivateChat(Chat privateChat) {
        this.mPrivateChat = privateChat;
    }


    public long getLastModif() {
        if (mLastModif != null){
            return mLastModif.getTime();
        } else {
            return 0;
        }
    }

    public void setLastModif(Date lastModif) {
        this.mLastModif = lastModif;
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

    public void setRegistrationDate(Date registrationDate) {
        mRegistrationDate = registrationDate;
    }

    public Date getRegistrationDate() {
        return mRegistrationDate;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}