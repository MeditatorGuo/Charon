package com.uowee.sample.app;

import android.app.Application;


public class App extends Application {
    private static App instance;

    public static synchronized App getInstance() {
        return instance;
    }

    public App() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
