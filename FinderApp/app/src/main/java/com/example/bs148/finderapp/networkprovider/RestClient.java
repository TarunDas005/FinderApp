package com.example.bs148.finderapp.networkprovider;

import com.example.bs148.finderapp.constants.SessionManagement;
import com.example.bs148.finderapp.constants.UrlLinkModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ashraful on 11/5/2015.
 */
public class RestClient {

    public static ApiService apiService;

    public static ApiService get() {
      //  if (apiService == null)
            initializeApiservice();
        return apiService;

    }

    public static ApiService get(String baseUrl) {
        //  if (apiService == null)
        initializeApiservice(baseUrl);
        return apiService;

    }

    public static void callApi()
    {

    }

    public static void initializeApiservice(String baseUrl) {
        /*Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();*/
        if(baseUrl.isEmpty())
            baseUrl= UrlLinkModel.BASE_URL;
        Gson gson = new GsonBuilder().create();
      //  Gson gson = new GsonBuilder().serializeNulls().create();
        OkHttpClient okHttpClient=getOkhttp();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static OkHttpClient getOkhttp()
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Builder okhttpBuilder = new Builder();
        okhttpBuilder.readTimeout(80, TimeUnit.SECONDS);
        okhttpBuilder.writeTimeout(80, TimeUnit.SECONDS);
        okhttpBuilder.addInterceptor(interceptor);
        okhttpBuilder.addInterceptor(getInterceptor());
        OkHttpClient client = okhttpBuilder.build();

        return client;
    }

    public static void initializeApiservice()
    {
        initializeApiservice("");
    }

    private static Interceptor getInterceptor() {
        return new Interceptor() {


            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder=original.newBuilder();
                SessionManagement.getToken(builder);
                Request request = builder
                        .addHeader("Content-Type","application/json")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }


        };
    }
}
