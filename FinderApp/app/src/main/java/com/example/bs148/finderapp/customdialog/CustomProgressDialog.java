package com.example.bs148.finderapp.customdialog;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by BS148 on 10/4/2016.
 */

public class CustomProgressDialog {
    private ProgressDialog pDialog;
    private Context context;

    public CustomProgressDialog(Context context) {
        this.context = context;
    }

    public void setProgressDialog() {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        pDialog.show();

    }

    public void dismissDialog() {
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
}
