package com.rammstein.messenger.model.local;

import com.rammstein.messenger.model.web.response.MessageResponse;
import com.rammstein.messenger.util.SimpleDateUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user on 17.05.2017.
 */

public class Message extends RealmObject {
    public final static String TIME_IN_MILLS = "mTimeInMills";
    public static final String ID = "mId";
    @PrimaryKey
    private int mId;
    private int mSenderId;
    private String mMessage;
    private long mTimeInMills;

    public Message(){

    }

    public Message(int id, int senderId, String message, long timeInMills) {
        this.mId = id;
        this.mSenderId = senderId;
        this.mMessage = message;
        this.mTimeInMills = timeInMills;
    }

    public Message(MessageResponse response){
        mId = response.getId();
        mSenderId = response.getSender().getId();
        mMessage = response.getContent();
        mTimeInMills = SimpleDateUtils.parseDateString(response.getTime()).getTime();
    }

    public int getSenderId() {
        return mSenderId;
    }

    public void setSenderId(int senderId) {
        mSenderId = senderId;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public long getTimeInMills() {
        return mTimeInMills;
    }

    public void setTimeInMills(long timeInMills) {
        mTimeInMills = timeInMills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return mId == message.mId;

    }

    @Override
    public int hashCode() {
        return mId;
    }
}
