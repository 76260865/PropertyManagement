package com.jason.property;

import com.jason.property.crash.CrashHandler;

import android.app.Application;

public class PropertyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}
