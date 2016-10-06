package com.example.bs148.finderapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onStart() {
        super.onStart();
        checkEventBusRegistration();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkEventBusRegistration();
    }
    public void checkEventBusRegistration() {
        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } catch (Exception ex) {

        }
    }
}
