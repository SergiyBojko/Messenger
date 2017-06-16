package com.rammstein.messenger.model;

import com.rammstein.messenger.model.core.Entity;

import java.util.ArrayList;

/**
 * Created by user on 17.05.2017.
 */

public class Chat implements Entity {
    private int mId;
    private String mChatName;
    private ArrayList<UserDetails> mChatMembers;
    private ArrayList<Message> mMessages;
    private String mChatPictureURI;

    public Chat(int id, String chatName) {
        mId = id;
        mChatName = chatName;
        mChatMembers = new ArrayList<>();
        mMessages = new ArrayList<>();
    }

    public int getId() {
        return mId;
    }

    public String getChatName() {
        return mChatName;
    }

    public ArrayList<UserDetails> getChatMembers() {
        return mChatMembers;
    }

    public ArrayList<Message> getMessages() {
        return mMessages;
    }

    public String getChatPictureURI() {
        return mChatPictureURI;
    }

    public void setChatName(String chatName) {
        mChatName = chatName;
    }

    public void setChatPictureURI(String chatPictureURI) {
        mChatPictureURI = chatPictureURI;
    }
}
