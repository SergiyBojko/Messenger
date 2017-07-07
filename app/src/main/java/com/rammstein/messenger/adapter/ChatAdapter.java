package com.rammstein.messenger.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rammstein.messenger.R;
import com.rammstein.messenger.activity.ChatActivity;
import com.rammstein.messenger.activity.MainActivity;
import com.rammstein.messenger.fragment.dialog.MenuDialog;
import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.local.Chat;
import com.rammstein.messenger.model.local.Message;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.util.GlideHelper;
import com.rammstein.messenger.util.RealmHelper;
import com.rammstein.messenger.util.SimpleDateUtils;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.rammstein.messenger.model.local.Message.TIME_IN_MILLS;

/**
 * Created by user on 17.05.2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>  {
    private Activity mActivity;
    private ArrayList<Chat> mChats;
    private AppUser mAppUser;

    public ChatAdapter(Activity activity, ArrayList<Chat> chats) {
        mActivity = activity;
        mChats = chats;
        mAppUser = RealmHelper.getCurrentUser();
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
        setChatPicture(holder.chatPicture, chat);
        setNewMessageCount (holder.newMessageCounter, chat);
        setIsGroupChat(holder.groupChatIcon, chat);
        setTemperature (holder.newMessageCounter, chat);
    }

    private void setTemperature(TextView newMessageCounter, Chat chat) {
        if (newMessageCounter.getVisibility() == View.VISIBLE){
            Drawable backgroundBubble;
            switch (chat.getTemperature()){
                case 0:
                    backgroundBubble = mActivity.getResources().getDrawable(R.drawable.bubble_grey);
                    break;
                case 1:
                    backgroundBubble = mActivity.getResources().getDrawable(R.drawable.bubble_green);
                    break;
                case 2:
                    backgroundBubble = mActivity.getResources().getDrawable(R.drawable.bubble_yellow);
                    break;
                case 3:
                    backgroundBubble = mActivity.getResources().getDrawable(R.drawable.bubble_red);
                    break;
                default:
                    backgroundBubble = mActivity.getResources().getDrawable(R.drawable.bubble_blue);
                    break;
            }

            newMessageCounter.setBackground(backgroundBubble);
        }
    }

    private void setIsGroupChat(AppCompatImageView groupChatIcon, Chat chat) {
        if (chat.isDialog()){
            groupChatIcon.setVisibility(View.GONE);
        } else {
            groupChatIcon.setVisibility(View.VISIBLE);
        }
    }

    private void setNewMessageCount(TextView newMessageCounter, Chat chat) {
        RealmResults<Message> newMessages = chat.getMessages().where().greaterThan(TIME_IN_MILLS, chat.getLastSeenMessageTime()).findAll();
        Log.i("chat_adapter", "chat last message time" + chat.getLastSeenMessageTime());
        if (newMessages.size() > 0){
            newMessageCounter.setVisibility(View.VISIBLE);
            newMessageCounter.setText(Integer.toString(newMessages.size()));
        } else {
            newMessageCounter.setVisibility(View.GONE);
        }
    }

    private void setChatPicture(ImageView chatPicture, Chat chat) {
        if (chat.isDialog()){
            for (UserDetails user : chat.getChatMembers()){
                if (user.getId() != mAppUser.getId()){
                    GlideHelper.loadAvatar(mActivity, chatPicture, user);
                }
            }
        } else {
            RealmResults<Message> messages = chat.getMessages().where().findAllSorted(Message.TIME_IN_MILLS, Sort.ASCENDING);
            if (messages.size() > 0){
                int lastSenderId = messages.get(messages.size()-1).getSenderId();
                UserDetails user = RealmRepository.getInstance().getById(UserDetails.class, lastSenderId);
                GlideHelper.loadAvatar(mActivity, chatPicture, user);
            } else {
                GlideHelper.loadPlceholder(mActivity, chatPicture);
            }
        }
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
        ArrayList<Message> messages = sortMessages(chat.getMessages());
        long lastMessageTime;
        if (messages.size() > 0){
            lastMessageTime = messages.get(messages.size()-1).getTimeInMills();
        } else {
            lastMessageTime = System.currentTimeMillis();
        }

        String formattedDate = SimpleDateUtils.formatTime(mActivity, lastMessageTime);
        holder.lastMessageTime.setText(formattedDate);
    }

    private void setLastMessage(ViewHolder holder, Chat chat) {
        ArrayList<Message> messages = sortMessages(chat.getMessages());
        int messageCount = messages.size();
        if (messageCount > 0){
            holder.lastMessage.setText(messages.get(messages.size()-1).getMessage());
        } else {
            holder.lastMessage.setText("");
        }
    }

    private void setChatName(ViewHolder holder, Chat chat) {
        if (chat.getChatName().isEmpty()){
            holder.chatName.setText(mActivity.getString(R.string.nameless));
        } else {
            holder.chatName.setText(chat.getChatName());
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    private ArrayList sortMessages(RealmList<Message> messages) {
        RealmResults<Message> results = messages.sort(Message.TIME_IN_MILLS, Sort.ASCENDING);
        return new ArrayList<>(results);
    }

    public void setData(ArrayList<Chat> data) {
        mChats = data;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        LinearLayout itemContainer;
        ImageView chatPicture;
        TextView chatName;
        TextView lastMessage;
        TextView timeGroup;
        TextView lastMessageTime;
        TextView newMessageCounter;
        AppCompatImageView groupChatIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            itemContainer = (LinearLayout) itemView.findViewById(R.id.item_container);
            chatPicture = (ImageView) itemView.findViewById(R.id.iv_chat_icon);
            chatName = (TextView) itemView.findViewById(R.id.tv_chat_name);
            lastMessage = (TextView) itemView.findViewById(R.id.tv_last_message);
            timeGroup = (TextView) itemView.findViewById(R.id.tv_time_group);
            lastMessageTime = (TextView) itemView.findViewById(R.id.tv_last_message_time);
            newMessageCounter = (TextView) itemView.findViewById(R.id.tv_new_message_count);
            groupChatIcon = (AppCompatImageView) itemView.findViewById(R.id.compativ_group);

            itemContainer.setOnClickListener(this);
            itemContainer.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(mActivity, ChatActivity.class);
            Chat selectedChat = mChats.get(getAdapterPosition());
            final int selectedChatId = selectedChat.getId();
            i.putExtra(ChatActivity.CHAT_ID_EXTRA, selectedChatId);
            mActivity.startActivity(i);
        }

        @Override
        public boolean onLongClick(View v) {
            AppUser appUser = RealmHelper.getCurrentUser();
            Chat selectedChat = mChats.get(getAdapterPosition());
            ArrayList<Integer> options = new ArrayList<>();
            options.add(R.string.delete_chat_history);
            options.add(R.string.mark_as_read);
            if (selectedChat.getChatMembers().contains(appUser.getUserDetails()) &&
                    !selectedChat.isDialog()){
                options.add(R.string.leave_chat);
            }
            int[] menuItemIds = new int[options.size()];
            for (int i = 0; i < options.size(); i++){
                menuItemIds[i] = options.get(i);
            }

            DialogFragment menuDialog = MenuDialog.newInstance(menuItemIds, selectedChat.getId(), getAdapterPosition(), selectedChat.getChatName());
            menuDialog.show(((FragmentActivity)mActivity).getSupportFragmentManager(), MainActivity.CHAT_MENU_DIALOG);
            return true;
        }
    }
}
