package com.rammstein.messenger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rammstein.messenger.util.InternetHelper;

/**
 * Created by user on 18.06.2017.
 */

public class ConnectivityActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        InternetHelper.checkInternetConnection(context);
    }
}
