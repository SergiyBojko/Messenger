package com.rammstein.messenger.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.SpinnerWithHeaderAdapter;
import com.rammstein.messenger.fragment.dialog.DatePickerDialogFragment;
import com.rammstein.messenger.model.local.Gender;
import com.rammstein.messenger.model.web.request.RegisterRequest;
import com.rammstein.messenger.model.web.response.TokenResponse;
import com.rammstein.messenger.model.web.response.UserDetailsResponse;
import com.rammstein.messenger.repository.SharedPreferencesRepository;
import com.rammstein.messenger.util.InputValidator;
import com.rammstein.messenger.util.RealmHelper;
import com.rammstein.messenger.util.SimpleDateUtils;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rammstein.messenger.activity.ChatActivity.FINISH;

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
    @BindView(R.id.til_username) TextInputLayout mUsernameContainer;
    @BindView(R.id.til_password) TextInputLayout mPasswordContainer;
    @BindView(R.id.til_first_name) TextInputLayout mFirstNameContainer;
    @BindView(R.id.til_last_name) TextInputLayout mLastNameContainer;
    @BindView(R.id.til_birthday) TextInputLayout mBirthdayContainer;
    @BindView(R.id.fl_birthday_overlay) FrameLayout mDateOfBirthOverlay;
    @BindView(R.id.sp_gender)  Spinner mGenderSelector;
    @BindView(R.id.btn_submit) AppCompatButton mSubmit;

    private RetrofitHelper mRetrofitHelper;
    private GregorianCalendar mBirthday;
    private Callback<Void> mRegistrationCallback;
    private Callback<TokenResponse> mGetTokenCallback;
    private Callback<UserDetailsResponse> mGetProfileCallback;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        mRetrofitHelper = new RetrofitHelper();

        mDateOfBirthOverlay.setOnClickListener(this);
        mSubmit.setOnClickListener(this);

        initRetrofitCallbacks();

        initGenderSpinner();
    }

    private void initRetrofitCallbacks() {
        mRegistrationCallback = new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200){
                    Intent i = new Intent();
                    i.putExtra(FINISH, true);
                    setResult(RESULT_OK, i);
                    String username = mUsername.getText().toString();
                    String password = mPassword.getText().toString();
                    mRetrofitHelper.getToken(username, password, mGetTokenCallback);
                } else {
                    mProgressDialog.dismiss();
                    try {
                        JSONObject json = new JSONObject(response.errorBody().string());
                        JSONObject modelState = json.getJSONObject("ModelState");
                        Iterator<String> keys = modelState.keys();
                        while (keys.hasNext()){
                            String key = keys.next();
                            Toast.makeText(RegistrationActivity.this, modelState.getString(key), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                mProgressDialog.dismiss();
            }
        };

        mGetTokenCallback = new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.code() == 200){
                    TokenResponse tokenResponse = response.body();
                    String token = String.format("%s %s", tokenResponse.getToken_type(), tokenResponse.getAccess_token());
                    mRetrofitHelper.getUserProfile(token, mGetProfileCallback);
                } else {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                t.printStackTrace();
                mProgressDialog.dismiss();
            }
        };

        mGetProfileCallback = new Callback<UserDetailsResponse>() {
            @Override
            public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                if (response.code() == 200){
                    String password = mPassword.getText().toString();
                    RealmHelper.addOrUpdateAppUser(mUsername.getText().toString(), response, call.request().header("Authorization"), password);
                    SharedPreferencesRepository.getInstance().setCurrentUserId(response.body().getId());
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    finish();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                t.printStackTrace();
                mProgressDialog.dismiss();
            }
        };
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
                }
                break;
        }
    }

    private void submit() {
        RetrofitHelper helper = new RetrofitHelper();
        RegisterRequest request = new RegisterRequest();
        request.setUserName(mUsername.getText().toString());
        request.setPassword(mPassword.getText().toString());
        request.setConfirmPassword(mPassword.getText().toString());
        request.setFirstName(mFirstName.getText().toString());
        request.setLastName(mLastName.getText().toString());
        String birthday = SimpleDateUtils.formatDateShort(mBirthday);
        request.setDateOfBirth(birthday);
        int genderId = getGenderId();
        request.setGender(Integer.toString(genderId));
        helper.register(request, mRegistrationCallback);
        showProgressDialog();
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.please_wait);
        mProgressDialog.setMessage(getResources().getString(R.string.creating_account));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private int getGenderId() {
        int genderId = 0;
        int itemPosition = mGenderSelector.getSelectedItemPosition();

        switch (itemPosition){
            case 0:
                genderId = 2;
                break;
            case 1:
                genderId = 0;
                break;
            case 2:
                genderId = 1;
                break;
        }
        return genderId;
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
