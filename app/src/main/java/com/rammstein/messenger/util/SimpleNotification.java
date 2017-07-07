package com.rammstein.messenger.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.rammstein.messenger.R;
import com.rammstein.messenger.activity.ChatActivity;
import com.rammstein.messenger.activity.MainActivity;
import com.rammstein.messenger.model.local.Chat;
import com.rammstein.messenger.repository.RealmRepository;

/**
 * Created by user on 03.07.2017.
 */

public class SimpleNotification {
    public static void showMessageNotification(Context context, int chatId, String title, String text, int tickerResId) {
        Intent notificationIntent = new Intent(context, ChatActivity.class);
        notificationIntent.putExtra(ChatActivity.CHAT_ID_EXTRA, chatId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder
                .setContentTitle(title)
                .setContentText(text)
                .setTicker(context.getString(tickerResId))
                .setSmallIcon(R.drawable.ic_message_white_24dp)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent).build();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

}
