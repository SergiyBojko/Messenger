package com.rammstein.messenger.adapter.base;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rammstein.messenger.R;
import com.rammstein.messenger.model.UserDetails;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 12.05.2017.
 */

public abstract class BasicContactsAdapter extends RecyclerView.Adapter<BasicContactsAdapter.ViewHolder> {

    protected ArrayList<UserDetails> mUserDetails;
    protected Activity mActivity;

    public BasicContactsAdapter(Activity activity, ArrayList<UserDetails> userDetails){
        mActivity = activity;
        mUserDetails = userDetails;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserDetails userDetails = mUserDetails.get(position);
        setUsername(holder, userDetails);
        setStatus(holder, userDetails);
        setFirstLetter(holder, position, userDetails);
    }

    protected void setUsername(ViewHolder holder, UserDetails userDetails) {
        holder.userName.setText(String.format("%s %s", userDetails.getFirstName(), userDetails.getLastName()));
    }

    protected void setStatus(ViewHolder holder, UserDetails userDetails) {
        holder.additionalInfo.setVisibility(View.INVISIBLE);
        //TODO add something
    }

    protected void setFirstLetter(ViewHolder holder, int position, UserDetails userDetails) {
        char firstLetterCurrent = userDetails.getFirstName().charAt(0);
        if (position == 0){
            holder.nameGroup.setVisibility(View.VISIBLE);
            holder.nameGroup.setText(String.valueOf(firstLetterCurrent));
        } else {
            UserDetails previousUserDetails = mUserDetails.get(position - 1);
            char firstLetterPrevious = previousUserDetails.getFirstName().charAt(0);
            if (firstLetterCurrent == firstLetterPrevious){
                holder.nameGroup.setVisibility(View.GONE);
            } else {
                holder.nameGroup.setVisibility(View.VISIBLE);
                holder.nameGroup.setText(String.valueOf(firstLetterCurrent));
            }
        }
    }


    @Override
    public int getItemCount() {
        return mUserDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout itemContainer;
        protected CircleImageView userPicture;
        protected TextView userName;
        protected TextView additionalInfo;
        protected TextView nameGroup;

        public ViewHolder(View itemView) {
            super(itemView);

            itemContainer = (LinearLayout) itemView.findViewById(R.id.item_container);
            userPicture = (CircleImageView) itemView.findViewById(R.id.iv_user_picture);
            userName = (TextView) itemView.findViewById(R.id.tv_username);
            additionalInfo = (TextView) itemView.findViewById(R.id.tv_status);
            nameGroup = (TextView) itemView.findViewById(R.id.tv_name_group);
        }

        public LinearLayout getItemContainer() {
            return itemContainer;
        }

        public CircleImageView getUserPicture() {
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
