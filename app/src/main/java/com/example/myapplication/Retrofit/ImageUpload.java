package com.example.myapplication.Retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImageUpload {
    @Multipart
    @POST(Retrofit2Constants.URL.IMAGE_UPLOAD_URL)
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part image, @Part("id") RequestBody id, @Part("title") RequestBody title, @Part("genre") RequestBody genre, @Part("rating") RequestBody rating, @Part("content") RequestBody content);
}