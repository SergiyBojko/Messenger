package com.rammstein.messenger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rammstein.messenger.R;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 10.05.2017.
 */

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "LogInActivity";
    public final static String EXTRA_ANIMATE = "animate";

    @BindView(R.id.logo_container) LinearLayout mLogoContainer;
    @BindView(R.id.log_in_form_container) LinearLayout mLogInFormContainer;
    @BindView(R.id.btn_log_in) Button mLogInBtn;
    @BindView(R.id.btn_register) Button mRegisterButton;

    int mHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);
        setListeners();
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
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();
                break;
            }
            case R.id.btn_register:{
                Intent i = new Intent(this, RegistrationActivity.class);
                startActivity(i);
                finish();
                break;
            }
        }
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
}
