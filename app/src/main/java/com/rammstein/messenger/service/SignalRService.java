package com.rammstein.messenger.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.local.Chat;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.model.web.response.ChatResponse;
import com.rammstein.messenger.model.web.response.MessageResponse;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.util.RealmHelper;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;

import static com.rammstein.messenger.activity.AddChatMembersActivity.CHAT_ID;
import static com.rammstein.messenger.activity.MainActivity.ACTION_UPDATE_VIEW;
import static com.rammstein.messenger.activity.MainActivity.STOP_SIGNALR_SERVICE;

/**
 * Created by user on 28.06.2017.
 */

public class SignalRService extends Service {
    private static final String TAG = "signalr_service";
    private static final String SERVER_URL = "http://andriidemkiv-001-site1.dtempurl.com/signalr";
    private static final String CHAT_HUB = "ChatHub";
    private static final String NEW_MESSAGE = "addNewMessageToPage";
    public static final String NEW_MESSAGE_RECEIVED = "com.rammstein.messenger.ACTION_NEW_MESSAGE_RECEIVED";
    public static final String INVITED_TO_CHAT = "com.rammstein.messenger.INVITED_TO_CHAT";
    public static final String START_SIGNALR_SERVICE = "com.rammstein.messenger.START_SIGNALR_SERVICE";
    private static final String CHECK_USERS_ONLINE = "check_users_online";
    private static final String USER_STATUS = "com.rammstein.messenger.USER_STATUS";
    public static final String TYPING = "typing";
    public static final String USER_ID = "user_id";
    public static final String APP_USER_TYPING = "app_user_typing";
    public static boolean isRunning;

    private HubConnection mHubConnection;
    private HubProxy mHubProxy;
    private Handler mHandler; // to display Toast message
    private final IBinder mBinder = new LocalBinder(); // Binder given to clients
    private BroadcastReceiver mStopServiceReceiver;
    private BroadcastReceiver mCheckUsersOnline;
    private BroadcastReceiver mAppUserTypingReceiver;
    private boolean mAutorestart;
    private AppUser mAppUser;

    public SignalRService() {
        Log.i(TAG, "constructor");
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "create");
        super.onCreate();
        mAutorestart = true;
        mHandler = new Handler(Looper.getMainLooper());
        mStopServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mAutorestart = false;
                stopSelf();
            }
        };
        mCheckUsersOnline = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkUsersOnline();
            }
        };
        mAppUserTypingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mHubProxy.invoke("TypeMessage", mAppUser.getId(), intent.getIntExtra(CHAT_ID, -1));
            }
        };
        IntentFilter stopSignalrFilter = new IntentFilter(STOP_SIGNALR_SERVICE);
        IntentFilter checkUsersFilter = new IntentFilter(CHECK_USERS_ONLINE);
        IntentFilter appUserTyping = new IntentFilter(APP_USER_TYPING);
        registerReceiver(mStopServiceReceiver, stopSignalrFilter);
        registerReceiver(mCheckUsersOnline, checkUsersFilter);
        registerReceiver(mAppUserTypingReceiver, appUserTyping);
    }

    public void checkUsersOnline() {
        for (UserDetails user : mAppUser.getFriends()){
            mHubProxy.invoke("UpdateStatus", user.getId());
        }
    }

    public void checkUserOnline(int id){
        mHubProxy.invoke("UpdateStatus", id);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "start command");
        int result = super.onStartCommand(intent, flags, startId);
        startSignalR();
        return result;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "task removed");
        if (mAutorestart){
            scheduleServiceRestart();
        }
        unregisterReceiver(mStopServiceReceiver);
        //mHubConnection.stop();
        super.onTaskRemoved(rootIntent);

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "destroy");
        if (mAutorestart){
            scheduleServiceRestart();
        }
        unregisterReceiver(mStopServiceReceiver);
        unregisterReceiver(mCheckUsersOnline);
        unregisterReceiver(mAppUserTypingReceiver);
        mHubProxy.invoke("Disconnect", mAppUser.getId());
        mHubConnection.stop();
        isRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SignalRService getService() {
            // Return this instance of SignalRService so clients can call public methods
            return SignalRService.this;
        }
    }
    
    private void scheduleServiceRestart(){
        Log.i(TAG, "scheduling service restart");
        Intent restartService = new Intent(START_SIGNALR_SERVICE);

        PendingIntent restartServicePI = PendingIntent.getBroadcast(
                getApplicationContext(), 0, restartService, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmService = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, restartServicePI);
    }


    private void startSignalR() {
        Log.i(TAG, "starting signalr");
        isRunning = true;
        mAppUser = RealmHelper.getCurrentUser();
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        final String token = mAppUser.getAccessToken();
        Log.i(TAG, "token " + token);
        Log.i(TAG, "user name" + mAppUser.getUserDetails().getName());

        Credentials credentials = new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                request.addHeader("Authorization", token);
            }
        };

        if (mHubConnection != null && mHubConnection.getState() == ConnectionState.Connected){
            mHubConnection.disconnect();
        }

        mHubConnection = new HubConnection(SERVER_URL);
        mHubConnection.setCredentials(credentials);
        mHubProxy = mHubConnection.createHubProxy(CHAT_HUB);
        //ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
        //SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);
        SignalRFuture<Void> signalRFuture = mHubConnection.start();


        try {
            signalRFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }

        mHubProxy.invoke("Connect", mAppUser.getId());

        for (Chat chat : mAppUser.getChats()){
            if (chat.getChatMembers().contains(mAppUser.getUserDetails())){
                Log.i("subscribe", chat.getChatName());
                mHubProxy.invoke("SubscribeOnChat", mAppUser.getId(), chat.getId());
            }
        }

        for (UserDetails user : mAppUser.getFriends()){
            mHubProxy.invoke("UpdateStatus", user.getId());
        }

        mHubProxy.on(NEW_MESSAGE,
                new SubscriptionHandler1<MessageResponse>() {
                    @Override
                    public void run(final MessageResponse msg) {
                        Log.i(TAG, "received!");
                        Intent i = new Intent(NEW_MESSAGE_RECEIVED);
                        i.putExtra("message", msg);
                        sendBroadcast(i);
                    }
                }
                , MessageResponse.class);
        mHubProxy.on("AddChatToPage",
                new SubscriptionHandler1<ChatResponse>() {
                    @Override
            public void run(ChatResponse response) {
                Log.i(TAG, "invited to chat");
                Intent i = new Intent(INVITED_TO_CHAT);
                i.putExtra("chat", response);
                sendBroadcast(i);

            }
        }, ChatResponse.class);
        mHubProxy.on("updateStatus", new SubscriptionHandler2<Integer, Boolean>() {
            @Override
            public void run(Integer i, Boolean b) {
                Log.i (TAG, "status user : " + i + " online: " +b);
                Intent intent = new Intent(USER_STATUS);
                intent.putExtra("id", i);
                intent.putExtra("online", b);
                sendBroadcast(intent);
            }
        }, Integer.class, Boolean.class);
        mHubProxy.on("notifyTyping", new SubscriptionHandler2<Integer, Integer>() {
            @Override
            public void run(Integer userId, Integer chatId) {
                Intent i = new Intent(TYPING);
                i.putExtra(CHAT_ID, chatId);
                i.putExtra(USER_ID, userId);
                sendBroadcast(i);

            }
        }, Integer.class, Integer.class);
    }
}
