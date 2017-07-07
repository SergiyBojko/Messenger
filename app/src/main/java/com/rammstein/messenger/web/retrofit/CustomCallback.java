package com.rammstein.messenger.web.retrofit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.fragment.dialog.NotificationDialog;
import com.rammstein.messenger.util.InternetHelper;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rammstein.messenger.activity.MainActivity.ACTION_UPDATE_VIEW;

/**
 * Created by user on 01.07.2017.
 */

public abstract class CustomCallback<T> implements Callback<T> {
    private Context mContext;
    private AppCompatActivity mActivity;

    public CustomCallback (Context context, AppCompatActivity activity){
        mContext = context;
        mActivity = activity;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response){
        if (response.code()/100 == 2){
            onSuccess(call, response);
            //todo придумати щось краще
            if (mContext != null){
                mContext.sendBroadcast(new Intent(ACTION_UPDATE_VIEW));
            }
        } else {
            try {
                Log.i("onResponse", response.code() + "\n" + response.errorBody().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Toast.makeText(mContext, response.code() + "\n" + response.errorBody().string(), Toast.LENGTH_LONG).show();
        }

    }

    protected abstract void onSuccess(Call<T> call, Response<T> response);

    @Override
    public void onFailure(@NonNull Call<T> call, Throwable t) {

        t.printStackTrace();
        if (mActivity != null){
            if (!InternetHelper.isConnected()){
                DialogFragment alert = NotificationDialog.newInstance(mContext.getResources().getString(R.string.internet_not_available), null);
                alert.show(mActivity.getSupportFragmentManager(), "internet_not_available");
            } else {
                DialogFragment alert = NotificationDialog.newInstance(mContext.getResources().getString(R.string.error), t.getMessage());
                alert.show(mActivity.getSupportFragmentManager(), "error");
            }
        } else {
            Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
