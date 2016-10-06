package com.example.bs148.finderapp.networkprovider;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by Ashraful on 4/1/2016.
 */
public class NetworkServiceHandler {
    public static<T> void processPreloginCallBack(Call<T> call, Context context) {
        call.enqueue(new CustomCallback<T>(context));
    }
}
