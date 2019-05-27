package com.example.recorddemo;

import android.app.Application;

public class App extends Application {

    private static App context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
    }

    public static App getApp(){
        return context;
    }
}
