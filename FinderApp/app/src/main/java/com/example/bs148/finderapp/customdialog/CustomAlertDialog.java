package com.example.bs148.finderapp.customdialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.bs148.finderapp.others.InternetConnectivity;

import de.greenrobot.event.EventBus;


/**
 * Created by BS148 on 10/3/2016.
 */

public class CustomAlertDialog {
    public static void showSettingsAlert(final Context mContext){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        alertDialog.setCancelable(false);

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Please Enable Gps then re-launch app?");

        // On pressing Settings button
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog,int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                mContext.startActivity(intent);
//            }
//        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                InternetConnectivity internetConnectivity=new InternetConnectivity(false);
                EventBus.getDefault().post(internetConnectivity);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
