package com.rammstein.messenger.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.SpinnerWithHeaderAdapter;
import com.rammstein.messenger.model.local.Gender;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.model.web.response.UserDetailsResponse;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.util.GlideHelper;
import com.rammstein.messenger.util.InternetHelper;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

/**
 * Created by user on 06.06.2017.
 */

public class ProfileDialog extends DialogFragment {

    private final static String USER_ID = "user_id";

    @BindView(R.id.fragment_container) LinearLayout mFragmentContainer;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.sp_gender)Spinner mGenderSelector;
    @BindView(R.id.tv_username)TextView mUsername;
    @BindView(R.id.tv_username_hint) TextView mUsernameHint;
    @BindView(R.id.tv_register_date)TextView mRegisterDate;
    @BindView(R.id.tv_first_name) TextView mFirstName;
    @BindView(R.id.tv_last_name) TextView mLastName;
    @BindView(R.id.tv_date_of_birth) TextView mDateOfBirth;
    @BindView(R.id.iv_avatar) ImageView mAvatar;
    private UserDetails mUser;
    private Unbinder mUnbinder;
    private String[] mGenderArr;
    private Callback<UserDetailsResponse> mOnUserReceived;

    public static ProfileDialog newInstance(int userId) {

        Bundle args = new Bundle();
        args.putInt(USER_ID, userId);

        ProfileDialog fragment = new ProfileDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int userId = getArguments().getInt(USER_ID);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile, null);
        mUnbinder = ButterKnife.bind(this, v);
        initGenderSpinner();
        initCallbacks();
        getUserData(userId);
        setUserData();
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).create();
        return dialog;
    }

    private void initCallbacks() {
        mOnUserReceived = new Callback<UserDetailsResponse>() {
            @Override
            public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                if (response.code()/100 == 2){
                    mUser = new UserDetails(response.body());
                    setUserData();
                } else {
                    dismiss();
                    try {
                        Toast.makeText(getActivity(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                t.printStackTrace();
                NotificationDialog.newInstance(getString(R.string.internet_not_available), "")
                        .show(getActivity().getSupportFragmentManager(), "notification");
                dismiss();
            }
        };
    }

    private void getUserData(int userId) {
        RealmRepository repository = RealmRepository.getInstance();
        mUser = repository.getById(UserDetails.class, userId);
        if (InternetHelper.isConnected()){
            RetrofitHelper helper = new RetrofitHelper();
            helper.getUserById(userId, mOnUserReceived);
        }
    }

    private void setUserData() {
        if (mUser != null) {
            mFragmentContainer.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(GONE);
            mUsername.setVisibility(GONE);
            mUsernameHint.setVisibility(GONE);
            mFirstName.setText(mUser.getFirstName());
            mLastName.setText(mUser.getLastName());
            if (mUser.getRegistrationDate() != null){
                mRegisterDate.setText(DateUtils.formatDateTime(getActivity(), mUser.getRegistrationDate().getTime(), DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR));
            } else {
                mRegisterDate.setText("No data");
            }
            String birthday;
            if (mUser.getBirthday() != null){
                birthday = DateUtils.formatDateTime(getActivity(), mUser.getBirthday().getTime(), DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR);
            } else {
                birthday = "No data";
            }
            mDateOfBirth.setText(birthday);
            Gender gender = mUser.getGender();
            if (gender != null){
                mGenderSelector.setSelection(gender.ordinal()+1);
            } else {
                String header = getResources().getString(R.string.unknown);
                ((SpinnerWithHeaderAdapter)mGenderSelector.getAdapter()).setHeader(header);
            }
            GlideHelper.loadAvatar(getActivity(), mAvatar, mUser);
        } else {
            mFragmentContainer.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void initGenderSpinner() {
        String header = getResources().getString(R.string.gender);
        ArrayList<String> genderList = new ArrayList<>();
        for (Gender gender : Gender.values()){
            String g = getResources().getString(gender.getTextResId());
            genderList.add(g);
        }
        mGenderArr = genderList.toArray(new String[genderList.size()]);
        SpinnerWithHeaderAdapter spinnerAdapter = new SpinnerWithHeaderAdapter(getActivity(), header, R.color.transparentGrey, mGenderArr);
        mGenderSelector.setAdapter(spinnerAdapter);
        mGenderSelector.setEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
