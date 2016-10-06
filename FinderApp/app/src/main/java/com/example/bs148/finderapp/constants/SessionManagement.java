package com.example.bs148.finderapp.constants;

import okhttp3.Request;

/**
 * Created by BS148 on 10/6/2016.
 */

public class SessionManagement {
    public static String authenticationToken="";
    public static void  getToken(Request.Builder builder)
    {
        if(authenticationToken!=null&&!authenticationToken.isEmpty())
            builder.addHeader("AuthenticationToken",authenticationToken );

    }
}
