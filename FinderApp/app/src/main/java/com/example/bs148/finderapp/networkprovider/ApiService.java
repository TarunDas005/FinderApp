package com.example.bs148.finderapp.networkprovider;

import com.example.bs148.finderapp.model.UserAddressInformation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by Ashraful on 3/31/2016.
 */
public interface ApiService {

    @GET
    Call<UserAddressInformation> getUserAddressInformation(@Url String url);

}

