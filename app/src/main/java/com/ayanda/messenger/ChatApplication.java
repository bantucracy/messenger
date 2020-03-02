package com.ayanda.messenger;

import android.app.Application;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        // Can be Level.BASIC, Level.HEADERS, or Level.BODY

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.networkInterceptors().add(httpLoggingInterceptor);


    }
}
