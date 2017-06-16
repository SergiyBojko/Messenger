package com.rammstein.messenger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.SelectableContactListAdapter;
import com.rammstein.messenger.custom_view.RecyclerViewWithEmptyView;
import com.rammstein.messenger.model.Chat;
import com.rammstein.messenger.model.UserDetails;
import com.rammstein.messenger.repository.Repository;
import com.rammstein.messenger.repository.TestChatRepository;
import com.rammstein.messenger.repository.TestUserDetailRepository;
import com.rammstein.messenger.util.SimpleAnimator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 09.06.2017.
 */

public class AddChatMembersActivity extends AppCompatActivity implements SelectableContactListAdapter.OnContactSelectedListener{
    public final static String CHAT_ID = "chat_id";
    private Chat mChat;
    private SelectableContactListAdapter mContactsAdapter;
    private ArrayList<UserDetails> mChatMembers;

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
        initContent();
    }

    private void initContent() {
        mChatMembers = new ArrayList<>();
        mChat = null;
        int chatId = getIntent().getIntExtra(CHAT_ID, -1);
        if (chatId != -1){
            Repository<Chat> chatRepository = TestChatRepository.getInstance();
            mChat = chatRepository.getById(chatId);
            mChatMembers.addAll(mChat.getChatMembers());
            Log.i("size", mChatMembers.size()+"");
        }

        initContacts();
        initActionBar();
    }

    private void initContacts() {
        Repository<UserDetails> userDetailsRepository = TestUserDetailRepository.getInstance();
        ArrayList<UserDetails> contacts = new ArrayList<>(userDetailsRepository.getAll());

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
        if (mChat == null){
            getSupportActionBar().setTitle(getString(R.string.create_new_chat));
        } else {
            getSupportActionBar().setTitle(String.format(getString(R.string.add_members_to_chat_x), mChat.getChatName()));
        }

        if (!mChatMembers.isEmpty()){

            mCurrentMembersContainer.setVisibility(View.VISIBLE);

            for (UserDetails chatMember : mChatMembers){
                View v = getLayoutInflater().inflate(R.layout.item_contact_small, null);
                TextView username = (TextView) v.findViewById(R.id.tv_username);
                username.setText(chatMember.getName());
                mFlexboxCurrentMembers.addView(v);
            }

            mCurrentMembersScrollContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int widthSpec = View.MeasureSpec.makeMeasureSpec(mCurrentMembersScrollContainer.getWidth(), View.MeasureSpec.AT_MOST);
                    int heightSpec = View.MeasureSpec.makeMeasureSpec(mToolbar.getHeight()*2, View.MeasureSpec.AT_MOST);

                    Log.i("width", mCurrentMembersScrollContainer.getWidth()+"");
                    mCurrentMembersScrollContainer.measure(widthSpec, heightSpec);
                    int targetHeight = mCurrentMembersScrollContainer.getMeasuredHeight();
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mCurrentMembersScrollContainer.getLayoutParams();
                    layoutParams.height = targetHeight;
                    mCurrentMembersScrollContainer.setLayoutParams(layoutParams);
                    mCurrentMembersScrollContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });


        }
    }

    @Override
    public void onContactSelected(final UserDetails userDetails, final int index) {


        if (mContactsAdapter.getSelectedContacts().contains(userDetails)){
            View v = getLayoutInflater().inflate(R.layout.item_contact_small, null);
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
            TextView username = (TextView) v.findViewById(R.id.tv_username);
            username.setText(userDetails.getName());
            mFlexboxNewMembers.addView(v);
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
        int widthSpec = View.MeasureSpec.makeMeasureSpec(mNewMembersContainer.getWidth(), View.MeasureSpec.AT_MOST);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(mToolbar.getHeight()*2, View.MeasureSpec.AT_MOST);
        mNewMembersContainer.measure(widthSpec, heightSpec);
        int targetHeight = mNewMembersContainer.getMeasuredHeight();
        final int delta = targetHeight - height;
        if (delta != 0){
            SimpleAnimator.animateHeight(mNewMembersContainer, height, delta);
        }
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
                if (mContactsAdapter.getSelectedContacts().size() == 1 && mChat == null){
                    UserDetails userDetails = mContactsAdapter.getSelectedContacts().get(0);
                    showPrivateChat(userDetails);
                }

                Chat chat = tryFindExistingChat();
                if(chat != null){
                    showChat(chat);
                } else {
                    createOrUpdateChat();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createOrUpdateChat() {
        if (mChat == null || mChat.getChatMembers().size() == 2){
            createNewChat();
        } else {
            addMembersToChat();
        }
    }

    private void addMembersToChat() {
        //TODO
    }

    private void createNewChat() {
        //TODO
    }

    private void showChat(Chat chat) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra(ChatActivity.CHAT_ID_EXTRA, chat.getId());
        startActivity(i);
        finish();
    }

    private Chat tryFindExistingChat() {
        ArrayList<UserDetails> targetChatMembes = new ArrayList<>();
        targetChatMembes.addAll(mChatMembers);
        targetChatMembes.addAll(mContactsAdapter.getSelectedContacts());

        //TODO
        return null;
    }

    private void showPrivateChat(UserDetails userDetails) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra(ChatActivity.USER_ID_EXTRA, userDetails.getId());
        startActivity(i);
        finish();
    }
}
