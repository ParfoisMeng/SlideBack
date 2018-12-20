package com.parfoismeng.slidebacklib;

import android.app.Activity;

import java.util.WeakHashMap;

/**
 * author : ParfoisMeng
 * time   : 2018/12/19
 * desc   : SlideBack工具类
 */
public class SlideBackUtils {
    static WeakHashMap<Activity, SlideBack> map = new WeakHashMap<>();

    public static void register(Activity activity, SlideBack.SlideBackCallBack callBack) {
        map.put(activity, new SlideBack().register(activity, callBack));
    }

    public static void unregister(Activity activity) {
        SlideBack slideBack = map.get(activity);
        if (null != slideBack) {
            slideBack.unregister();
        }
        map.remove(activity);
    }
}