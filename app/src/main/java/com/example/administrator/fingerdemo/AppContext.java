package com.example.administrator.fingerdemo;

import android.app.Application;


/**
 *
 */
public class AppContext extends Application {
    private static AppContext app;
    public static synchronized AppContext getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

}
