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
import com.rammstein.messenger.adapter.base.BasicContactsAdapter;
import com.rammstein.messenger.fragment.dialog.MenuDialog;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.util.RealmHelper;

import java.util.ArrayList;

import io.realm.RealmResults;

import static com.rammstein.messenger.activity.MainActivity.CONTACT_MENU_DIALOG;

/**
 * Created by user on 13.06.2017.
 */

public class SearchResultsAdapter extends BasicContactsAdapter{

    private RealmResults<UserDetails> currentFriends;

    public SearchResultsAdapter(Activity activity, ArrayList<UserDetails> userDetails) {
        super(activity, userDetails);
        currentFriends = RealmHelper.getSortedContactList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BasicContactsAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    protected void setFirstLetter(BasicContactsAdapter.ViewHolder holder, int position, UserDetails userDetails) {
        holder.getNameGroup().setVisibility(View.GONE);
    }

    @Override
    protected void setStatus(BasicContactsAdapter.ViewHolder holder, UserDetails userDetails) {
        String text;
        if (currentFriends.contains(userDetails)){
            text = "In contact list";
        } else{
            text = "";
        }


        holder.getAdditionalInfo().setText(text);
    }

    class ViewHolder extends BasicContactsAdapter.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            itemContainer.setOnClickListener(this);
            itemContainer.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Intent i = new Intent(mActivity, ChatActivity.class);
            i.putExtra(ChatActivity.USER_NAME_EXTRA, mUserDetails.get(getAdapterPosition()).getName());
            i.putExtra(ChatActivity.USER_ID_EXTRA, mUserDetails.get(getAdapterPosition()).getId());
            mActivity.startActivity(i);
        }

        @Override
        public boolean onLongClick(View v) {
            UserDetails userDetails = mUserDetails.get(getAdapterPosition());
            int[] options;
            if (currentFriends.contains(userDetails)){
                options = new int[]{R.string.show_information, R.string.delete_contact};
            } else {
                options = new int[]{R.string.show_information, R.string.add_to_contacts, R.string.show_connection};
            }

            DialogFragment dialog = MenuDialog.newInstance(options, userDetails.getId(), getAdapterPosition(), userDetails.getName());
            dialog.show(((FragmentActivity)mActivity).getSupportFragmentManager(), CONTACT_MENU_DIALOG);
            return false;
        }
    }
}
