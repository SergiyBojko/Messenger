package com.rammstein.messenger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.service.SignalRService;
import com.rammstein.messenger.util.RealmHelper;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;

import static com.rammstein.messenger.service.SignalRService.START_SIGNALR_SERVICE;

/**
 * Created by user on 01.07.2017.
 */

public class StartUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("StartUpdateReceiver", "start!");
        RetrofitHelper retrofitHelper = new RetrofitHelper(context);
        AppUser appUser = RealmHelper.getCurrentUser();
        if (appUser != null){
            retrofitHelper.updateAppUserToken();
            retrofitHelper.addOrUpdateUser(null, appUser.getId());
            retrofitHelper.updateAppUserFriends(null);
            if (!SignalRService.isRunning){
                retrofitHelper.updateAppUserChatsFull(null);
            } else {
                restartSignalr(context);
            }
        }
    }

    private void restartSignalr(Context context){
        Intent startService = new Intent(START_SIGNALR_SERVICE);
        context.sendBroadcast(startService);
    }
}
