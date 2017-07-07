package com.rammstein.messenger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.util.RealmHelper;

import static com.rammstein.messenger.activity.MainActivity.ACTION_UPDATE_VIEW;

/**
 * Created by user on 04.07.2017.
 */

public class UserStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", -1);
        if (id == -1){
            return;
        }
        boolean online = intent.getBooleanExtra("online", false);
        RealmRepository realmRepository = RealmRepository.getInstance();
        AppUser appUser = RealmHelper.getCurrentUser();
        if (appUser != null){
            UserDetails user = appUser.getFriends().where().equalTo(UserDetails.ID, id).findFirst();
            if (user != null){
                realmRepository.beginTransaction();
                user.setOnline(online);
                realmRepository.commitTransaction();
            }
            Intent i = new Intent(ACTION_UPDATE_VIEW);
            context.sendBroadcast(i);
        }

    }
}
