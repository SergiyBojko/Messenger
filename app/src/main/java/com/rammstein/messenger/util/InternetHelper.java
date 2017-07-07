package com.rammstein.messenger.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.InetAddress;

import static com.rammstein.messenger.activity.MainActivity.STOP_SIGNALR_SERVICE;

/**
 * Created by user on 17.06.2017.
 */

public class InternetHelper {
    public final static String START_UPDATE = "com.rammstein.messenger.START_UPDATE";
    private static long sessionId;
    private static boolean isConnected;

    public static void checkInternetConnection(final Context context){
        new Thread(){
            @Override
            public void run() {
                super.run();
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
                    try {
                        InetAddress ipAddr = InetAddress.getByName("www.google.com");
                        if (!ipAddr.equals("")){
                            sessionId = System.currentTimeMillis();
                            isConnected = true;
                        } else {
                            isConnected =  false;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        isConnected =  false;
                    }

                } else {
                    isConnected = false;
                }
                if (isConnected){
                    Intent i = new Intent(START_UPDATE);
                    context.sendBroadcast(i);
                } else {
                    Intent i = new Intent(STOP_SIGNALR_SERVICE);
                    context.sendBroadcast(i);
                }
                Log.i("Internet", "is connected = " + isConnected);
            }
        }.start();
    }

    public static long getSessionId() {
        return sessionId;
    }

    public static boolean isConnected() {
        return isConnected;
    }
}
