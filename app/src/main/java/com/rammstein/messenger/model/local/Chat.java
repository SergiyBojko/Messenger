package com.rammstein.messenger.model.local;

import android.support.annotation.NonNull;

import com.rammstein.messenger.model.web.response.ChatResponse;
import com.rammstein.messenger.util.RealmHelper;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user on 17.05.2017.
 */

public class Chat extends RealmObject implements Comparable<Chat>{
    public static final String ID = "mId";
    public static String IS_PRIVATE = "mIsDialog";
    public static String CHAT_MEMBER_ID = "mChatMembers.mId";
    @PrimaryKey
    private int mId;
    private String mChatName;
    private RealmList<UserDetails> mChatMembers;
    private RealmList<Message> mMessages;
    private boolean mIsDialog;
    private int mCreatorId;
    private int mTemperature;
    private long mLastSeenMessageTime;

    public Chat(){
        mChatMembers = new RealmList<>();
        mMessages = new RealmList<>();
        mIsDialog = false;
    }

    public Chat(int id, String chatName) {
        this();
        mId = id;
        mChatName = chatName;
    }

    public Chat(int id, String chatName, boolean isDialog, int creatorId, int temperature) {
        this();
        mId = id;
        mChatName = chatName;
        mIsDialog = isDialog;
        mCreatorId = creatorId;
        mTemperature = temperature;
    }

    public Chat(ChatResponse response){
        this();
        mId = response.getId();
        mChatName = response.getName();
        if (response.getCreator() != null){
            mCreatorId = response.getCreator().getId();
        }
        mIsDialog = response.getIsDialog();
        mTemperature = response.getTemperature();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getChatName() {
        AppUser appUser = RealmHelper.getCurrentUser();
        if (mIsDialog){
            for (UserDetails userDetails :mChatMembers){
                if (userDetails.getId() != appUser.getId()){
                    return userDetails.getName();
                }
            }
        }
        return mChatName;
    }

    public RealmList<UserDetails> getChatMembers() {
        return mChatMembers;
    }

    public RealmList<Message> getMessages() {
        return mMessages;
    }


    public void setChatName(String chatName) {
        mChatName = chatName;
    }


    public boolean isDialog() {
        return mIsDialog;
    }

    public void setIsDialog(boolean dialog) {
        mIsDialog = dialog;
    }

    public int getCreatorId() {
        return mCreatorId;
    }

    public void setCreatorId(int creatorId) {
        mCreatorId = creatorId;
    }

    public void setDialog(boolean dialog) {
        mIsDialog = dialog;
    }

    public int getTemperature() {
        return mTemperature;
    }

    public void setTemperature(int temperature) {
        mTemperature = temperature;
    }

    public long getLastSeenMessageTime() {
        return mLastSeenMessageTime;
    }

    public void setLastSeenMessageTime(long lastSeenMessageTime) {
        mLastSeenMessageTime = lastSeenMessageTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chat chat = (Chat) o;

        return mId == chat.mId;

    }

    @Override
    public int hashCode() {
        return mId;
    }

    @Override
    public int compareTo(@NonNull Chat o) {
        int comparison = 0;
        long last1;
        long last2;
        if (this.getMessages().size() > 0){
            last1 = this.getMessages().where().max(Message.TIME_IN_MILLS).longValue();
        } else {
            //TODO add chat creation time
            last1 = System.currentTimeMillis();
        }

        if (o.getMessages().size() > 0){
            last2 = o.getMessages().where().max(Message.TIME_IN_MILLS).longValue();
        } else {
            //TODO add chat creation time
            last2 = System.currentTimeMillis();
        }

        long diff = last2 - last1;

        if (diff > 0){
            comparison = 1;
        }
        if (diff < 0){
            comparison = -1;
        }
        return comparison;
    }
}
