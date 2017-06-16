package com.rammstein.messenger.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rammstein.messenger.R;
import com.rammstein.messenger.activity.ChatActivity;
import com.rammstein.messenger.activity.MainActivity;
import com.rammstein.messenger.adapter.base.BasicContactsAdapter;
import com.rammstein.messenger.fragment.dialog.MenuDialog;
import com.rammstein.messenger.model.UserDetails;

import java.util.ArrayList;

/**
 * Created by user on 08.06.2017.
 */

public class ContactListAdapter extends BasicContactsAdapter {
    public ContactListAdapter(Activity activity, ArrayList<UserDetails> userDetails) {
        super(activity, userDetails);
    }

    @Override
    public BasicContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(v);
    }

    class ViewHolder extends BasicContactsAdapter.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ViewHolder(View itemView) {
            super(itemView);

            itemContainer.setOnClickListener(this);
            itemContainer.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Intent i = new Intent(mActivity, ChatActivity.class);
            UserDetails selectedUser = mUserDetails.get(getAdapterPosition());
            int selectedUserId = selectedUser.getId();
            i.putExtra(ChatActivity.USER_ID_EXTRA, selectedUserId);
            mActivity.startActivity(i);
        }

        @Override
        public boolean onLongClick(View v) {
            int[] menuItemIds = {R.string.show_information, R.string.delete};
            UserDetails selectedUser = mUserDetails.get(getAdapterPosition());
            DialogFragment menuDialog = MenuDialog.newInstance(menuItemIds, selectedUser.getId(), selectedUser.getName());
            menuDialog.show(((FragmentActivity)mActivity).getSupportFragmentManager(), MainActivity.CONTACT_MENU_DIALOG);
            return true;
        }
    }
}
