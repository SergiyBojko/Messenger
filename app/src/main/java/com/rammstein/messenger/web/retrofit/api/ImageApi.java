package com.rammstein.messenger.web.retrofit.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by user on 28.06.2017.
 */

public interface ImageApi {
    @Multipart
    @POST("api/images")
    Call<Void> uploadImage(@Header("Authorization") String token, @Part MultipartBody.Part file);
}
