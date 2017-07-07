package com.rammstein.messenger.application;

import android.app.Application;
import android.util.Log;

import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.repository.SharedPreferencesRepository;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;

/**
 * Created by user on 19.06.2017.
 */

public class Messenger extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Application", "create");
        RealmRepository.createInstance(this);
        SharedPreferencesRepository.createInstance(this);
        RetrofitHelper.createInstance(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RealmRepository.getInstance().closeRealm();
    }
}
