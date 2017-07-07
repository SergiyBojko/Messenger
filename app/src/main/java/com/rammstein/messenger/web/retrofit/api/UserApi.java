package com.rammstein.messenger.web.retrofit.api;

import com.rammstein.messenger.model.web.response.UserDetailsResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by user on 28.06.2017.
 */

public interface UserApi {
    @GET("api/users")
    Call<List<UserDetailsResponse>> getUsers(@Header("Authorization") String token, @QueryMap Map<String, String> filters);

    @GET("api/users/{user_id}")
    Call<UserDetailsResponse> getUserById(@Header("Authorization") String token, @Path("user_id") int userId);

    @GET("api/users/{user_id}/path")
    Call<List<UserDetailsResponse>> getPath(@Header("Authorization") String token, @Path("user_id") int userId);
}
