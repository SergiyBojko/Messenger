package com.rammstein.messenger.web.retrofit.api;

import com.rammstein.messenger.model.web.response.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by user on 27.06.2017.
 */

public interface Api {

    @FormUrlEncoded
    @POST("token")
    Call<TokenResponse> getToken(@Field("grant_type") String grantType, @Field("userName") String username, @Field("password") String password);
}
