package com.rammstein.messenger.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.MessageAdapter;
import com.rammstein.messenger.fragment.dialog.ConfirmationDialog;
import com.rammstein.messenger.fragment.dialog.ConfirmationWithOptionsDialog;
import com.rammstein.messenger.fragment.dialog.MenuDialog;
import com.rammstein.messenger.fragment.dialog.NotificationDialog;
import com.rammstein.messenger.fragment.dialog.ProfileDialog;
import com.rammstein.messenger.fragment.dialog.TextInputDialogFragment;
import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.local.Chat;
import com.rammstein.messenger.model.local.Message;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.model.web.response.UserDetailsResponse;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.repository.SharedPreferencesRepository;
import com.rammstein.messenger.util.GlideHelper;
import com.rammstein.messenger.util.InternetHelper;
import com.rammstein.messenger.util.RealmHelper;
import com.rammstein.messenger.util.SimpleAnimator;
import com.rammstein.messenger.web.retrofit.CustomCallback;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Response;

import static com.rammstein.messenger.activity.AddChatMembersActivity.CHAT_ID;
import static com.rammstein.messenger.activity.MainActivity.ACTION_UPDATE_VIEW;
import static com.rammstein.messenger.activity.MainActivity.PROFILE_DIALOG;
import static com.rammstein.messenger.activity.SearchActivity.USER_MENU_DIALOG;
import static com.rammstein.messenger.model.local.Message.TIME_IN_MILLS;
import static com.rammstein.messenger.service.SignalRService.APP_USER_TYPING;
import static com.rammstein.messenger.service.SignalRService.TYPING;
import static com.rammstein.messenger.service.SignalRService.USER_ID;

/**
 * Created by user on 29.05.2017.
 */

public class ChatActivity extends AppCompatActivity
        implements View.OnClickListener, TextInputDialogFragment.OnClickListener, ConfirmationDialog.OnClickListener,
        ConfirmationWithOptionsDialog.OnClickListener, MenuDialog.OnClickListener

{
    public final static String CHAT_ID_EXTRA = "chat_id";
    public final static String USER_ID_EXTRA = "user_id";
    public final static String USER_NAME_EXTRA = "user_name";
    public static final String CHAT_TITLE_INPUT_DIALOG = "title_dialog";
    private static final String LEAVE_CHAT_CONFIRMATION_DIALOG = "leave_chat_confirmation_dialog";
    private static final String DELETE_HISTORY_CONFIRMATION_DIALOG = "delete_chat_history_confirmation_dialog";
    private static final int CHAT_MEMBER = 0;
    private static final String TAG = "ChatActivity";
    private static final int CREATE_CHAT = 1;
    public static final String FINISH = "finish";
    public static final String CHAT_CREATED = "chat_created";

    @BindView(R.id.rv_chat) RecyclerView mChatRecyclerView;
    @BindView(R.id.et_message_input) EditText mMessageEditText;
    @BindView(R.id.btn_send_message) Button mSendMessageButton;
    @BindView(R.id.current_members_container) LinearLayout mChatMembersLayout;
    @BindView(R.id.flex_current_members) FlexboxLayout mChatMembersContainer;
    @BindView(R.id.current_members_scroll_view) ScrollView mChatMembersScrollView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_is_typing) TextView mUserTypingTextView;

    private MessageAdapter mMessageAdapter;
    private boolean mChatExists;
    private Chat mChat;
    private AppUser mAppUser;
    private UserDetails mUserDetails;
    private RealmResults<Message> mMessages;
    private boolean mIsMemberListVisible;
    private RealmList<UserDetails> mChatMembers;
    private RetrofitHelper mRetrofitHelper;
    private BroadcastReceiver mChatCreatedReceiver;
    private BroadcastReceiver mTypingReceiver;
    private Handler mHandler;
    private Runnable mHideTypingRunnable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("Chat", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        mRetrofitHelper = new RetrofitHelper(this);
        mHandler = new Handler();
        mHideTypingRunnable = new Runnable() {
            @Override
            public void run() {
                mUserTypingTextView.setVisibility(View.GONE);
            }
        };

        getChatData(getIntent().getIntExtra(CHAT_ID_EXTRA, -1));
        initChat();
        initActionBar();

        mSendMessageButton.setOnClickListener(this);
        mToolbar.setOnClickListener(this);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty() || (mChat != null && !mChat.getChatMembers().contains(mAppUser.getUserDetails()))){
                    mSendMessageButton.setEnabled(false);
                    mSendMessageButton.setAlpha(0.5f);
                } else {
                    mSendMessageButton.setEnabled(true);
                    mSendMessageButton.setAlpha(1);

                    if (mChat != null){
                        Intent i = new Intent(APP_USER_TYPING);
                        i.putExtra(CHAT_ID, mChat.getId());
                        sendBroadcast(i);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "resume");
        super.onResume();
        setChatData();
        setMessageListener();
        setChatMembersListener();

        mChatCreatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int chatId = intent.getIntExtra(Chat.ID, -1);
                getChatData(chatId);
                setChatData();
                setMessageListener();
                setChatMembersListener();

                if (mChatExists && mMessageAdapter != null && mMessages.size() > 0){
                    mChatRecyclerView.smoothScrollToPosition(mMessages.size()-1);
                }
            }
        };

        mTypingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //TODO
                int chatId = intent.getIntExtra(CHAT_ID, -1);
                int userId = intent.getIntExtra(USER_ID, -1);
                Log.i(TAG, "typing " + chatId + " " + userId);

                if (mChat != null && mChat.getId() == chatId && mAppUser.getId() != userId){
                    UserDetails user = mChat.getChatMembers().where().equalTo(UserDetails.ID, userId).findFirst();
                    mUserTypingTextView.setVisibility(View.VISIBLE);
                    mUserTypingTextView.setText(String.format(getString(R.string.user_x_is_typing), user.getName()));
                    mHandler.removeCallbacks(mHideTypingRunnable);
                    mHandler.postDelayed(mHideTypingRunnable, 2000);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(CHAT_CREATED);
        IntentFilter typingFilter = new IntentFilter(TYPING);
        registerReceiver(mChatCreatedReceiver, intentFilter);
        registerReceiver(mTypingReceiver, typingFilter);
    }

    private void setChatMembersListener() {
        if (mChatExists){
            Log.i(TAG, "setting message listener");
            mChatMembers.addChangeListener(new RealmChangeListener<RealmList<UserDetails>>() {
                @Override
                public void onChange(RealmList<UserDetails> userDetailses) {
                    Log.i(TAG, "members listener");
                    displayChatMembers();
                }
            });
        }
    }

    private void setMessageListener() {
        if (mChatExists){
            Log.i(TAG, "setting message listener");
            mMessages.addChangeListener(new RealmChangeListener<RealmResults<Message>>() {
                @Override
                public void onChange(RealmResults<Message> messages) {
                    Log.i(TAG, "message listener");
                    setChatData();
                    if (mChatExists && mMessageAdapter != null && mMessages.size() > 0){
                        mChatRecyclerView.smoothScrollToPosition(mMessages.size()-1);
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mChatExists){
            Log.i(TAG, "remove listeners");
            mMessages.removeAllChangeListeners();
            mChatMembers.removeAllChangeListeners();
            unregisterReceiver(mChatCreatedReceiver);
            unregisterReceiver(mTypingReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mHideTypingRunnable);
    }

    private void initActionBar() {
        setSupportActionBar(mToolbar);
        String chatTitle;
        if (mChat != null){
            chatTitle = mChat.getChatName();
        } else {
            if (mUserDetails != null){
                chatTitle = mUserDetails.getName();
            } else {
                chatTitle = getIntent().getExtras().getString(USER_NAME_EXTRA);
            }
        }
        getSupportActionBar().setTitle(chatTitle);
    }

    private void displayChatMembers() {
        String subtitle = String.format(getResources().getString(R.string.chat_members) + " %d", mChat.getChatMembers().size());
        mToolbar.setSubtitle(subtitle);
        mChatMembersContainer.removeAllViews();
        mChatMembersLayout.setVisibility(View.VISIBLE);
        RealmResults <UserDetails> members = mChat.getChatMembers()
                .where()
                .findAllSorted(UserDetails.FIRST_NAME, Sort.ASCENDING, UserDetails.LAST_NAME, Sort.ASCENDING);
        for (UserDetails chatMember : members){
            View v = addMemberView(chatMember, mChatMembersContainer);
            v.setOnClickListener(this);
            v.setId(CHAT_MEMBER);
            v.setTag(chatMember);
        }
    }

    private View addMemberView(UserDetails chatMember, ViewGroup layout) {
        View v = getLayoutInflater().inflate(R.layout.item_contact_small, null);
        TextView username = (TextView) v.findViewById(R.id.tv_username);
        ImageView avatar = (ImageView) v.findViewById(R.id.iv_avatar);
        GlideHelper.loadAvatar(this, avatar, chatMember);
        username.setText(chatMember.getName());
        layout.addView(v);
        return v;
    }

    private int getMeasuredHeight(View view, int maxHeight) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.AT_MOST);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST);
        view.measure(widthSpec, heightSpec);
        return view.getMeasuredHeight();
    }

    private void initChat() {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        mChatRecyclerView.setLayoutManager(llm);
    }

    private void getChatData(int chatId) {
        RealmRepository realmRepository = RealmRepository.getInstance();
        mAppUser = RealmHelper.getCurrentUser();
        if (chatId != -1){
            mChat = realmRepository.getById(Chat.class, chatId);
        } else {
            int userId = getIntent().getIntExtra(USER_ID_EXTRA, -1);
            mUserDetails = realmRepository.getById(UserDetails.class, userId);
            mChat = RealmHelper.findPrivateChat(userId);
            if (mChat == null){
                mChatExists = false;
                return;
            }
        }
        mChatExists = true;
        mMessages = mChat.getMessages().where().findAllSorted(Message.TIME_IN_MILLS, Sort.ASCENDING);
        mChatMembers = mChat.getChatMembers();
    }

    private void setChatData() {
        if(mChatExists){
            ArrayList<Message> list = new ArrayList(mMessages);
            if (mMessageAdapter == null){
                mMessageAdapter = new MessageAdapter(this, list);
                mChatRecyclerView.setAdapter(mMessageAdapter);
            } else {
                mMessageAdapter.setData(list);
                mMessageAdapter.notifyDataSetChanged();
            }
            if (!mChat.isDialog()){
                displayChatMembers();
            }
            if (mChat.getMessages().size() > 0){
                long lastMessageTime = mChat.getMessages().where().max(TIME_IN_MILLS).longValue();
                RealmRepository realmRepository = RealmRepository.getInstance();
                realmRepository.beginTransaction();
                mChat.setLastSeenMessageTime(lastMessageTime);
                realmRepository.commitTransaction();
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_send_message:
                String messageText = mMessageEditText.getText().toString();
                if (InternetHelper.isConnected()){
                    if (mChatExists){
                        Log.i(TAG, "sending message!");
                        sendMessage(messageText);
                    } else {
                        Log.i(TAG, "creating chat and sending message!");
                        createChatAndSendMessage();
                    }
                    int userId = getIntent().getIntExtra(USER_ID_EXTRA, -1);
                    if (userId != -1){
                        UserDetails user = RealmHelper.getCurrentUser().getFriends().where().equalTo(UserDetails.ID, userId).findFirst();
                        if (user == null){
                            Log.i(TAG, "adding user to friends!");
                            addUserToFriends();
                        }
                    }

                } else {
                    DialogFragment alert = NotificationDialog.newInstance(getResources().getString(R.string.internet_not_available), null);
                    alert.show(getSupportFragmentManager(), "internet_not_available");
                }
                break;
            case R.id.toolbar:
                int delta = 0;
                int height = mChatMembersScrollView.getHeight();
                int measuredHeight = getMeasuredHeight(mChatMembersScrollView, mToolbar.getHeight()*2);
                if (mIsMemberListVisible){
                    mIsMemberListVisible = false;
                    delta = -height;
                } else {
                    mIsMemberListVisible = true;
                    delta = measuredHeight - height;
                }
                SimpleAnimator.animateHeight(mChatMembersScrollView, height, delta);
                break;
            case CHAT_MEMBER:
                AppUser appUser = RealmHelper.getCurrentUser();
                UserDetails user = (UserDetails) v.getTag();
                if (user.getId() == appUser.getId()){
                    break;
                }

                int[] options;
                if (appUser.getFriends().contains(user)){
                    options = new int[]{R.string.show_information, R.string.delete_contact};
                } else {
                    options = new int[]{R.string.show_information, R.string.add_to_contacts};
                }

                if (mChat.getCreatorId() == appUser.getId()){
                    int[] temp = options;
                    options = new int[options.length + 1];
                    System.arraycopy(temp, 0, options, 0, temp.length);
                    options[options.length-1] = R.string.kick_from_chat;
                }

                DialogFragment dialog = MenuDialog.newInstance(options, user.getId(), 0, user.getName());
                dialog.show(getSupportFragmentManager(), USER_MENU_DIALOG);
                break;
        }

    }

    private void addUserToFriends() {
        final int userId = getIntent().getIntExtra(USER_ID_EXTRA, -1);
        RetrofitHelper helper = RetrofitHelper.getInstance();
        helper.addToContacts(this, userId);

    }

    private void sendMessage(String messageText) {
        mRetrofitHelper.sendMessage(this, mChat.getId(), messageText);
        mMessageEditText.getText().clear();
    }

    private void createChatAndSendMessage() {
        RealmRepository realmRepository = RealmRepository.getInstance();
        final int userId = getIntent().getIntExtra(USER_ID_EXTRA, -1);
        UserDetails user = realmRepository.getById(UserDetails.class, userId);
        mRetrofitHelper.createDialogAndSendMessage(this, user, mMessageEditText.getText().toString());
        mMessageEditText.getText().clear();
    }

    private int getCurrentUserId() {
        return SharedPreferencesRepository.getInstance().getCurrentUserId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("Chat", "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int currentUserId = SharedPreferencesRepository.getInstance().getCurrentUserId();
        UserDetails currentUser = RealmRepository.getInstance().getById(UserDetails.class, currentUserId);

        MenuItem changeTitle = menu.findItem(R.id.change_title);
        MenuItem addUser = menu.findItem(R.id.add_user);
        MenuItem deleteChatHistory = menu.findItem(R.id.delete_chat_history);
        MenuItem leaveChat = menu.findItem(R.id.leave_chat);

        changeTitle.setVisible(false);
        addUser.setVisible(false);
        deleteChatHistory.setVisible(false);
        leaveChat.setVisible(false);

        if (mChatExists) {
            deleteChatHistory.setVisible(true);
            if (mChat.getChatMembers().contains(currentUser)){
                addUser.setVisible(true);
                if (!mChat.isDialog()){
                    changeTitle.setVisible(true);
                    leaveChat.setVisible(true);
                }
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DialogFragment dialog;
        int id = item.getItemId();
        switch (id){
            case R.id.add_user:
                Intent i = new Intent(this, AddChatMembersActivity.class);
                if (mChat != null){
                    i.putExtra(CHAT_ID, mChat.getId());
                }
                startActivityForResult(i, CREATE_CHAT);
                break;
            case R.id.change_title:
                dialog = TextInputDialogFragment.newInstance(getString(R.string.chat_title), mChat.getChatName());
                dialog.show(getSupportFragmentManager(), CHAT_TITLE_INPUT_DIALOG);
                break;
            case R.id.leave_chat:
                int[] options = {R.string.delete_chat_history};
                dialog = ConfirmationWithOptionsDialog.newInstance(getResources().getString(R.string.confirm_leave_chat), options);
                dialog.show(getSupportFragmentManager(), LEAVE_CHAT_CONFIRMATION_DIALOG);
                break;
            case R.id.delete_chat_history:
                dialog = ConfirmationDialog.newInstance(getResources().getString(R.string.confirm_delete_history));
                dialog.show(getSupportFragmentManager(), DELETE_HISTORY_CONFIRMATION_DIALOG);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOkClicked(String tag, String textOutput) {
        switch (tag){
            case CHAT_TITLE_INPUT_DIALOG:
                if (textOutput.isEmpty()){
                    textOutput = getString(R.string.nameless);
                }
                mChat.setChatName(textOutput);
                getSupportActionBar().setTitle(textOutput);
                break;
        }
    }

    @Override
    public void onConfirm(String tag) {
        AppUser appUser = RealmHelper.getCurrentUser();
        switch (tag){
            case DELETE_HISTORY_CONFIRMATION_DIALOG:
                mRetrofitHelper.deleteChatHistory(this, mChat.getId());
                if (!mChat.getChatMembers().contains(appUser.getUserDetails())){
                    finish();
                }
                break;
        }
    }

    @Override
    public void onConfirm(String tag, Bundle args) {
        AppUser appUser = RealmHelper.getCurrentUser();
        switch (tag){
            case LEAVE_CHAT_CONFIRMATION_DIALOG:
                boolean deleteChat = args.getBoolean(Integer.toString(R.string.delete_chat_history));
                RealmRepository.getInstance().beginTransaction();
                mRetrofitHelper.removeUserFromChat(this, mChat.getId(), appUser.getId());
                RealmRepository.getInstance().commitTransaction();
                if (deleteChat){
                    mRetrofitHelper.deleteChatHistory(this, mChat.getId());
                    finish();
                }
                break;
        }
    }

    @Override
    public void onOptionSelected(String tag, int optionId, int itemId, int itemIndex) {
        RealmRepository realmRepository = RealmRepository.getInstance();
        UserDetails user = realmRepository.getById(UserDetails.class, itemId);
        switch (tag){
            case USER_MENU_DIALOG:
                switch (optionId){
                    case R.string.show_information:
                        ProfileDialog dialog = ProfileDialog.newInstance(itemId);
                        dialog.show(getSupportFragmentManager(), PROFILE_DIALOG);
                        break;
                    case R.string.add_to_contacts:
                        mRetrofitHelper.addToContacts(this, user.getId());
                        break;
                    case R.string.delete_contact:
                        mRetrofitHelper.removeFromContacts(this, user.getId());
                        break;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case CREATE_CHAT:
                    boolean finishThisActivity = data.getBooleanExtra(FINISH, false);
                    if (finishThisActivity){
                        finish();
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
