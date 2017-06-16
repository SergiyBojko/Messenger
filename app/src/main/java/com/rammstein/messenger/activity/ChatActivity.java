package com.rammstein.messenger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.MessageAdapter;
import com.rammstein.messenger.fragment.dialog.TextInputDialogFragment;
import com.rammstein.messenger.model.Chat;
import com.rammstein.messenger.model.Message;
import com.rammstein.messenger.model.UserDetails;
import com.rammstein.messenger.repository.Repository;
import com.rammstein.messenger.repository.TestChatRepository;
import com.rammstein.messenger.repository.TestUserDetailRepository;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 29.05.2017.
 */

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, TextInputDialogFragment.OnClickListener {
    public final static String CHAT_ID_EXTRA = "chat_id";
    public final static String USER_ID_EXTRA = "user_id";
    public static final String CHAT_TITLE_INPUT_DIALOG = "title_dialog";

    @BindView(R.id.rv_chat) RecyclerView mChatRecyclerView;
    @BindView(R.id.et_message_input) EditText mMessageEditText;
    @BindView(R.id.btn_send_message) Button mSendMessageButton;

    private MessageAdapter mMessageAdapter;
    private boolean mChatExists;
    private Chat mChat;
    private UserDetails mUserDetails;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("Chat", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        initChat();
        initActionBar();

        mSendMessageButton.setOnClickListener(this);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()){
                    mSendMessageButton.setEnabled(false);
                    mSendMessageButton.setAlpha(0.5f);
                } else {
                    mSendMessageButton.setEnabled(true);
                    mSendMessageButton.setAlpha(1);
                }
            }
        });
    }

    private void initActionBar() {
        String chatTitle;
        if (mChat != null){
            chatTitle = mChat.getChatName();
        } else {
            chatTitle = mUserDetails.getName();
        }
        getSupportActionBar().setTitle(chatTitle);
    }

    private void initChat() {
        getChatData();
        if(mChatExists){
            setChatData();
        }
    }

    private void getChatData() {
        int chatId = getIntent().getIntExtra(CHAT_ID_EXTRA, -1);
        Repository<Chat> chatRepository = TestChatRepository.getInstance();
        if (chatId != -1){
            mChat = chatRepository.getById(chatId);
        } else {
            int userId = getIntent().getIntExtra(USER_ID_EXTRA, -1);
            mUserDetails = new TestUserDetailRepository().getById(userId);
            mChat = mUserDetails.getPrivateChat();
            if (mChat == null){
                mChat = findValidChat(userId);
                if (mChat == null){
                    mChatExists = false;
                    return;
                } else {
                    mUserDetails.setPrivateChat(mChat);
                }
            }

        }
        mChatExists = true;
    }

    private Chat findValidChat(int userId) {
        //TODO find chat in database
        return null;
    }


    private void setChatData() {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        mChatRecyclerView.setLayoutManager(llm);
        mMessageAdapter = new MessageAdapter(this, mChat.getMessages());
        mChatRecyclerView.setAdapter(mMessageAdapter);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_send_message:
                String messageText = mMessageEditText.getText().toString();
                if (mChat == null){
                    createChat();
                } else {
                    sendMessage(messageText);
                }
                break;
        }

    }

    private void sendMessage(String messageText) {
        //TODO send message to server
        Message message = new Message(0, mChat.getId(), getCurrentUserId(), messageText, System.currentTimeMillis());
        mChat.getMessages().add(message);
        mMessageAdapter.notifyDataSetChanged();
        mMessageEditText.getText().clear();
        mChatRecyclerView.scrollToPosition(mChat.getMessages().size()-1);
    }

    private void createChat() {
        //TODO
    }

    private int getCurrentUserId() {
        //TODO
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("Chat", "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem changeTitle = menu.findItem(R.id.change_title);
        MenuItem addUser = menu.findItem(R.id.add_user);
        MenuItem deleteChat = menu.findItem(R.id.delete_chat);
        MenuItem leaveChat = menu.findItem(R.id.leave_chat);
        if (mChat == null){
            changeTitle.setVisible(false);
            deleteChat.setVisible(false);
            leaveChat.setVisible(false);
        } else {
            changeTitle.setVisible(true);
            deleteChat.setVisible(true);
            leaveChat.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.add_user:
                Intent i = new Intent(this, AddChatMembersActivity.class);
                if (mChat != null){
                    i.putExtra(AddChatMembersActivity.CHAT_ID, mChat.getId());
                }
                startActivity(i);
                break;
            case R.id.change_title:
                DialogFragment dialog = TextInputDialogFragment.newInstance(getString(R.string.chat_title), mChat.getChatName());
                dialog.show(getSupportFragmentManager(), CHAT_TITLE_INPUT_DIALOG);

                break;
            case R.id.leave_chat:
                //TODO
                break;
            case R.id.delete_chat:
                //TODO
                break;
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
}
