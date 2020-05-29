package com.lamonjush.libsynctask;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SyncTaskLib.getInstance().initiate(getApplicationContext(), getApplicationContext().getPackageName());
    }
}