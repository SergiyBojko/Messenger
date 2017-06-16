package com.rammstein.messenger.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rammstein.messenger.R;
import com.rammstein.messenger.activity.ChatActivity;
import com.rammstein.messenger.activity.MainActivity;
import com.rammstein.messenger.fragment.dialog.MenuDialog;
import com.rammstein.messenger.model.Chat;
import com.rammstein.messenger.model.Message;
import com.rammstein.messenger.util.SimpleDateUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 17.05.2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>  {
    private Activity mActivity;
    private ArrayList<Chat> mChats;

    public ChatAdapter(Activity activity, ArrayList<Chat> chats) {
        mActivity = activity;
        mChats = chats;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        setChatName(holder, chat);
        setLastMessage(holder, chat);
        setLastMessageTime(holder, chat);
        setTimeGroup(holder, chat, position);
        //TODO chat picture

    }

    private void setTimeGroup(ViewHolder holder, Chat chat, int position) {
        int currentTimeGroup = SimpleDateUtils.getTimeGroup(chat);
        if (position == 0){
            holder.timeGroup.setVisibility(View.VISIBLE);
            setTimeGroupText(holder, chat, currentTimeGroup);
        } else {
            Chat previousChat = mChats.get(position-1);
            int previousChatTimeGroup = SimpleDateUtils.getTimeGroup(previousChat);
            if (previousChatTimeGroup == currentTimeGroup){
                holder.timeGroup.setVisibility(View.GONE);
            } else {
                holder.timeGroup.setVisibility(View.VISIBLE);
                setTimeGroupText(holder, chat, currentTimeGroup);
            }
        }

    }

    private void setTimeGroupText(ViewHolder holder, Chat chat, int currentTimeGroup) {
        String timeGroupText = SimpleDateUtils.getTimeGroupLabel(mActivity, chat);

        holder.timeGroup.setText(timeGroupText);
    }

    private void setLastMessageTime(ViewHolder holder, Chat chat) {
        ArrayList<Message> messages = chat.getMessages();
        long lastMessageTime = messages.get(messages.size()-1).getTimeInMills();

        String formattedDate = SimpleDateUtils.formatTime(mActivity, lastMessageTime);
        holder.lastMessageTime.setText(formattedDate);
    }

    private void setLastMessage(ViewHolder holder, Chat chat) {
        ArrayList<Message> messages = chat.getMessages();
        holder.lastMessage.setText(messages.get(messages.size()-1).getText());
    }

    private void setChatName(ViewHolder holder, Chat chat) {
        holder.chatName.setText(chat.getChatName());
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        LinearLayout itemContainer;
        CircleImageView chatPicture;
        TextView chatName;
        TextView lastMessage;
        TextView timeGroup;
        TextView lastMessageTime;

        public ViewHolder(View itemView) {
            super(itemView);
            itemContainer = (LinearLayout) itemView.findViewById(R.id.item_container);
            chatPicture = (CircleImageView) itemView.findViewById(R.id.iv_chat_icon);
            chatName = (TextView) itemView.findViewById(R.id.tv_chat_name);
            lastMessage = (TextView) itemView.findViewById(R.id.tv_last_message);
            timeGroup = (TextView) itemView.findViewById(R.id.tv_time_group);
            lastMessageTime = (TextView) itemView.findViewById(R.id.tv_last_message_time);

            itemContainer.setOnClickListener(this);
            itemContainer.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(mActivity, ChatActivity.class);
            Chat selectedChat = mChats.get(getAdapterPosition());
            int selectedChatId = selectedChat.getId();
            i.putExtra(ChatActivity.CHAT_ID_EXTRA, selectedChatId);
            mActivity.startActivity(i);
        }

        @Override
        public boolean onLongClick(View v) {
            int[] menuItemIds = {R.string.delete};
            Chat selectedChat = mChats.get(getAdapterPosition());
            DialogFragment menuDialog = MenuDialog.newInstance(menuItemIds, selectedChat.getId(), selectedChat.getChatName());
            menuDialog.show(((FragmentActivity)mActivity).getSupportFragmentManager(), MainActivity.CHAT_MENU_DIALOG);
            return true;
        }
    }
}
