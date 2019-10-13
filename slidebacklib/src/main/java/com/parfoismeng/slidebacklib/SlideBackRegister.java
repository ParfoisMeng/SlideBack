package com.parfoismeng.slidebacklib;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.parfoismeng.slidebacklib.annotation.SlideBackBinder;
import com.parfoismeng.slidebacklib.callback.SlideBackCallBack;

/**
 * com.parfoismeng.slidebacklib.annonation
 * 2019/10/13
 * ken
 * 20:19
 */
public class SlideBackRegister implements Application.ActivityLifecycleCallbacks{
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        register(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        unRegister(activity);
    }


    private void register(final Activity activity){
        Class<? extends Activity> clazz = activity.getClass();
        if(clazz.isAnnotationPresent(SlideBackBinder.class)){
            SlideBackBinder binder = clazz.getAnnotation(SlideBackBinder.class);
            boolean haveScroll = false;
            if(binder!=null){
                haveScroll = binder.haveScroll();
            }
            SlideBack.with(activity)
                    .haveScroll(haveScroll)
                    .callBack(new SlideBackCallBack() {
                        @Override
                        public void onSlideBack() {
                            if(Build.VERSION.SDK_INT>=21){
                                activity.finishAfterTransition();
                            }else{
                                activity.finish();
                            }
                        }
                    })
                    .register();
        }
    }

    private void unRegister(Activity activity){
        Class<? extends Activity> clazz = activity.getClass();
        if(clazz.isAnnotationPresent(SlideBackBinder.class)){
            SlideBack.unregister(activity);
        }
    }
}
