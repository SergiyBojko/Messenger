package com.rammstein.messenger.model.local;

import com.rammstein.messenger.R;

/**
 * Created by user on 20.05.2017.
 */

public enum Gender{
    MALE(R.string.male), FEMALE(R.string.female);

    private int mTextResId;

    Gender(int textId) {
        mTextResId = textId;
    }

    public int getTextResId (){
        return mTextResId;
    }
}
