package com.parfoismeng.slidebacklib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.parfoismeng.slidebacklib.widget.SlideBackIconView;

/**
 * author : ParfoisMeng
 * time   : 2018/12/19
 * desc   : SlideBack管理器
 */
class SlideBackManager {
    private Activity activity;
    private SlideBackCallBack callBack;

    private float maxSlideLength;

    private SlideBackIconView slideBackIconView;

    /**
     * 需要使用滑动的页面注册
     *
     * @param activity 页面Act
     * @param callBack 回调
     */
    @SuppressLint("ClickableViewAccessibility")
    SlideBackManager register(Activity activity, SlideBackCallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        float screenWidth = dm.widthPixels;
        float screenHeight = dm.heightPixels;

        maxSlideLength = screenWidth / 12;

        slideBackIconView = new SlideBackIconView(activity);
        slideBackIconView.setBackViewColor(Color.BLACK);
        slideBackIconView.setBackViewHeight(screenHeight / 4);
        slideBackIconView.setArrowSize(dp2px(5));
        slideBackIconView.setMaxSlideLength(maxSlideLength);

        FrameLayout container = (FrameLayout) activity.getWindow().getDecorView();
        container.addView(slideBackIconView);
        container.setOnTouchListener(new View.OnTouchListener() {
            private boolean isSideSlide = false;
            private float downX = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        if (downX <= maxSlideLength) {
                            isSideSlide = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isSideSlide) {
                            float moveX = event.getRawX() - downX;
                            if (Math.abs(moveX) <= maxSlideLength * 4) {
                                slideBackIconView.updateSlideLength(Math.abs(moveX) / 4);
                            }
                            setSlideBackPosition(slideBackIconView, (int) (event.getRawY()));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isSideSlide && event.getRawX() >= maxSlideLength * 4) {
                            if (null != SlideBackManager.this.callBack) {
                                SlideBackManager.this.callBack.onSlideBack();
                            }
                        }
                        isSideSlide = false;
                        slideBackIconView.updateSlideLength(0);
                        break;
                }
                return isSideSlide;
            }
        });

        return this;
    }

    /**
     * 页面销毁时记得解绑
     * 其实就是置空防止内存泄漏
     */
    void unregister() {
        activity = null;
        callBack = null;
        maxSlideLength = 0;
        slideBackIconView = null;
    }

    private void setSlideBackPosition(SlideBackIconView view, int position) {
        int topMargin = (int) (position - (view.getBackViewHeight() / 2));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(view.getLayoutParams());
        layoutParams.topMargin = topMargin;
        view.setLayoutParams(layoutParams);
    }

    private float dp2px(float dpValue) {
        return dpValue * activity.getResources().getDisplayMetrics().density + 0.5f;
    }

    public interface SlideBackCallBack {
        void onSlideBack();
    }
}