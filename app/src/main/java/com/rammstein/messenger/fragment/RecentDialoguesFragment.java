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
import com.rammstein.messenger.adapter.ChatAdapter;
import com.rammstein.messenger.custom_view.RecyclerViewWithEmptyView;
import com.rammstein.messenger.model.local.Chat;
import com.rammstein.messenger.util.InternetHelper;
import com.rammstein.messenger.util.RealmHelper;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by user on 12.05.2017.
 */

public class RecentDialoguesFragment extends Fragment {

    @BindView(R.id.rv_list) RecyclerViewWithEmptyView mRecyclerView;
    @BindView(R.id.tv_place_holder) TextView mPlaceHolder;

    private Unbinder mUnbinder;
    private ChatAdapter mChatAdapter;
    private RealmResults<Chat> mChats;
    private BroadcastReceiver mBroadcastReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        mPlaceHolder.setText(R.string.no_dialogues_to_display);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setEmptyView(mPlaceHolder);
        mRecyclerView.setTag(true);

        mChats = RealmHelper.getChatList();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        mChats.addChangeListener(new RealmChangeListener<RealmResults<Chat>>() {
            @Override
            public void onChange(RealmResults<Chat> chats) {
                setData();
            }
        });
        */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_UPDATE_VIEW);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("receiver", "updating chats");
                setData();
            }
        };

        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
        setData();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadcastReceiver);
        //mChats.removeAllChangeListeners();
    }

    private void setData() {
        ArrayList<Chat> chats = new ArrayList<>(mChats);
        Collections.sort(chats);
        if (mChatAdapter == null){
            mChatAdapter = new ChatAdapter(getActivity(), chats);
            mRecyclerView.setAdapter(mChatAdapter);
        } else {
            mChatAdapter.setData(chats);
            mChatAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("fragment", "destroyView");
        mUnbinder.unbind();
    }
}
