package com.rammstein.messenger.web.retrofit.api;

import com.rammstein.messenger.model.web.request.SendMessageRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by user on 28.06.2017.
 */

public interface MessageApi {
    @POST("api/messages")
    @Headers("Content-Type:application/json")
    Call<Integer> sendMessage(@Header("Authorization") String token, @Body SendMessageRequest body);
}
