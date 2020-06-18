package com.yuong.database;

import android.app.Application;
import android.content.Context;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/17
 * desc   :
 */
public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
