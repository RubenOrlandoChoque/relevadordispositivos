package com.sistematias.relevadordispositivos.activity;

import android.app.Application;
import android.content.Context;

/**
 * Created by samuel on 19/09/2015.
 */
public class MyApp extends Application {
    private static Context context;
    public void onCreate(){
        super.onCreate();
        MyApp.context = getApplicationContext();
    }

    public static Context getAppContext(){
        return context;
    }
}
