package com.rammstein.messenger.web.retrofit.api;

import com.rammstein.messenger.model.web.response.UserDetailsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by user on 28.06.2017.
 */

public interface FriendApi {

    @GET("api/friends")
    Call<List<UserDetailsResponse>> getFriends(@Header("Authorization") String token);

    @POST("api/friends/{user_id}")
    Call<Void> addFriend(@Header("Authorization") String token, @Path("user_id") int userId);

    @DELETE("api/friends/{user_id}")
    Call<Void> removeFriend(@Header("Authorization") String token, @Path("user_id") int userId);
}
