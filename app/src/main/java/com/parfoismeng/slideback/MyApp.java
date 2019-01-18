package com.parfoismeng.slideback;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;

/**
 * author : ParfoisMeng
 * time   : 2018/12/19
 * desc   : ...
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }
}
