package com.rammstein.messenger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.SpinnerWithHeaderAdapter;
import com.rammstein.messenger.fragment.dialog.DatePickerDialogFragment;
import com.rammstein.messenger.fragment.dialog.TextInputDialogFragment;
import com.rammstein.messenger.model.Gender;
import com.rammstein.messenger.util.InputValidator;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 19.05.2017.
 */

public class RegistrationActivity extends AppCompatActivity
        implements View.OnClickListener, DatePickerDialogFragment.OnClickListener {

    private static final String BIRTHDAY_PICKER_FRAGMENT = "date_picker";

    @BindView(R.id.et_username)  TextInputEditText mUsername;
    @BindView(R.id.et_first_name) TextInputEditText mFirstName;
    @BindView(R.id.et_last_name) TextInputEditText mLastName;
    @BindView(R.id.et_password) TextInputEditText mPassword;
    @BindView(R.id.et_birthday) TextInputEditText mDateOfBirth;
    @BindView(R.id.fl_birthday_overlay) FrameLayout mDateOfBirthOverlay;
    @BindView(R.id.sp_gender)  Spinner mGenderSelector;
    @BindView(R.id.btn_submit) Button mSubmit;
    private GregorianCalendar mBirthday;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        mDateOfBirthOverlay.setOnClickListener(this);
        mSubmit.setOnClickListener(this);

        initGenderSpinner();
    }

    private void initGenderSpinner() {
        String header = getResources().getString(R.string.gender);
        String[] genderArr;
        ArrayList<String> genderList = new ArrayList<>();
        for (Gender gender : Gender.values()){
            String g = getResources().getString(gender.getTextResId());
            genderList.add(g);
        }
        genderArr = genderList.toArray(new String[genderList.size()]);
        SpinnerWithHeaderAdapter spinnerAdapter = new SpinnerWithHeaderAdapter(this, header, R.color.transparentWhite, genderArr);
        mGenderSelector.setAdapter(spinnerAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, LogInActivity.class);
        i.putExtra(LogInActivity.EXTRA_ANIMATE, false);
        startActivity(i);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fl_birthday_overlay:
                DialogFragment datePikerDialog;
                if (mBirthday == null){
                    datePikerDialog = DatePickerDialogFragment.newInstance(System.currentTimeMillis());
                } else {
                    datePikerDialog = DatePickerDialogFragment.newInstance(mBirthday.getTimeInMillis());
                }

                datePikerDialog.show(getSupportFragmentManager(), BIRTHDAY_PICKER_FRAGMENT);


                break;
            case R.id.btn_submit:
                boolean isValid = verifyInputs();
                if (isValid){
                    submit();
                } else {
                    Toast.makeText(this, "invalid", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void submit() {
        Toast.makeText(this, "send!", Toast.LENGTH_SHORT).show();
    }

    private boolean verifyInputs() {
        InputValidator iv = new InputValidator(this);
        boolean isValid;
        isValid = iv.validateUsername(mUsername);
        isValid &= iv.validateName(mFirstName);
        isValid &= iv.validateName(mLastName);
        isValid &= iv.validatePassword(mPassword);
        if (mBirthday != null){
            isValid &= iv.validateDateOfBirth(mBirthday.getTimeInMillis(), mDateOfBirth);
        }
        return isValid;
    }

    @Override
    public void onOkClicked(String tag, GregorianCalendar date) {
        switch (tag){
            case BIRTHDAY_PICKER_FRAGMENT:
                mBirthday = date;
                int flags = DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR;
                String dateString = DateUtils.formatDateTime(this, date.getTimeInMillis(), flags);
                mDateOfBirth.setText(dateString);
                mDateOfBirth.setError(null);
                break;
        }
    }
}
