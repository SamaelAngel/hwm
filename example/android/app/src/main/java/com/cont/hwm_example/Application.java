package com.cont.hwm_example;

import android.content.Context;

import com.cont.hwm.HWMHelp;


/**
 * Created by songjie on 2018/10/10.
 */

public class Application extends android.app.Application {
    private static Context mAppContext;
    public static final String TAG = "MApplication";


    @Override
    public void onCreate() {
        super.onCreate();
        HWMHelp.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }




}
