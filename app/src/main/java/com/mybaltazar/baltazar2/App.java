package com.mybaltazar.baltazar2;

import android.app.Application;

import co.ronash.pushe.Pushe;
import khangtran.preferenceshelper.PrefHelper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PrefHelper.initHelper(this);
        Pushe.initialize(this,true);
    }
}
