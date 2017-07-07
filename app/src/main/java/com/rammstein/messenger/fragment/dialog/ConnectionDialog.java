package com.rammstein.messenger.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.SearchResultsAdapter;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.model.web.response.UserDetailsResponse;
import com.rammstein.messenger.web.retrofit.CustomCallback;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

import static android.view.View.GONE;

/**
 * Created by user on 04.07.2017.
 */

public class ConnectionDialog extends DialogFragment {
    private final static String TARGET_USER_ID = "user_id";
    private final static String TARGET_USER_NAME = "user_name";

    @BindView(R.id.rv_list) RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.subtitle) TextView mSubtitle;
    View mView;

    private Unbinder mUnbinder;
    private AlertDialog mAlertDialog;

    private CustomCallback<List<UserDetailsResponse>> mOnPathReceived;

    public static ConnectionDialog newInstance(int id, String name) {

        Bundle args = new Bundle();
        args.putInt(TARGET_USER_ID, id);
        args.putString(TARGET_USER_NAME, name);
        ConnectionDialog fragment = new ConnectionDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int targetUserId = getArguments().getInt(TARGET_USER_ID);
        String userName = getArguments().getString(TARGET_USER_NAME);
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user_connection, null);
        mUnbinder = ButterKnife.bind(this, mView);
        initRetrofitcallbacks();
        getPath(targetUserId);

        mAlertDialog =  new AlertDialog.Builder(getActivity())
                .setTitle(userName)
                .setView(mView)
                .create();

        return mAlertDialog;
    }

    private void getPath(int targetUserId) {
        RetrofitHelper retrofitHelper = new RetrofitHelper(getContext());
        retrofitHelper.getPath(targetUserId, mOnPathReceived);
    }

    private void initRetrofitcallbacks() {
        mOnPathReceived = new CustomCallback<List<UserDetailsResponse>>(getContext(), null) {
            @Override
            protected void onSuccess(Call<List<UserDetailsResponse>> call, Response<List<UserDetailsResponse>> response) {
                if (response.body().size() > 0){
                    ArrayList<UserDetails> users = new ArrayList<>();
                    for (UserDetailsResponse userResponse : response.body()){
                        UserDetails user = new UserDetails(userResponse);
                        users.add(user);
                    }

                    mSubtitle.setText(getResources().getString(R.string.connection_length) + " : " + users.size());
                    SearchResultsAdapter adapter = new SearchResultsAdapter(getActivity(), users);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRecyclerView.setAdapter(adapter);
                    mProgressBar.setVisibility(GONE);
                } else {
                    mSubtitle.setText(getString(R.string.no_connecton));
                    mRecyclerView.setVisibility(GONE);
                    mProgressBar.setVisibility(GONE);
                    mAlertDialog.setView(null);
                }
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
