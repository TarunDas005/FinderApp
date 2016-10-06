package com.example.bs148.finderapp.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bs148.finderapp.R;

import de.greenrobot.event.EventBus;

public class BaseFragment extends Fragment {

    public BaseFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEventBus();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }

    protected void registerEventBus()
    {
        try {
            if (!isEventBusRegistered()) {
                EventBus.getDefault().register(getActivity());
            }
        } catch (Exception ex) {

        }

    }

    protected boolean isEventBusRegistered()
    {
        return EventBus.getDefault().isRegistered(getActivity());
    }

    protected void unRegisterEventBus()
    {
        if (isEventBusRegistered()) {
            EventBus.getDefault().unregister(this);
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterEventBus();
    }


    @Override
    public void onStart() {
        super.onStart();
        checkEventBusRegistration();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
    @Override
    public void onResume() {
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
