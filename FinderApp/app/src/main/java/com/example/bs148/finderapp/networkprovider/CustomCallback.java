package com.example.bs148.finderapp.networkprovider;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.bs148.finderapp.constants.ResponseCode;
import com.example.bs148.finderapp.model.UserAddressInformation;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Ashraful on 11/5/2015.
 */
public class CustomCallback<T> implements Callback<T> {
    Context context;
    boolean isTranscactionRequest;

    public ProgressDialog pDialog;

    public  void setProgressDialog() {
        pDialog= new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        if(isTranscactionRequest) {
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
        }
        else {
            pDialog.setCancelable(true);
            pDialog.setCanceledOnTouchOutside(true);
        }
        pDialog.show();


    }
    public  void dismissDialog() {
        try {
            if ((pDialog != null) && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();

        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            pDialog = null;
        }


    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        dismissDialog();
        String errorMessage="Something going wrong";

        if(!isNetworkAvailable())
            errorMessage="No Network Connection";
      else  if(t!=null){
           Throwable cause=t;


            /*if(cause instanceof IOException)
                errorMessage="No Network Connection";
            else*/ if(cause instanceof SocketTimeoutException)
                errorMessage="Connection Timeout, try again later";
            else if(cause instanceof UnknownHostException)
                errorMessage="Requested Service is Unavailable";
        }
        if(context!=null)
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        dismissDialog();
        manipulateBaseResponse(response.body());

    }


    public CustomCallback(Context context)
    {
        this.context=context;
        setProgressDialog();
    }

    public CustomCallback(Context context, boolean isTransactionRequest)
    {
        this.context=context;
        this.isTranscactionRequest=true;
        setProgressDialog();
    }
    public CustomCallback( ) {

    }



    public void manipulateBaseResponse(T t){

        try{
            UserAddressInformation userAddressInformation= (UserAddressInformation) t;
            if (userAddressInformation.getStatus().equals(ResponseCode.RESPONSE_SUCCESSFULL)){
                EventBus.getDefault().post(userAddressInformation);
            }else{
                Toast.makeText(context,"No Data Found Try Later??????",Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected boolean isNetworkAvailable() {
        if(context!=null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        else
            return false;
    }
}
