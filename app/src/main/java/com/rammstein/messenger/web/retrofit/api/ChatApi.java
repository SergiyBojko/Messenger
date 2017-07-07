package com.rammstein.messenger.web.retrofit.api;

import com.rammstein.messenger.model.web.request.CreateChatRequest;
import com.rammstein.messenger.model.web.response.ChatResponse;
import com.rammstein.messenger.model.web.response.MessageResponse;
import com.rammstein.messenger.model.web.response.UserDetailsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by user on 27.06.2017.
 */

public interface ChatApi {

    @POST("api/chats")
    @Headers("Content-Type:application/json")
    Call<Integer> createChat(@Header("Authorization") String token, @Body CreateChatRequest body);

    @GET("api/chats")
    @Headers("Content-Type:application/json")
    Call<List<ChatResponse>> getChats(@Header("Authorization") String token);

    @GET("api/chats/{chat_id}")
    @Headers("Content-Type:application/json")
    Call<ChatResponse> getChatById(@Header("Authorization") String token, @Path("chat_id") int chatId);

    @GET("/api/chats/{chat_id}/messages")
    @Headers("Content-Type:application/json")
    Call<List<MessageResponse>> getChatMessages(@Header("Authorization") String token, @Path("chat_id") int chatId, @Query("take") int take, @Query("skip") int skip);

    @GET("/api/chats/{chat_id}/messages")
    @Headers("Content-Type:application/json")
    Call<List<MessageResponse>> getChatMessages(@Header("Authorization") String token, @Path("chat_id") int chatId, @Query("minTime") String minTime);

    @GET("/api/chats/{chat_id}/members")
    @Headers("Content-Type:application/json")
    Call<List<UserDetailsResponse>> getChatMembers(@Header("Authorization") String token, @Path("chat_id") int chatId);

    @POST("/api/chats/{chat_id}/members/{user_id}")
    @Headers("Content-Type:application/json")
    Call<Void> addUserToChat(@Header("Authorization") String token, @Path("chat_id") int chatId, @Path("user_id") int userId);

    @DELETE("/api/chats/{chat_id}/members/{user_id}")
    @Headers("Content-Type:application/json")
    Call<Void> removeUserFromChat(@Header("Authorization") String token, @Path("chat_id") int chatId, @Path("user_id") int userId);

    @DELETE("api/chats/{chat_id}")
    Call<Void> deleteHistory(@Header("Authorization") String token, @Path("chat_id") int chatId);
}
