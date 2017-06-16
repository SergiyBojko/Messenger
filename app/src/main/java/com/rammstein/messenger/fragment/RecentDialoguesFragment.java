package com.rammstein.messenger.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.ChatAdapter;
import com.rammstein.messenger.custom_view.RecyclerViewWithEmptyView;
import com.rammstein.messenger.model.Chat;
import com.rammstein.messenger.repository.Repository;
import com.rammstein.messenger.repository.TestChatRepository;

/**
 * Created by user on 12.05.2017.
 */

public class RecentDialoguesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        RecyclerViewWithEmptyView recyclerView = (RecyclerViewWithEmptyView) v.findViewById(R.id.rv_list);
        TextView placeHolder = (TextView) v.findViewById(R.id.tv_place_holder);
        placeHolder.setText(R.string.no_dialogues_to_display);
        Repository<Chat> repository = new TestChatRepository();
        ChatAdapter adapter = new ChatAdapter(getActivity(), repository.getAll());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setEmptyView(placeHolder);
        recyclerView.setAdapter(adapter);
        return v;
    }
}
