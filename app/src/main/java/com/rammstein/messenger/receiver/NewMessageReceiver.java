package com.rammstein.messenger.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.activity.ChatActivity;
import com.rammstein.messenger.activity.MainActivity;
import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.local.Chat;
import com.rammstein.messenger.model.local.Message;
import com.rammstein.messenger.model.web.response.MessageResponse;
import com.rammstein.messenger.model.web.response.SenderResponse;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.util.RealmHelper;
import com.rammstein.messenger.util.SimpleNotification;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;

import static com.rammstein.messenger.activity.MainActivity.ACTION_UPDATE_VIEW;

/**
 * Created by user on 02.07.2017.
 */

public class NewMessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("NewMessageReceiver", "start");
        RealmRepository realmRepository = RealmRepository.getInstance();
        MessageResponse messageResponse = intent.getParcelableExtra("message");
        Chat chat = realmRepository.getById(Chat.class, messageResponse.getChatId());
        Message message = new Message(messageResponse);
        AppUser appUser = RealmHelper.getCurrentUser();
        realmRepository.beginTransaction();
        chat.getMessages().add(message);
        realmRepository.commitTransaction();
        SenderResponse senderResponse = messageResponse.getSender();
        if (appUser.getId() != senderResponse.getId()){
            String title = context.getResources().getString(R.string.app_name);
            String text =  String.format("%s %s : %s", senderResponse.getFirstName(), senderResponse.getLastName(), messageResponse.getContent());
            SimpleNotification.showMessageNotification(context, messageResponse.getChatId(), title, text , R.string.new_message);
        }
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        retrofitHelper.getOrUpdateChat(null, messageResponse.getChatId());
        context.sendBroadcast(new Intent(ACTION_UPDATE_VIEW));
        Log.i("NewMessageReceiver", "end");
    }
}
