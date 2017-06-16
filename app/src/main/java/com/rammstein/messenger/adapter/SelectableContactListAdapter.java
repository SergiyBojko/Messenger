package com.rammstein.messenger.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.base.BasicContactsAdapter;
import com.rammstein.messenger.model.UserDetails;

import java.util.ArrayList;

/**
 * Created by user on 08.06.2017.
 */

public class SelectableContactListAdapter extends BasicContactsAdapter {

    private ArrayList<UserDetails> mSelectedContacts;

    public SelectableContactListAdapter(Activity activity, ArrayList<UserDetails> userDetails) {
        super(activity, userDetails);
        mSelectedContacts = new ArrayList<>();

        if (!(activity instanceof OnContactSelectedListener)){
            throw new RuntimeException("Activity " + activity.getClass().getSimpleName()
                    + " must implement interface " + OnContactSelectedListener.class.getSimpleName());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BasicContactsAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        setBackground(holder.getItemContainer(), position);
    }

    private void setBackground(LinearLayout itemContainer, int position) {
        UserDetails contact = mUserDetails.get(position);
        if (mSelectedContacts.contains(contact)){
            itemContainer.setBackgroundColor(mActivity.getResources().getColor(R.color.lightBlue));
        } else {
            itemContainer.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
        }
    }

    class ViewHolder extends BasicContactsAdapter.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            itemContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int index = getAdapterPosition();
            UserDetails selectedContact = mUserDetails.get(index);
            if (mSelectedContacts.contains(selectedContact)){
                mSelectedContacts.remove(selectedContact);
            } else {
                mSelectedContacts.add(selectedContact);
            }
            notifyItemChanged(index);
            ((OnContactSelectedListener)mActivity).onContactSelected(selectedContact, index);
        }
    }

    public ArrayList<UserDetails> getSelectedContacts() {
        return mSelectedContacts;
    }

    public interface OnContactSelectedListener{
        void onContactSelected(UserDetails userDetails, int index);
    }
}
