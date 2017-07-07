package com.rammstein.messenger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.SelectableContactListAdapter;
import com.rammstein.messenger.custom_view.RecyclerViewWithEmptyView;
import com.rammstein.messenger.fragment.dialog.Confirmation2ChoicesDialog;
import com.rammstein.messenger.fragment.dialog.ConfirmationDialog;
import com.rammstein.messenger.fragment.dialog.NotificationDialog;
import com.rammstein.messenger.fragment.dialog.TextInputDialogFragment;
import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.local.Chat;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.util.GlideHelper;
import com.rammstein.messenger.util.InternetHelper;
import com.rammstein.messenger.util.RealmHelper;
import com.rammstein.messenger.util.SimpleAnimator;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

import static com.rammstein.messenger.activity.ChatActivity.FINISH;

/**
 * Created by user on 09.06.2017.
 */

public class AddChatMembersActivity extends AppCompatActivity
        implements SelectableContactListAdapter.OnContactSelectedListener, ConfirmationDialog.OnClickListener,
        TextInputDialogFragment.OnClickListener, Confirmation2ChoicesDialog.OnClickListener{
    public final static String CHAT_ID = "chat_id";
    private static final String CONFIRM_CHAT_CREATION_DIALOG = "confirm_chat_creation";
    private static final String CHAT_TITLE_INPUT_DIALOG = "title_dialog";
    private Chat mChat;
    private Chat mExistingChat;
    private SelectableContactListAdapter mContactsAdapter;
    private ArrayList<UserDetails> mChatMembers;
    private RetrofitHelper mRetrofitHelper;

    @BindView(R.id.new_users_container) ScrollView mNewMembersContainer;
    @BindView(R.id.flex_new_users) FlexboxLayout mFlexboxNewMembers;
    @BindView(R.id.current_members_scroll_view) ScrollView mCurrentMembersScrollContainer;
    @BindView(R.id.current_members_container) LinearLayout mCurrentMembersContainer;
    @BindView(R.id.flex_current_members) FlexboxLayout mFlexboxCurrentMembers;
    @BindView(R.id.rv_contacts) RecyclerViewWithEmptyView mContactsRV;
    @BindView(R.id.tv_place_holder) TextView mPlaceholder;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.appbar_layout) AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat_members);
        ButterKnife.bind(this);
        mRetrofitHelper = new RetrofitHelper(this);
        getCurrentChatData();
        initContent();
    }

    private void getCurrentChatData() {
        mChatMembers = new ArrayList<>();
        int chatId = getIntent().getIntExtra(CHAT_ID, -1);
        if (chatId != -1){
            RealmRepository realmRepository = RealmRepository.getInstance();
            mChat = realmRepository.getById(Chat.class, chatId);
            mChatMembers.addAll(mChat.getChatMembers());
            Log.i("size", mChatMembers.size()+"");
        }
    }

    private void initContent() {
        initContacts();
        initActionBar();
    }

    private void initContacts() {
        ArrayList<UserDetails> contacts = new ArrayList<>(RealmHelper.getSortedContactList());

        if (!mChatMembers.isEmpty()){

            for (UserDetails chatMember : mChatMembers){
                contacts.remove(chatMember);
            }
        }

        mContactsRV.setLayoutManager(new LinearLayoutManager(this));
        mContactsAdapter = new SelectableContactListAdapter(this, contacts);
        mContactsRV.setEmptyView(mPlaceholder);
        mContactsRV.setAdapter(mContactsAdapter);
    }

    private void initActionBar() {
        setSupportActionBar(mToolbar);
        if (mChat == null || mChat.isDialog()){
            getSupportActionBar().setTitle(getString(R.string.create_new_chat));
        } else {
            getSupportActionBar().setTitle(String.format(getString(R.string.add_members_to_chat_x), mChat.getChatName()));
        }

        if (!mChatMembers.isEmpty()){

            mCurrentMembersContainer.setVisibility(View.VISIBLE);

            for (UserDetails chatMember : mChatMembers){
                addMemberView(chatMember, mFlexboxCurrentMembers);
            }

            mCurrentMembersScrollContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    int targetHeight = getMeasuredHeight(mCurrentMembersScrollContainer, mToolbar.getHeight()*2);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mCurrentMembersScrollContainer.getLayoutParams();
                    layoutParams.height = targetHeight;
                    mCurrentMembersScrollContainer.setLayoutParams(layoutParams);
                    mCurrentMembersScrollContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });


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


    @Override
    public void onContactSelected(final UserDetails userDetails, final int index) {

        if (mContactsAdapter.getSelectedContacts().contains(userDetails)){
            View v = addMemberView(userDetails, mFlexboxNewMembers);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFlexboxNewMembers.removeView(v);
                    mContactsAdapter.getSelectedContacts().remove(userDetails);
                    mContactsAdapter.notifyItemChanged(index);
                    v.setOnClickListener(null);
                    updateNewMembersContainer();
                }
            });
            v.setId(index);
        } else {
            View v = mFlexboxNewMembers.findViewById(index);
            mFlexboxNewMembers.removeView(v);
            v.setOnClickListener(null);
        }
        updateNewMembersContainer();
    }

    private void updateNewMembersContainer() {
        if (mContactsAdapter.getSelectedContacts().size() > 0){
            mFlexboxNewMembers.setVisibility(View.VISIBLE);
        } else {
            mFlexboxNewMembers.setVisibility(View.GONE);
        }

        final int height = mNewMembersContainer.getHeight();
        int targetHeight = getMeasuredHeight(mNewMembersContainer, mToolbar.getHeight()*2);
        final int delta = targetHeight - height;
        if (delta != 0){
            SimpleAnimator.animateHeight(mNewMembersContainer, height, delta);
        }
    }

    private int getMeasuredHeight(View view, int maxHeight) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.AT_MOST);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST);
        view.measure(widthSpec, heightSpec);
        return view.getMeasuredHeight();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_chat_members_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.save:
                if (mContactsAdapter.getSelectedContacts().size() == 0){
                    break;
                }
                if (mContactsAdapter.getSelectedContacts().size() == 1 && mChat == null){
                    UserDetails userDetails = mContactsAdapter.getSelectedContacts().get(0);
                    showPrivateChat(userDetails);
                    break;
                }

                mExistingChat = tryFindExistingChat();
                if(mExistingChat != null){
                    String title = getResources().getString(R.string.chat_exists);
                    String message = getResources().getString(R.string.chat_exists_create_anyway);
                    String choice = getString(R.string.open_existing_chat);
                    DialogFragment dialogFragment = Confirmation2ChoicesDialog.newInstance(title, message, choice);
                    dialogFragment.show(getSupportFragmentManager(), CONFIRM_CHAT_CREATION_DIALOG);
                } else {
                    createOrUpdateChat();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createOrUpdateChat() {
        if (InternetHelper.isConnected()){
            if (mChat == null || mChat.isDialog()){
                createNewChat();
            } else {
                addMembersToChat();
            }
        } else {
            DialogFragment alert = NotificationDialog.newInstance(getResources().getString(R.string.internet_not_available), null);
            alert.show(getSupportFragmentManager(), "internet_not_available");
        }
    }

    private void addMembersToChat() {
        int [] userIds = new int[mContactsAdapter.getSelectedContacts().size()];
        for (int i = 0; i < userIds.length; i++){
            userIds[i] = mContactsAdapter.getSelectedContacts().get(i).getId();
        }
        mRetrofitHelper.addMembersToChat(this, mChat.getId(), userIds);
        finish();
    }

    private void createNewChat() {
        String title = getString(R.string.enter_chat_title);
        String hint = getString(R.string.chat_title);
        DialogFragment dialog = TextInputDialogFragment.newInstance(title, hint);
        dialog.show(getSupportFragmentManager(), CHAT_TITLE_INPUT_DIALOG);
    }

    private void closePreviousChat() {
        Intent i = new Intent();
        i.putExtra(FINISH, true);
        setResult(RESULT_OK, i);
    }

    private void openExistingChat(){
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra(ChatActivity.CHAT_ID_EXTRA, mExistingChat.getId());
        startActivity(i);
    }

    private Chat tryFindExistingChat() {
        AppUser appUser = RealmHelper.getCurrentUser();

        ArrayList<UserDetails> targetChatMembes = new ArrayList<>();
        targetChatMembes.addAll(mChatMembers);
        targetChatMembes.addAll(mContactsAdapter.getSelectedContacts());
        if (!targetChatMembes.contains(appUser.getUserDetails())){
            targetChatMembes.add(appUser.getUserDetails());
        }

        RealmResults<Chat> groupChats = appUser.getChats().where().equalTo(Chat.IS_PRIVATE, false).findAll();

        for (Chat chat : groupChats){
            boolean chatsEqual = false;
            if (chat.getChatMembers().size() == targetChatMembes.size()){
                chatsEqual = true;
                for (UserDetails member : chat.getChatMembers()){
                    if (!targetChatMembes.contains(member)){
                        chatsEqual = false;
                        break;
                    }
                }
            }

            if (chatsEqual){
                return chat;
            }
        }

        return null;
    }

    private void showPrivateChat(UserDetails userDetails) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra(ChatActivity.USER_ID_EXTRA, userDetails.getId());
        startActivity(i);
        finish();
    }


    @Override
    public void onConfirm(String tag) {
        switch (tag){
            case CONFIRM_CHAT_CREATION_DIALOG:
                Log.i("Dialog", "onConfirm");
                createOrUpdateChat();
                break;
        }
    }

    @Override
    public void onChoiceSelected(String tag) {
        switch (tag){
            case CONFIRM_CHAT_CREATION_DIALOG:
                Log.i("Dialog", "onChoiceSelected");
                openExistingChat();
                closePreviousChat();
                finish();
        }
    }

    @Override
    public void onOkClicked(String tag, String textOutput) {
        switch (tag){
            case CHAT_TITLE_INPUT_DIALOG:
                AppUser appUser = RealmHelper.getCurrentUser();
                ArrayList<UserDetails> users = new ArrayList<>();
                users.addAll(mContactsAdapter.getSelectedContacts());
                users.addAll(mChatMembers);
                users.remove(appUser.getUserDetails());

                int[] userIds = new int[users.size()];
                for (int i = 0; i < userIds.length; i++){
                    int userId = users.get(i).getId();
                    if (userId != appUser.getId()){
                        userIds[i] = userId;
                    }
                }
                mRetrofitHelper.createGroupChat(this, userIds, textOutput);
                closePreviousChat();
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
