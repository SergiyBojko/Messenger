package com.rammstein.messenger.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.Request;
import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.SpinnerWithHeaderAdapter;
import com.rammstein.messenger.fragment.dialog.DatePickerDialogFragment;
import com.rammstein.messenger.fragment.dialog.MenuDialog;
import com.rammstein.messenger.fragment.dialog.TextInputDialogFragment;
import com.rammstein.messenger.model.AppUser;
import com.rammstein.messenger.model.Gender;
import com.rammstein.messenger.model.UserDetails;
import com.rammstein.messenger.repository.TestAppUserRepository;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.rammstein.messenger.activity.MainActivity.BIRTHDAY_PICKER_DIALOG;
import static com.rammstein.messenger.activity.MainActivity.FIRST_NAME_INPUT_DIALOG;
import static com.rammstein.messenger.activity.MainActivity.IMAGE_SOURCE_PICKER_DIALOG;
import static com.rammstein.messenger.activity.MainActivity.LAST_NAME_INPUT_DIALOG;
import static com.rammstein.messenger.activity.MainActivity.ACTION_UPDATE_PROFILE_FRAGMENT;

/**
 * Created by user on 19.05.2017.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.sp_gender) Spinner mGenderSelector;
    @BindView(R.id.civ_avatar) CircleImageView mAvatar;
    @BindView(R.id.tv_username)TextView mUsername;
    @BindView(R.id.tv_register_date)TextView mRegisterDate;
    @BindView(R.id.tv_first_name) TextView mFirstName;
    @BindView(R.id.tv_last_name) TextView mLastName;
    @BindView(R.id.tv_date_of_birth) TextView mDateOfBirth;
    private AppUser mAppUser;
    private Unbinder mUnbinder;
    private String[] mGenderArr;
    private BroadcastReceiver mUpdateReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("fragment", "createview");
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        initGenderSpinner();
        return v;
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_PROFILE_FRAGMENT);
        mUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("receiver", "image");
                Bitmap image = intent.getParcelableExtra("image");
                mAvatar.setImageBitmap(image);
            }
        };

        getActivity().registerReceiver(mUpdateReceiver, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserData();
        registerBroadcastReceiver();
        Log.i("fragment", "resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mUpdateReceiver);
        Log.i("fragment", "pause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("fragment", "destroyView");
        mUnbinder.unbind();
    }

    private void setUserData() {
        TestAppUserRepository repository = new TestAppUserRepository();
        mAppUser = repository.get(getCurrentUserId());
        UserDetails ud = mAppUser.getUserDetails();
        mFirstName.setText(ud.getFirstName());
        mLastName.setText(ud.getLastName());
        mDateOfBirth.setText(DateUtils.formatDateTime(getActivity(), ud.getBirthday(), DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR));
        mGenderSelector.setSelection(ud.getGender().ordinal()+1);
        mAvatar.setOnClickListener(this);
        mFirstName.setOnClickListener(this);
        mLastName.setOnClickListener(this);
        mDateOfBirth.setOnClickListener(this);
        GlideUrl url = new GlideUrl("https://pbs.twimg.com/profile_images/664169149002874880/z1fmxo00_400x400.jpg");
        Drawable placeholder = getActivity().getResources().getDrawable(R.mipmap.ic_default_profile);
        Glide.with(getActivity()).load(url).dontAnimate().into(mAvatar);
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
    }

    private int getCurrentUserId() {
        //TODO
        return 0;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String hint;
        DialogFragment inputDialog;
        switch (id){
            case R.id.civ_avatar:
                int[] options = {R.string.gallery, R.string.camera};
                String title = getResources().getString(R.string.select_image_source);
                DialogFragment menuDialog = MenuDialog.newInstance(options, R.id.civ_avatar, title);
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
                inputDialog = DatePickerDialogFragment.newInstance(mAppUser.getUserDetails().getBirthday());
                inputDialog.show(getActivity().getSupportFragmentManager(), BIRTHDAY_PICKER_DIALOG);
                break;

        }
    }
}
