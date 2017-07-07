package com.rammstein.messenger.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.activity.ChatActivity;
import com.rammstein.messenger.activity.MainActivity;
import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.local.Chat;
import com.rammstein.messenger.model.web.response.ChatResponse;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.service.SignalRService;
import com.rammstein.messenger.util.RealmHelper;
import com.rammstein.messenger.util.SimpleNotification;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;

import static com.rammstein.messenger.activity.MainActivity.ACTION_UPDATE_VIEW;

/**
 * Created by user on 03.07.2017.
 */

public class ChatInviteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ChatResponse response = intent.getExtras().getParcelable("chat");
        RealmRepository realmRepository = RealmRepository.getInstance();
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Chat chat = new Chat(response);
        RealmHelper.addOrUpdateChat(response);
        retrofitHelper.updateChatMembers(null, response.getId());
        context.sendBroadcast(new Intent(ACTION_UPDATE_VIEW));
        SimpleNotification.showMessageNotification(context, chat.getId(), context.getString(R.string.invited_to_chat) ,chat.getChatName(),R.string.invited_to_chat);
        Toast.makeText(context, "invited to chat!", Toast.LENGTH_SHORT).show();
    }
}
