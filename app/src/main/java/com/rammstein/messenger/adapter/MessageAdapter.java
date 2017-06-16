package com.rammstein.messenger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rammstein.messenger.R;
import com.rammstein.messenger.model.Message;
import com.rammstein.messenger.model.UserDetails;
import com.rammstein.messenger.repository.TestUserDetailRepository;
import com.rammstein.messenger.util.SimpleDateUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 29.05.2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int USER_MESSAGE = 0;
    private final static int RECEIVED_MESSAGE = 1;

    private Context mContext;
    private ArrayList<Message> mMessages;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        mContext = context;
        mMessages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        int senderId = message.getSenderId();
        int currentUserId = getCurrentUserId();
        if(senderId == currentUserId){
            return USER_MESSAGE;
        } else {
            return RECEIVED_MESSAGE;
        }
    }

    private int getCurrentUserId() {
        //TODO
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case USER_MESSAGE:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_user_message, parent, false);
                return new ViewHolderUserMessage(view);
            case RECEIVED_MESSAGE:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_received_message, parent, false);
                return new ViewHolderReceivedMessage(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        bindCommonData((ViewHolderBasicMessage) holder, position);
        switch (viewType){
            case USER_MESSAGE:
                bindUserMessageViewHolder((ViewHolderUserMessage)holder, position);
                break;
            case RECEIVED_MESSAGE:
                bindReceivedMessageViewHolder((ViewHolderReceivedMessage)holder, position);
                break;
        }
    }

    private void bindCommonData(ViewHolderBasicMessage holder, int position) {
        Message message = mMessages.get(position);
        setMessageTime(holder, message);
        setMessageText(holder, message);
    }

    private void bindUserMessageViewHolder(ViewHolderUserMessage holder, int position) {
        //configure  app user message if needed
    }

    private void bindReceivedMessageViewHolder(ViewHolderReceivedMessage holder, int position) {
        Message message = mMessages.get(position);
        setUsername(holder, message);
        setSeen(holder, message);
        //TODO setAvatar
    }

    private void setSeen(ViewHolderReceivedMessage holder, Message message) {
        if (!message.isSeen()){
            message.setSeen(true);
            holder.mSeen.setVisibility(View.VISIBLE);
        } else {
            holder.mSeen.setVisibility(View.GONE);
        }
    }

    private void setMessageText(ViewHolderBasicMessage holder, Message message) {
        String text = message.getText();

        holder.mMessageText.setText(text);
    }

    private void setMessageTime(ViewHolderBasicMessage holder, Message message) {
        long messageTime = message.getTimeInMills();
        String time = SimpleDateUtils.formatTime(mContext, messageTime, DateUtils.FORMAT_SHOW_TIME);

        holder.mMessageTime.setText(time);
    }

    private void setUsername(ViewHolderReceivedMessage holder, Message message) {
        TestUserDetailRepository users = new TestUserDetailRepository();
        int senderId = message.getSenderId();
        UserDetails sender = users.getById(senderId);
        String firstName = sender.getFirstName();
        String lastName = sender.getLastName();
        String username;
        if (firstName == null || firstName.isEmpty() && lastName == null || lastName.isEmpty()){
            username = sender.getUsername();
        } else {
            username = String.format("%s %s", firstName, lastName);
        }

        holder.mUsername.setText(username);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class ViewHolderReceivedMessage extends ViewHolderBasicMessage{
        @BindView(R.id.civ_avatar) CircleImageView mAvatar;
        @BindView(R.id.tv_username) TextView mUsername;
        @BindView(R.id.civ_seen) CircleImageView mSeen;
        public ViewHolderReceivedMessage(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ViewHolderUserMessage extends ViewHolderBasicMessage{

        public ViewHolderUserMessage(View itemView) {
            super(itemView);
        }
    }

    class ViewHolderBasicMessage extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_message_time) TextView mMessageTime;
        @BindView(R.id.tv_message) TextView mMessageText;
        @BindView(R.id.ll_message_container) LinearLayout mMessageContainer;
        public ViewHolderBasicMessage(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
