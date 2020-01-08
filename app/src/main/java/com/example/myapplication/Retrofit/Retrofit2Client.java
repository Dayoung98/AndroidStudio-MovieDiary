package com.example.myapplication.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit2Client {
    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Retrofit2Constants.URL.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static Retrofit getInstance() {
        return retrofit;
    }
}