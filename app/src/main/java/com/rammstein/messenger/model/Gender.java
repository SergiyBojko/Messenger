package com.rammstein.messenger.model;

import com.rammstein.messenger.R;

/**
 * Created by user on 20.05.2017.
 */

public enum Gender{
    MALE(0, R.string.male), FEMALE(1, R.string.female), OTHER(2, R.string.other);
    private int mId;
    private int mTextResId;

    Gender(int id, int textId) {
        mId = id;
        mTextResId = textId;
    }

    public int getTextResId (){
        return mTextResId;

    }

    public int getId() {
        return mId;
    }
}
