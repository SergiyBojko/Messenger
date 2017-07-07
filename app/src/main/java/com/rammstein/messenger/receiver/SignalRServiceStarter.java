package com.rammstein.messenger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.rammstein.messenger.repository.SharedPreferencesRepository;
import com.rammstein.messenger.service.SignalRService;
import com.rammstein.messenger.util.RealmHelper;

/**
 * Created by user on 28.06.2017.
 */

public class SignalRServiceStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("SignalRServiceStarter", "onReceive");
        if (RealmHelper.getCurrentUser() != null){
            Log.i("SignalRServiceStarter", "starting signalr");
            context.startService(new Intent(context, SignalRService.class));
        }
    }
}
