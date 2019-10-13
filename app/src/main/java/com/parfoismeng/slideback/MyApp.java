package com.parfoismeng.slideback;

import android.app.Application;

import com.parfoismeng.slidebacklib.SlideBackRegister;

/**
 * author : ParfoisMeng
 * time   : 2018/12/19
 * desc   : ...
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //添加
        registerActivityLifecycleCallbacks(new SlideBackRegister());
    }
}
