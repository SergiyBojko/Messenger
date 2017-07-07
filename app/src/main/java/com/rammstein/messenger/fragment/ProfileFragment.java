package com.rammstein.messenger.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.SpinnerWithHeaderAdapter;
import com.rammstein.messenger.fragment.dialog.DatePickerDialogFragment;
import com.rammstein.messenger.fragment.dialog.MenuDialog;
import com.rammstein.messenger.fragment.dialog.TextInputDialogFragment;
import com.rammstein.messenger.model.local.Gender;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.repository.SharedPreferencesRepository;
import com.rammstein.messenger.util.GlideHelper;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;

import static com.rammstein.messenger.activity.MainActivity.ACTION_UPDATE_VIEW;
import static com.rammstein.messenger.activity.MainActivity.BIRTHDAY_PICKER_DIALOG;
import static com.rammstein.messenger.activity.MainActivity.FIRST_NAME_INPUT_DIALOG;
import static com.rammstein.messenger.activity.MainActivity.IMAGE_SOURCE_PICKER_DIALOG;
import static com.rammstein.messenger.activity.MainActivity.LAST_NAME_INPUT_DIALOG;

/**
 * Created by user on 19.05.2017.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    @BindView(R.id.sp_gender) Spinner mGenderSelector;
    @BindView(R.id.iv_avatar) ImageView mAvatar;
    @BindView(R.id.tv_username)TextView mUsername;
    @BindView(R.id.tv_register_date)TextView mRegisterDate;
    @BindView(R.id.tv_first_name) TextView mFirstName;
    @BindView(R.id.tv_last_name) TextView mLastName;
    @BindView(R.id.tv_date_of_birth) TextView mDateOfBirth;
    private UserDetails mAppUserDetails;
    private Unbinder mUnbinder;
    private String[] mGenderArr;
    private BroadcastReceiver mUpdateReceiver;
    private RealmRepository mRepository;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("fragment", "createview");
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        initGenderSpinner();
        getUserData();

        mAvatar.setOnClickListener(this);
        mFirstName.setOnClickListener(this);
        mLastName.setOnClickListener(this);
        mDateOfBirth.setOnClickListener(this);
        mGenderSelector.setOnItemSelectedListener(this);
        return v;
    }

    private void getUserData() {
        mRepository = RealmRepository.getInstance();
        mAppUserDetails = mRepository.getById(UserDetails.class, getCurrentUserId());
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_VIEW);
        mUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setUserData();
            }
        };

        getActivity().registerReceiver(mUpdateReceiver, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserData();
        registerBroadcastReceiver();
        mAppUserDetails.addChangeListener(new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel realmModel) {
                setUserData();
            }
        });
        Log.i("fragment", "resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mUpdateReceiver);
        mAppUserDetails.removeAllChangeListeners();
        Log.i("fragment", "pause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("fragment", "destroyView");
        mUnbinder.unbind();
    }

    private void setUserData() {
        mUsername.setText(mAppUserDetails.getUsername());
        mFirstName.setText(mAppUserDetails.getFirstName());
        mLastName.setText(mAppUserDetails.getLastName());
        if (mAppUserDetails.getRegistrationDate() != null){
            mRegisterDate.setText(DateUtils.formatDateTime(getActivity(), mAppUserDetails.getRegistrationDate().getTime(), DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR));
        } else {
            mRegisterDate.setText("No data");
        }
        String birthday;
        if (mAppUserDetails.getBirthday() != null){
            birthday = DateUtils.formatDateTime(getActivity(), mAppUserDetails.getBirthday().getTime(), DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR);
        } else {
            birthday = "No data";
        }
        mDateOfBirth.setText(birthday);
        Gender gender = mAppUserDetails.getGender();
        if (gender != null){
            mGenderSelector.setSelection(gender.ordinal()+1);
        }

        GlideHelper.loadAvatar(getActivity(), mAvatar, mAppUserDetails);
    }

    private void initGenderSpinner() {
        String header = getResources().getString(R.string.not_selected);
        ArrayList<String> genderList = new ArrayList<>();
        for (Gender gender : Gender.values()){
            String g = getResources().getString(gender.getTextResId());
            genderList.add(g);
        }
        mGenderArr = genderList.toArray(new String[genderList.size()]);
        SpinnerWithHeaderAdapter spinnerAdapter = new SpinnerWithHeaderAdapter(getActivity(), header, R.color.grey, mGenderArr);
        mGenderSelector.setAdapter(spinnerAdapter);
    }

    private int getCurrentUserId() {
        return SharedPreferencesRepository.getInstance().getCurrentUserId();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String hint;
        DialogFragment inputDialog;
        switch (id){
            case R.id.iv_avatar:
                int[] options = {R.string.gallery, R.string.camera};
                String title = getResources().getString(R.string.select_image_source);
                DialogFragment menuDialog = MenuDialog.newInstance(options, R.id.iv_avatar, 0, title);
                menuDialog.show(getActivity().getSupportFragmentManager(), IMAGE_SOURCE_PICKER_DIALOG);
                break;
            case R.id.tv_first_name:
                hint = getActivity().getResources().getString(R.string.first_name);
                inputDialog = TextInputDialogFragment.newInstance(hint, mFirstName.getText().toString());
                inputDialog.show(getActivity().getSupportFragmentManager(), FIRST_NAME_INPUT_DIALOG);
                break;
            case R.id.tv_last_name:
                hint = getActivity().getResources().getString(R.string.last_name);
                inputDialog = TextInputDialogFragment.newInstance(hint, mLastName.getText().toString());
                inputDialog.show(getActivity().getSupportFragmentManager(), LAST_NAME_INPUT_DIALOG);
                break;
            case R.id.tv_date_of_birth:
                Date birthday = mAppUserDetails.getBirthday();
                long birthdayMills;
                if (birthday != null){
                   birthdayMills = birthday.getTime();
                } else {
                    birthdayMills = System.currentTimeMillis();
                }

                inputDialog = DatePickerDialogFragment.newInstance(birthdayMills);
                inputDialog.show(getActivity().getSupportFragmentManager(), BIRTHDAY_PICKER_DIALOG);
                break;


        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.sp_gender:
                //TODO send to backend
                Gender gender;
                Gender userGender = mAppUserDetails.getGender();
                if (position == 0){
                    gender = null;
                } else {
                    gender = Gender.values()[position-1];
                }
                if (gender != userGender){
                    mRepository.beginTransaction();
                    mAppUserDetails.setGender(gender);
                    mRepository.commitTransaction();
                }
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
