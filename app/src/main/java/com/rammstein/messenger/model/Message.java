package com.rammstein.messenger.model;

import com.rammstein.messenger.model.core.Entity;

/**
 * Created by user on 17.05.2017.
 */

public class Message implements Entity {
    private int mId;
    private int mChatId;
    private int mSenderId;
    private String mMessage;
    private long mTimeInMills;
    private boolean mSeen;

    public Message(int id, int chatId, int senderId, String message, long timeInMills) {
        this.mId = id;
        this.mChatId = chatId;
        this.mSenderId = senderId;
        this.mMessage = message;
        this.mTimeInMills = timeInMills;
        mSeen = false;
    }

    public int getId() {
        return mId;
    }

    public int getChatId() {
        return mChatId;
    }

    public int getSenderId() {
        return mSenderId;
    }

    public String getText() {
        return mMessage;
    }

    public long getTimeInMills() {
        return mTimeInMills;
    }

    public boolean isSeen() {
        return mSeen;
    }

    public void setSeen(boolean seen) {
        mSeen = seen;
    }
}
