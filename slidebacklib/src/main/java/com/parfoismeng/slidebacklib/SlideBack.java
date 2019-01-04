package com.parfoismeng.slidebacklib;

import android.app.Activity;
import com.parfoismeng.slidebacklib.callback.SlideBackCallBack;

import java.util.WeakHashMap;

/**
 * author : ParfoisMeng
 * time   : 2018/12/19
 * desc   : SlideBack使用类
 */
public class SlideBack {
    // 使用WeakHashMap防止内存泄漏
    private static WeakHashMap<Activity, SlideBackManager> map = new WeakHashMap<>();

    /**
     * 注册
     *
     * @param activity 目标Act
     * @param callBack 回调
     */
    public static void register(Activity activity, SlideBackCallBack callBack) {
        map.put(activity, new SlideBackManager().register(activity, callBack));
    }

    /**
     * 注销
     *
     * @param activity 目标Act
     */
    public static void unregister(Activity activity) {
        SlideBackManager slideBack = map.get(activity);
        if (null != slideBack) {
            slideBack.unregister();
        }
        map.remove(activity);
    }
}