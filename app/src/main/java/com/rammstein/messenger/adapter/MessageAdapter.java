package com.rammstein.messenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.transcode.BitmapToGlideDrawableTranscoder;
import com.rammstein.messenger.R;
import com.rammstein.messenger.model.local.Message;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.util.GlideHelper;
import com.rammstein.messenger.util.RealmHelper;
import com.rammstein.messenger.util.SimpleDateUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by user on 29.05.2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int USER_MESSAGE = 0;
    private final static int RECEIVED_MESSAGE = 1;
    //private final static String URL = "https?:\\/\\/(?![^\" ]*(?:jpg|png|gif|jpeg|svg))[^\" \\n]+";
    private final static String IMAGE_URL = "(https?://[^\" ]*\\.(?:png|jpg|jpeg|svg|gif))";
    //private final static String GIF_URL = "(https?://[^\" ]*\\.(?:gif))";


    private Context mContext;
    private ArrayList<Message> mMessages;
    private Pattern mHttpLinkPattern;
    private Pattern mImageLinkPattern;
    private Pattern mGifLinkPattern;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        mContext = context;
        mMessages = messages;
        //mHttpLinkPattern = Pattern.compile(URL);
        mImageLinkPattern = Pattern.compile(IMAGE_URL);
        //mGifLinkPattern = Pattern.compile(GIF_URL);
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
        return RealmHelper.getCurrentUser().getId();
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
        boolean showAdditionalInfo = getShowAdditionalInfo(position);
        bindCommonData((ViewHolderBasicMessage) holder, position, showAdditionalInfo);
        switch (viewType){
            case USER_MESSAGE:
                bindUserMessageViewHolder((ViewHolderUserMessage)holder, position, showAdditionalInfo);
                break;
            case RECEIVED_MESSAGE:
                bindReceivedMessageViewHolder((ViewHolderReceivedMessage)holder, position, showAdditionalInfo);
                break;
        }
    }

    private boolean getShowAdditionalInfo(int position) {
        Message message = mMessages.get(position);
        boolean show = false;
        if (position == 0){
            show = true;
        } else {
            Message previousMessage = mMessages.get(position-1);
            if (message.getSenderId() == previousMessage.getSenderId()){
                show = false;
            } else {
                show = true;
            }
        }
        return show;
    }

    private void bindCommonData(ViewHolderBasicMessage holder, int position, boolean showAdditionalInfo) {
        Message message = mMessages.get(position);
        setMessageContent(holder, message);
        setMessageTime(holder, message, position, showAdditionalInfo);
    }



    private void bindUserMessageViewHolder(ViewHolderUserMessage holder, int position, boolean showAdditionalInfo) {
        //configure  app user message here
    }

    private void bindReceivedMessageViewHolder(ViewHolderReceivedMessage holder, int position, boolean showAdditionalInfo) {
        Message message = mMessages.get(position);
        setUsername(holder, message, showAdditionalInfo);
        setAvatar(holder.mAvatar, message.getSenderId(), showAdditionalInfo);
    }

    private void setAvatar(ImageView avatar, int senderId, boolean showAvatar) {
        if (showAvatar){
            avatar.setVisibility(VISIBLE);
            UserDetails user = RealmRepository.getInstance().getById(UserDetails.class, senderId);
            GlideHelper.loadAvatar(mContext, avatar, user);
        } else {
            avatar.setVisibility(View.GONE);
        }

    }

    private void setMessageContent(final ViewHolderBasicMessage holder, Message message) {
        String text = message.getMessage().trim();
        //Matcher httpMatcher = mHttpLinkPattern.matcher(text);
        Matcher imageMatcher = mImageLinkPattern.matcher(text);
        //Matcher gifMatcher = mGifLinkPattern.matcher(text);

        holder.mMessageText.setVisibility(VISIBLE);
        holder.mImageContainer.setVisibility(VISIBLE);
        if (imageMatcher.find()){
            if (imageMatcher.start() == 0 && imageMatcher.end() == text.length()){
                holder.mMessageText.setVisibility(GONE);
            }
            final String imageUrl = imageMatcher.group();
            Glide.with(mContext)
                    .load(imageUrl)
                    .asBitmap()
                    .transcode(new BitmapToGlideDrawableTranscoder(mContext), GlideDrawable.class)
                    .thumbnail(Glide.with(mContext).load(imageUrl).thumbnail(0.1f))
                    .into(holder.mImage);
            holder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
                    mContext.startActivity(i);
                }
            });
        } else {
            holder.mImageContainer.setVisibility(GONE);
        }
        holder.mMessageText.setText(text);
    }

    private void setMessageTime(final ViewHolderBasicMessage holder, Message message, int position, boolean showTime) {
        long messageTime = message.getTimeInMills();
        long previousMessageTime = -1;
        if (position > 0){
            Message previousMessage = mMessages.get(position - 1);
            previousMessageTime = previousMessage.getTimeInMills();
        }


        if (showTime || (previousMessageTime != -1 && (messageTime - previousMessageTime) > 60000)){
            holder.mMessageTime.setVisibility(VISIBLE);
            String time = SimpleDateUtils.formatTime(mContext, messageTime, DateUtils.FORMAT_SHOW_TIME);
            holder.mMessageTime.setText(time);
        } else {
            holder.mMessageTime.setVisibility(View.GONE);
        }

    }

    private void setUsername(ViewHolderReceivedMessage holder, Message message, boolean showUsername) {
        if (showUsername){
            RealmRepository realmRepository = RealmRepository.getInstance();
            int senderId = message.getSenderId();
            UserDetails sender = realmRepository.getById(UserDetails.class, senderId);
            holder.mUsername.setVisibility(VISIBLE);
            holder.mUsername.setText(sender.getName());
        } else {
            holder.mUsername.setVisibility(GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void setData(ArrayList<Message> list) {
        mMessages = list;
    }

    class ViewHolderReceivedMessage extends ViewHolderBasicMessage{
        @BindView(R.id.iv_avatar) ImageView mAvatar;
        @BindView(R.id.tv_username) TextView mUsername;
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
        @BindView(R.id.image_container) FrameLayout mImageContainer;
        @BindView(R.id.image) ImageView mImage;
        public ViewHolderBasicMessage(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
