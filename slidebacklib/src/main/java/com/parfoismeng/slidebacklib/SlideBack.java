package com.parfoismeng.slidebacklib;

import android.app.Activity;

import java.util.WeakHashMap;

/**
 * author : ParfoisMeng
 * time   : 2018/12/19
 * desc   : SlideBack使用类
 */
public class SlideBack {
    private static WeakHashMap<Activity, SlideBackManager> map = new WeakHashMap<>();

    public static void register(Activity activity, SlideBackManager.SlideBackCallBack callBack) {
        map.put(activity, new SlideBackManager().register(activity, callBack));
    }

    public static void unregister(Activity activity) {
        SlideBackManager slideBack = map.get(activity);
        if (null != slideBack) {
            slideBack.unregister();
        }
        map.remove(activity);
    }
}