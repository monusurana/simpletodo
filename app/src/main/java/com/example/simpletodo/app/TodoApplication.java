package com.example.simpletodo.app;

import android.app.Application;
import android.os.StrictMode;

import com.example.simpletodo.BuildConfig;
import com.squareup.leakcanary.LeakCanary;
import timber.log.Timber;

public class TodoApplication extends Application{
    private static TodoApplication singleton;

    public static TodoApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeathOnNetwork()
                    .build());

            Timber.plant(new Timber.DebugTree());

            LeakCanary.install(this);
        }
    }
}
