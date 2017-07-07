package com.rammstein.messenger.adapter.base;

import android.app.Activity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rammstein.messenger.R;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.util.GlideHelper;
import com.rammstein.messenger.util.InternetHelper;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by user on 12.05.2017.
 */

public abstract class BasicContactsAdapter extends RecyclerView.Adapter<BasicContactsAdapter.ViewHolder> {

    private final static String TAG = "adapter";
    protected ArrayList<UserDetails> mUserDetails;
    protected Activity mActivity;
    protected RealmRepository mRealmRepository;

    private BasicContactsAdapter(){
        mRealmRepository = RealmRepository.getInstance();
    }

    public BasicContactsAdapter(Activity activity, ArrayList<UserDetails> userDetails){
        this();
        mActivity = activity;
        mUserDetails = userDetails;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserDetails userDetails = mUserDetails.get(position);
        setUsername(holder, userDetails);
        setStatus(holder, userDetails);
        setFirstLetter(holder, position, userDetails);
        setUserAvatar(holder.userPicture, userDetails);
        setIsOnline(holder.isOnline, userDetails);
    }

    private void setIsOnline(AppCompatImageView isOnline, UserDetails userDetails) {
        Log.i("contacts_adapter", "userDetails.isOnline() = " + userDetails.isOnline());
        if (InternetHelper.isConnected() && userDetails.isOnline()){
            isOnline.setVisibility(View.VISIBLE);
        } else {
            isOnline.setVisibility(GONE);
        }
    }

    protected void setUserAvatar(ImageView userPicture, UserDetails userDetails){
        GlideHelper.loadAvatar(mActivity, userPicture, userDetails);

    }


    protected void setUsername(ViewHolder holder, UserDetails userDetails) {
        holder.userName.setText(String.format("%s %s", userDetails.getFirstName(), userDetails.getLastName()));
    }

    protected void setStatus(ViewHolder holder, UserDetails userDetails) {
        holder.additionalInfo.setVisibility(View.INVISIBLE);
        //TODO add something?
    }

    protected void setFirstLetter(ViewHolder holder, int position, UserDetails userDetails) {
        char firstLetterCurrent = userDetails.getFirstName().toUpperCase().charAt(0);
        if (position == 0){
            holder.nameGroup.setVisibility(View.VISIBLE);
            holder.nameGroup.setText(String.valueOf(firstLetterCurrent));
        } else {
            UserDetails previousUserDetails = mUserDetails.get(position - 1);
            char firstLetterPrevious = previousUserDetails.getFirstName().toUpperCase().charAt(0);
            if (firstLetterCurrent == firstLetterPrevious){
                holder.nameGroup.setVisibility(GONE);
            } else {
                holder.nameGroup.setVisibility(View.VISIBLE);
                holder.nameGroup.setText(String.valueOf(firstLetterCurrent));
            }
        }
    }

    public void setData(ArrayList<UserDetails> userDetails){
        mUserDetails = userDetails;
    }


    @Override
    public int getItemCount() {
        return mUserDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout itemContainer;
        protected ImageView userPicture;
        protected TextView userName;
        protected TextView additionalInfo;
        protected TextView nameGroup;
        protected AppCompatImageView isOnline;

        public ViewHolder(View itemView) {
            super(itemView);

            itemContainer = (LinearLayout) itemView.findViewById(R.id.item_container);
            userPicture = (ImageView) itemView.findViewById(R.id.iv_user_picture);
            userName = (TextView) itemView.findViewById(R.id.tv_username);
            additionalInfo = (TextView) itemView.findViewById(R.id.tv_status);
            nameGroup = (TextView) itemView.findViewById(R.id.tv_name_group);
            isOnline = (AppCompatImageView) itemView.findViewById(R.id.compativ_online);
        }

        public LinearLayout getItemContainer() {
            return itemContainer;
        }

        public ImageView getUserPicture() {
            return userPicture;
        }

        public TextView getUserName() {
            return userName;
        }

        public TextView getAdditionalInfo() {
            return additionalInfo;
        }

        public TextView getNameGroup() {
            return nameGroup;
        }
    }
}
