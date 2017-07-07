package com.rammstein.messenger.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.web.response.TokenResponse;
import com.rammstein.messenger.model.web.response.UserDetailsResponse;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.repository.SharedPreferencesRepository;
import com.rammstein.messenger.util.RealmHelper;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rammstein.messenger.activity.ChatActivity.FINISH;

/**
 * Created by user on 10.05.2017.
 */

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "LogInActivity";
    public final static String EXTRA_ANIMATE = "animate";
    private static final int REGISTER = 0;

    @BindView(R.id.logo_container) LinearLayout mLogoContainer;
    @BindView(R.id.log_in_form_container) LinearLayout mLogInFormContainer;
    @BindView(R.id.et_username) TextInputEditText mUsername;
    @BindView(R.id.et_password) TextInputEditText mPassword;
    @BindView(R.id.btn_log_in) AppCompatButton mLogInBtn;
    @BindView(R.id.btn_register) AppCompatButton mRegisterButton;

    int mHeight;
    RetrofitHelper mRetrofitHelper;
    ProgressDialog mProgressDialog;
    private Callback<TokenResponse> mGetTokenCallback;
    private Callback<UserDetailsResponse> mGetProfileCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);
        mRetrofitHelper = new RetrofitHelper();
        setListeners();
        initRetrofitCallbacks();
        initProgressDialog();

        AppUser appUser = RealmHelper.getCurrentUser();
        if (appUser != null){
            startActivity(new Intent(LogInActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initRetrofitCallbacks() {
        mGetTokenCallback = new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.code() == 200){
                    TokenResponse tokenResponse = response.body();
                    String token = String.format("%s %s", tokenResponse.getToken_type(), tokenResponse.getAccess_token());
                    mRetrofitHelper.getUserProfile(token, mGetProfileCallback);
                } else {
                    mProgressDialog.dismiss();
                    try {
                        Toast.makeText(LogInActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                    startActivity(new Intent(LogInActivity.this, MainActivity.class));
                    finish();
                } else {
                    try {
                        Toast.makeText(LogInActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    private void setListeners() {
        mLogInBtn.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        boolean animate = getIntent().getBooleanExtra(EXTRA_ANIMATE, true);
        if (animate){
            mLogInFormContainer.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mHeight = mLogInFormContainer.getHeight();
                            animateLogo();
                            animateForm();
                            mLogInFormContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
            );
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_log_in:{
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                mRetrofitHelper.getToken(username, password, mGetTokenCallback);
                mProgressDialog.show();
                break;
            }
            case R.id.btn_register:{
                Intent i = new Intent(this, RegistrationActivity.class);
                startActivityForResult(i, REGISTER);
                finish();
                break;
            }
        }
    }

    private void initProgressDialog(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.please_wait);
        mProgressDialog.setMessage(getResources().getString(R.string.logging_in));
        mProgressDialog.setCancelable(false);
    }

    private void animateLogo() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, mHeight/2, 0);
        translateAnimation.setDuration(300);
        translateAnimation.setStartOffset(500);
        translateAnimation.setInterpolator(new DecelerateInterpolator());
        mLogoContainer.startAnimation(translateAnimation);
    }

    private void animateForm() {
        AnimationSet formAnimation = new AnimationSet(false);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, mHeight/2, 0);
        translateAnimation.setDuration(300);
        translateAnimation.setStartOffset(500);
        translateAnimation.setInterpolator(new DecelerateInterpolator());
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(300);
        alphaAnimation.setStartOffset(500);
        formAnimation.addAnimation(translateAnimation);
        formAnimation.addAnimation(alphaAnimation);
        mLogInFormContainer.startAnimation(formAnimation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REGISTER:
                    boolean finish = data.getBooleanExtra(FINISH, false);
                    if (finish){
                        finish();
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
