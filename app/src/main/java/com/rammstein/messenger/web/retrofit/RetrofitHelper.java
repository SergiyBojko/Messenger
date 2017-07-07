package com.rammstein.messenger.web.retrofit;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.fragment.dialog.NotificationDialog;
import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.local.Chat;
import com.rammstein.messenger.model.local.Message;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.model.web.request.CreateChatRequest;
import com.rammstein.messenger.model.web.request.RegisterRequest;
import com.rammstein.messenger.model.web.request.SendMessageRequest;
import com.rammstein.messenger.model.web.response.ChatResponse;
import com.rammstein.messenger.model.web.response.MessageResponse;
import com.rammstein.messenger.model.web.response.TokenResponse;
import com.rammstein.messenger.model.web.response.UserDetailsResponse;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.service.SignalRService;
import com.rammstein.messenger.util.InternetHelper;
import com.rammstein.messenger.util.RealmHelper;
import com.rammstein.messenger.util.SimpleDateUtils;
import com.rammstein.messenger.web.retrofit.api.AccountApi;
import com.rammstein.messenger.web.retrofit.api.Api;
import com.rammstein.messenger.web.retrofit.api.ChatApi;
import com.rammstein.messenger.web.retrofit.api.FriendApi;
import com.rammstein.messenger.web.retrofit.api.ImageApi;
import com.rammstein.messenger.web.retrofit.api.MessageApi;
import com.rammstein.messenger.web.retrofit.api.UserApi;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.rammstein.messenger.activity.ChatActivity.CHAT_CREATED;
import static com.rammstein.messenger.activity.MainActivity.ACTION_UPDATE_VIEW;
import static com.rammstein.messenger.service.SignalRService.START_SIGNALR_SERVICE;

/**
 * Created by user on 27.06.2017.
 */

public class RetrofitHelper {
    private static RetrofitHelper sInstance;
    private String baseApiUrl = "http://andriidemkiv-001-site1.dtempurl.com/";
    private Api mApi;
    private AccountApi mAccountApi;
    private ChatApi mChatApi;
    private UserApi mUserApi;
    private final ImageApi mImageApi;
    private FriendApi mFriendApi;
    private MessageApi mMessageApi;
    private Context mContext;

    public RetrofitHelper(){
        Log.i("retrofit", "new retrofit helper");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseApiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApi = retrofit.create(Api.class);
        mAccountApi = retrofit.create(AccountApi.class);
        mChatApi = retrofit.create(ChatApi.class);
        mUserApi = retrofit.create(UserApi.class);
        mImageApi = retrofit.create(ImageApi.class);
        mFriendApi = retrofit.create(FriendApi.class);
        mMessageApi = retrofit.create(MessageApi.class);
        Log.i("retrofit", "new retrofit helper2");
    }

    public RetrofitHelper(Context context) {
        this();
        mContext = context;
        Log.i("retrofit", "new retrofit helper3");
    }

    public static void createInstance (Context context){
        if (sInstance == null){
            sInstance = new RetrofitHelper(context);
        }
    }

    public static RetrofitHelper getInstance(){
        return sInstance;
    }

    public String getBaseApiUrl() {
        return baseApiUrl;
    }

    public void register (RegisterRequest registerRequest, Callback<Void> callback){
        Call<Void> call = mAccountApi.register(registerRequest);
        call.enqueue(callback);
    }

    public void getUserProfile (Callback<UserDetailsResponse> callback){
        Call<UserDetailsResponse> call = mAccountApi.getUserProfile(RealmHelper.getCurrentUser().getAccessToken());
        call.enqueue(callback);
    }

    public void getUserProfile (String token, Callback<UserDetailsResponse> callback){
        Call<UserDetailsResponse> call = mAccountApi.getUserProfile(token);
        call.enqueue(callback);
    }

    public void getToken (String username, String password, Callback<TokenResponse> callback){
        Call<TokenResponse> call = mApi.getToken("password", username, password);
        call.enqueue(callback);
    }

    public void getToken(Callback<TokenResponse> callback) {
        String username = RealmHelper.getCurrentUser().getUserDetails().getUsername();
        String password = RealmHelper.getCurrentUser().getPassword();
        getToken(username, password, callback);
    }


    public void getAllUsers(Map<String, String> filters, Callback<List<UserDetailsResponse>> callback){
        Call<List<UserDetailsResponse>> call = mUserApi.getUsers(RealmHelper.getCurrentUser().getAccessToken(), filters);
        call.enqueue(callback);
    }

    public void createChat (int [] memberIds, String name, boolean isDialog, Callback<Integer> callback) throws JSONException {
        CreateChatRequest chatRequest = new CreateChatRequest(name, memberIds, isDialog);
        Call<Integer> call = mChatApi.createChat(RealmHelper.getCurrentUser().getAccessToken(), chatRequest);
        call.enqueue(callback);
    }

    public void getChats (Callback<List<ChatResponse>> callback){
        Call<List<ChatResponse>> call = mChatApi.getChats(RealmHelper.getCurrentUser().getAccessToken());
        call.enqueue(callback);
    }

    public void getChatById (int chatId, Callback<ChatResponse> callback){
        Call<ChatResponse> call = mChatApi.getChatById(RealmHelper.getCurrentUser().getAccessToken(), chatId);
        call.enqueue(callback);
    }

    public void getChatMessages (int chatId, int take, int skip, Callback<List<MessageResponse>> callback){
        Call<List<MessageResponse>> call = mChatApi.getChatMessages(RealmHelper.getCurrentUser().getAccessToken(), chatId, take, skip);
        call.enqueue(callback);
    }

    public void getChatMessages (int chatId, String minTime, Callback<List<MessageResponse>> callback){
        Call<List<MessageResponse>> call = mChatApi.getChatMessages(RealmHelper.getCurrentUser().getAccessToken(), chatId, minTime);
        call.enqueue(callback);
    }

    public void getAllChatMessages(int chatId, Callback<List<MessageResponse>> callback){
        getChatMessages(chatId, 0, 0, callback);
    }

    public void getChatMembers (int chatId, Callback<List<UserDetailsResponse>> callback){
        Call<List<UserDetailsResponse>> call = mChatApi.getChatMembers(RealmHelper.getCurrentUser().getAccessToken(), chatId);
        call.enqueue(callback);
    }

    public void addUserToChat (int chatId, int userId, Callback<Void> callback){
        Call<Void> call = mChatApi.addUserToChat(RealmHelper.getCurrentUser().getAccessToken(), chatId, userId);
        call.enqueue(callback);
    }

    public void removeUserFromChat (int chatId, int userId, Callback<Void> callback){
        Call<Void> call = mChatApi.removeUserFromChat(RealmHelper.getCurrentUser().getAccessToken(), chatId, userId);
        call.enqueue(callback);
    }

    public void deleteChatHistory (int chatId, Callback<Void> callback){
        Call<Void> call = mChatApi.deleteHistory(RealmHelper.getCurrentUser().getAccessToken(), chatId);
        call.enqueue(callback);
    }

    public void uploadImage (byte[] file, Callback<Void> callback){
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", Long.toString(System.currentTimeMillis())+".png", requestFile);
        Call<Void> call = mImageApi.uploadImage(RealmHelper.getCurrentUser().getAccessToken(), body);
        call.enqueue(callback);

    }
    
    public void getFriends (Callback<List<UserDetailsResponse>> callback){
        Call<List<UserDetailsResponse>> call = mFriendApi.getFriends(RealmHelper.getCurrentUser().getAccessToken());
        call.enqueue(callback);
    }
    
    public void addToContacts(int userId, Callback<Void> callback){
        Call<Void> call = mFriendApi.addFriend(RealmHelper.getCurrentUser().getAccessToken(), userId);
        call.enqueue(callback);
    }

    public void removeFriend (int userId, Callback<Void> callback){
        Call<Void> call = mFriendApi.removeFriend(RealmHelper.getCurrentUser().getAccessToken(), userId);
        call.enqueue(callback);
    }
    
    public void sendMessage (int chatId, String content, Callback<Integer> callback){
        SendMessageRequest request = new SendMessageRequest(chatId, content);
        mMessageApi.sendMessage(RealmHelper.getCurrentUser().getAccessToken(), request).enqueue(callback);
    }

    public void getUserById (int userId, Callback<UserDetailsResponse> callback){
        mUserApi.getUserById(RealmHelper.getCurrentUser().getAccessToken(), userId).enqueue(callback);
    }

    public void getPath (int userId, Callback<List<UserDetailsResponse>> callback){
        mUserApi.getPath(RealmHelper.getCurrentUser().getAccessToken(), userId).enqueue(callback);
    }

    public void addOrUpdateUser(final AppCompatActivity activity, int id) {
        getUserById(id, new CustomCallback<UserDetailsResponse>(mContext, activity) {
            @Override
            protected void onSuccess(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                RealmHelper.addOrUpdateUser(response.body(), false);
            }
        });
    }

    public void addToContacts(final AppCompatActivity activity, final int userId) {
        addToContacts(userId, new CustomCallback<Void>(mContext, activity) {
            @Override
            protected void onSuccess(Call<Void> call, Response<Void> response) {
                getUserById(userId, new CustomCallback<UserDetailsResponse>(mContext, activity) {
                    @Override
                    protected void onSuccess(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                        UserDetails user = new UserDetails(response.body());
                        RealmHelper.addUserToContacts(user);
                        mContext.sendBroadcast(new Intent(ACTION_UPDATE_VIEW));
                    }
                });
            }
        });
    }

    public void removeFromContacts(final AppCompatActivity activity, final int userId){
        removeFriend(userId, new CustomCallback<Void>(mContext, activity) {
            @Override
            protected void onSuccess(Call<Void> call, Response<Void> response) {
                UserDetails userToRemove = RealmRepository.getInstance().getById(UserDetails.class, userId);
                RealmHelper.removeContact(userToRemove);
                mContext.sendBroadcast(new Intent(ACTION_UPDATE_VIEW));
            }
        });
    }

    public void showPath(final AppCompatActivity activity, int userId) {
        getPath(userId, new CustomCallback<List<UserDetailsResponse>>(mContext, activity) {
            @Override
            protected void onSuccess(Call<List<UserDetailsResponse>> call, Response<List<UserDetailsResponse>> response) {
                List<UserDetailsResponse> path = response.body();
                for (UserDetailsResponse user: path) {
                    //TODO  show path in dialog
                    Log.i("shortest_path", user.getId() + " : " + user.getFirstName() + " " + user.getLastName());
                }
            }
        });

    }

    public void createDialogAndSendMessage(final AppCompatActivity activity, final UserDetails receiverUserDetails, final String messageText) {
        int[] member = {receiverUserDetails.getId()};
        try {
            createChat(member, receiverUserDetails.getName(), true, new CustomCallback<Integer>(mContext, activity) {
                @Override
                protected void onSuccess(Call<Integer> call, Response<Integer> response) {
                    int chatId = response.body();
                    RealmRepository repository = RealmRepository.getInstance();
                    repository.beginTransaction();
                    Chat chat = repository.getById(Chat.class, chatId);
                    if (chat == null){
                        chat = repository.getRealm().createObject(Chat.class, response.body());
                        chat.setChatName("");
                        chat.setIsDialog(true);
                        chat.setCreatorId(RealmHelper.getCurrentUser().getId());
                        chat.getChatMembers().add(RealmHelper.getCurrentUser().getUserDetails());
                        chat.getChatMembers().add(receiverUserDetails);
                        RealmHelper.getCurrentUser().getChats().add(chat);
                    }

                    repository.commitTransaction();
                    //TODO sign up to chat on signalr
                    Intent i = new Intent(CHAT_CREATED);
                    i.putExtra(Chat.ID, chatId);
                    mContext.sendBroadcast(i);
                    sendMessage(activity, chatId, messageText);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateUserMessages (final AppCompatActivity activity, final int take){
        getChats(new CustomCallback<List<ChatResponse>>(mContext, activity) {
            @Override
            protected void onSuccess(Call<List<ChatResponse>> call, Response<List<ChatResponse>> response) {
                for (ChatResponse chatResponse : response.body()){
                    updateChatMessages(activity, chatResponse.getId(), take);
                }
            }
        });
    }


    public void updateAppuserChats (final AppCompatActivity activity){
        getChats(new CustomCallback<List<ChatResponse>>(mContext, activity) {
            @Override
            protected void onSuccess(Call<List<ChatResponse>> call, Response<List<ChatResponse>> response) {
                for (ChatResponse chatResponse : response.body()){
                    RealmHelper.addOrUpdateChat(chatResponse);
                }
            }
        });
    }

    public void updateAppUserChatsFull(final AppCompatActivity activity){
        Log.i("updateAppUserChatsFull", "sending call to server");
        getChats(new CustomCallback<List<ChatResponse>>(mContext, activity) {
            @Override
            protected void onSuccess(Call<List<ChatResponse>> call, Response<List<ChatResponse>> response) {
                for (ChatResponse chatResponse : response.body()){
                    Log.i("updateAppUserChatsFull", chatResponse.toString());
                    RealmHelper.addOrUpdateChat(chatResponse);
                    RealmRepository repository = RealmRepository.getInstance();
                    Chat chat = repository.getById(Chat.class, chatResponse.getId());
                    String lastMessagedate = null;
                    if (!chat.getMessages().isEmpty()){
                        long lastMessageTime = chat.getMessages().where().max(Message.TIME_IN_MILLS).longValue();
                        lastMessagedate = SimpleDateUtils.formatDateLong(lastMessageTime);
                    }


                    updateChatMessages(activity, chatResponse.getId(), lastMessagedate);
                    updateChatMembers(activity, chatResponse.getId());
                }

                Intent startService = new Intent(START_SIGNALR_SERVICE);
                mContext.sendBroadcast(startService);
            }
        });
        Log.i("updateAppUserChatsFull", "call send");
    }

    public void getOrUpdateChat(final AppCompatActivity activity, final int chatId) {
        getChatById(chatId, new CustomCallback<ChatResponse>(mContext, activity) {
            @Override
            protected void onSuccess(Call<ChatResponse> call, Response<ChatResponse> response) {
                RealmHelper.addOrUpdateChat(response.body());
            }
        });
    }

    public void updateChatMessages(final AppCompatActivity activity, final int chatId, final int take) {
        getChatMessages(chatId, take, 0, new CustomCallback<List<MessageResponse>>(mContext, activity) {
            @Override
            protected void onSuccess(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                RealmRepository repository = RealmRepository.getInstance();
                Chat chat = repository.getById(Chat.class, chatId);
                RealmList<Message> messages = chat.getMessages();
                //repository.beginTransaction();
                //TODO don't delete all messages
                //messages.deleteAllFromRealm();
                //repository.commitTransaction();
                Log.i("updateChatMessages", "chat " + chat.getChatName());
                for (MessageResponse messageResponse : response.body()){
                    Log.i("updateChatMessages", "message " + messageResponse.getContent());
                    Message message = RealmHelper.addOrUpdateMessage(messageResponse);

                    repository.beginTransaction();
                    if (!messages.contains(message)){
                        messages.add(message);
                    }
                    repository.commitTransaction();

                }
            }
        });
    }


    public void updateChatMessages (final AppCompatActivity activity, final int chatId, final String minDate){
        getChatMessages(chatId, minDate, new CustomCallback<List<MessageResponse>>(mContext, activity) {
            @Override
            protected void onSuccess(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                Log.i("updateChatMessages", minDate + " " + response.body().size());
                if (response.body().size() == 0){
                    return;
                }
                RealmRepository repository = RealmRepository.getInstance();
                Chat chat = repository.getById(Chat.class, chatId);
                RealmList<Message> messages = chat.getMessages();
                for (MessageResponse messageResponse : response.body()){
                    Message message = RealmHelper.addOrUpdateMessage(messageResponse);

                    repository.beginTransaction();
                    if (!messages.contains(message)){
                        messages.add(message);
                    }
                    repository.commitTransaction();
                }
            }
        });
    }

    public void updateChatMessages(final AppCompatActivity activity, final int chatId) {
        //TODO download only new messages
        updateChatMessages(activity, chatId, 0);
    }

    public void updateChatMembers(final AppCompatActivity activity, final int chatId) {
        getChatMembers(chatId, new CustomCallback<List<UserDetailsResponse>>(mContext, activity) {
            @Override
            protected void onSuccess(Call<List<UserDetailsResponse>> call, Response<List<UserDetailsResponse>> response) {
                Log.i("updateChatMembers", chatId +"");
                RealmRepository repository = RealmRepository.getInstance();
                Chat chat = repository.getById(Chat.class, chatId);
                RealmList<UserDetails> members = chat.getChatMembers();
                repository.beginTransaction();
                members.clear();
                for (UserDetailsResponse userResponse : response.body()){
                    UserDetails managedUser = RealmHelper.addOrUpdateUserRaw(userResponse, true);
                    members.add(managedUser);
                }
                repository.commitTransaction();
            }
        });
    }

    public void sendMessage(final AppCompatActivity activity, final int chatId, String messageText) {
        sendMessage(chatId, messageText, new CustomCallback<Integer>(mContext, activity) {
            @Override
            protected void onSuccess(Call<Integer> call, Response<Integer> response) {
                Log.i("sendMessage", "success");
            }
        });
    }

    public void updateAppUserFriends (final AppCompatActivity activity){
        getFriends(new CustomCallback<List<UserDetailsResponse>>(mContext, activity) {
            @Override
            protected void onSuccess(Call<List<UserDetailsResponse>> call, Response<List<UserDetailsResponse>> response) {
                RealmRepository repository = RealmRepository.getInstance();
                repository.beginTransaction();
                RealmList<UserDetails> friends = RealmHelper.getCurrentUser().getFriends();
                friends.clear();
                repository.commitTransaction();
                for (UserDetailsResponse user : response.body()){
                    UserDetails managedUser = RealmHelper.addOrUpdateUser(user, true);
                    repository.beginTransaction();
                    friends.add(managedUser);
                    repository.commitTransaction();
                }
            }
        });
    }

    public void createGroupChat(final AppCompatActivity activity, final int[] memberIds, final String name){
        try {
            createChat(memberIds, name, false, new CustomCallback<Integer>(mContext, activity) {
                @Override
                protected void onSuccess(Call<Integer> call, Response<Integer> response) {
                    RealmRepository repository = RealmRepository.getInstance();
                    repository.beginTransaction();
                    Chat managedChat = repository.getRealm().createObject(Chat.class, response.body());
                    managedChat.setCreatorId(RealmHelper.getCurrentUser().getId());
                    managedChat.setIsDialog(false);
                    managedChat.setChatName(name);
                    for (int id : memberIds){
                        UserDetails user = repository.getById(UserDetails.class, id);
                        managedChat.getChatMembers().add(user);
                    }
                    managedChat.getChatMembers().add(RealmHelper.getCurrentUser().getUserDetails());
                    RealmHelper.getCurrentUser().getChats().add(managedChat);
                    repository.commitTransaction();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addMembersToChat(final AppCompatActivity activity, final int chatId, int [] memberIds){
        for (int memberId : memberIds){
            final int id = memberId;
            addUserToChat(chatId, memberId, new CustomCallback<Void>(mContext, activity) {
                @Override
                protected void onSuccess(Call<Void> call, Response<Void> response) {
                    if (response.code()/100 == 2){
                        RealmHelper.addUserToChat(id, chatId);
                    }
                }
            });
        }
    }

    public void removeUserFromChat (final AppCompatActivity activity, final int chatId, final int memberId){
        removeUserFromChat(chatId, memberId, new CustomCallback<Void>(mContext, activity) {
            @Override
            protected void onSuccess(Call<Void> call, Response<Void> response) {
                if (response.code()/100 == 2){
                    RealmHelper.removeUserFromChat(memberId, chatId);
                }
            }
        });
    }

    public void deleteChatHistory (final AppCompatActivity activity, final int chatId){
        deleteChatHistory(chatId, new CustomCallback<Void>(mContext, activity) {
            @Override
            protected void onSuccess(Call<Void> call, Response<Void> response) {
                if (response.code()/100 == 2){
                    RealmHelper.deleteChatHistory(chatId);
                }
            }
        });
    }

    public void updateAppUserToken() {
        getToken(new CustomCallback<TokenResponse>(mContext, null) {
            @Override
            protected void onSuccess(Call<TokenResponse> call, Response<TokenResponse> response) {
                RealmRepository realmRepository = RealmRepository.getInstance();
                realmRepository.beginTransaction();
                String token = String.format("%s %s", response.body().getToken_type(), response.body().getAccess_token());
                RealmHelper.getCurrentUser().setAccessToken(token);
                realmRepository.commitTransaction();
            }
        });
    }
}
