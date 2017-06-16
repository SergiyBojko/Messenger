package com.rammstein.messenger.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.SpinnerWithHeaderAdapter;
import com.rammstein.messenger.model.AppUser;
import com.rammstein.messenger.model.Gender;
import com.rammstein.messenger.model.UserDetails;
import com.rammstein.messenger.repository.TestAppUserRepository;
import com.rammstein.messenger.repository.TestUserDetailRepository;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by user on 06.06.2017.
 */

public class ProfileDialog extends DialogFragment {

    private final static String USER_ID = "user_id";

    @BindView(R.id.sp_gender)
    Spinner mGenderSelector;
    @BindView(R.id.tv_username)TextView mUsername;
    @BindView(R.id.tv_register_date)TextView mRegisterDate;
    @BindView(R.id.tv_first_name) TextView mFirstName;
    @BindView(R.id.tv_last_name) TextView mLastName;
    @BindView(R.id.tv_date_of_birth) TextView mDateOfBirth;
    private UserDetails mUser;
    private Unbinder mUnbinder;
    private String[] mGenderArr;

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
        setUserData(userId);

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).create();
        return dialog;
    }

    private void setUserData(int userId) {
        TestUserDetailRepository repository = new TestUserDetailRepository();
        mUser = repository.getById(userId);
        if (mUser == null){
            //TODO download from backend
        }
        mUsername.setText(mUser.getUsername());
        mFirstName.setText(mUser.getFirstName());
        mLastName.setText(mUser.getLastName());
        mDateOfBirth.setText(DateUtils.formatDateTime(getActivity(), mUser.getBirthday(), DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR));
        mGenderSelector.setSelection(mUser.getGender().ordinal()+1);
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
