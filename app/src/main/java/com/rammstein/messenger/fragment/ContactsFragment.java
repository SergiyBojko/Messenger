package com.rammstein.messenger.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rammstein.messenger.R;
import com.rammstein.messenger.activity.MainActivity;
import com.rammstein.messenger.adapter.ContactListAdapter;
import com.rammstein.messenger.adapter.base.BasicContactsAdapter;
import com.rammstein.messenger.custom_view.RecyclerViewWithEmptyView;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.util.InternetHelper;
import com.rammstein.messenger.util.RealmHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by user on 12.05.2017.
 */

public class ContactsFragment extends Fragment {
    @BindView(R.id.rv_list) RecyclerViewWithEmptyView mRecyclerrView;
    @BindView(R.id.tv_place_holder) TextView mPlaceHolder;

    private BroadcastReceiver mBroadcastReceiver;
    private RealmResults<UserDetails> mContacts;
    private BasicContactsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this, v);
        mPlaceHolder.setText(R.string.no_contacts_to_display);
        mRecyclerrView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerrView.setEmptyView(mPlaceHolder);
        mContacts = RealmHelper.getSortedContactList();

        return v;
    }



    @Override
    public void onResume() {
        super.onResume();
        setContent();
        /*
        mContacts.addChangeListener(new RealmChangeListener<RealmResults<UserDetails>>() {
            @Override
            public void onChange(RealmResults<UserDetails> userDetailses) {
                setContent();
            }
        });
        */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_UPDATE_VIEW);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("receiver", "updating contacts");
                setContent();
            }
        };

        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void setContent(){
        Log.i("contacts", "set content");
        ArrayList<UserDetails> contacts = new ArrayList<>(mContacts);
        if (mAdapter == null){
            mAdapter = new ContactListAdapter(getActivity(), contacts);
            mRecyclerrView.setAdapter(mAdapter);
        } else {
            mAdapter.setData(contacts);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        //mContacts.removeAllChangeListeners();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
