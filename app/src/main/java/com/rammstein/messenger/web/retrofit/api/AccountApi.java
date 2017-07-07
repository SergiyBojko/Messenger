package com.rammstein.messenger.web.retrofit.api;

import com.rammstein.messenger.model.web.request.RegisterRequest;
import com.rammstein.messenger.model.web.response.UserDetailsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by user on 27.06.2017.
 */

public interface AccountApi {

    @POST("api/account/register")
    @Headers("Content-Type:application/json")
    Call<Void> register(@Body RegisterRequest body);

    @GET("api/account/profile")
    Call<UserDetailsResponse> getUserProfile(@Header("Authorization") String token);
}
