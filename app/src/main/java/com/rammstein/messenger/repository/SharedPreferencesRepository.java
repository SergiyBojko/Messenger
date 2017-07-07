package com.rammstein.messenger.repository;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user on 22.06.2017.
 */

public class SharedPreferencesRepository {
    private static final String FILENAME = "shared_preferences.xml";
    private static final String CURRENT_USER_ID = "current_user_id";


    private static SharedPreferencesRepository sInstance;
    private SharedPreferences mPreferences;

    private SharedPreferencesRepository(Context context){
        mPreferences = context.getSharedPreferences(FILENAME, Context.MODE_APPEND);
    }

    public static void createInstance(Context context){
        if (sInstance == null){
            sInstance = new SharedPreferencesRepository(context);
        }
    }

    public static SharedPreferencesRepository getInstance (){
        return sInstance;
    }

    public int getCurrentUserId(){
        return mPreferences.getInt(CURRENT_USER_ID, -1);
    }

    public void setCurrentUserId(int currentUserId) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(CURRENT_USER_ID, currentUserId);
        editor.apply();
    }
}
