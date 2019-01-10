package com.parfoismeng.slidebacklib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.parfoismeng.slidebacklib.callback.SlideBackCallBack;
import com.parfoismeng.slidebacklib.widget.SlideBackIconView;
import com.parfoismeng.slidebacklib.widget.SlideBackInterceptLayout;

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
     * @param activity   页面Act
     * @param haveScroll 页面是否有滑动
     * @param callBack   回调
     */
    @SuppressLint("ClickableViewAccessibility")
    SlideBackManager register(Activity activity, boolean haveScroll, SlideBackCallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        float screenWidth = dm.widthPixels;
        float screenHeight = dm.heightPixels;

        maxSlideLength = screenWidth / 12; // 这里我设置为 屏宽/12

        // 初始化SlideBackIconView
        slideBackIconView = new SlideBackIconView(activity);
        slideBackIconView.setBackViewColor(Color.BLACK);
        slideBackIconView.setBackViewHeight(screenHeight / 4);
        slideBackIconView.setArrowSize(dp2px(5));
        slideBackIconView.setMaxSlideLength(maxSlideLength);

        // 获取decorView并设置OnTouchListener监听
        FrameLayout container = (FrameLayout) activity.getWindow().getDecorView();
        if (haveScroll) {
            SlideBackInterceptLayout interceptLayout = new SlideBackInterceptLayout(activity);
            interceptLayout.setSideSlideLength(maxSlideLength / 2);
            addInterceptLayout(container, interceptLayout);
        }
        container.addView(slideBackIconView);
        container.setOnTouchListener(new View.OnTouchListener() {
            private boolean isSideSlide = false;  // 是否从边缘开始滑动
            private float downX = 0; // 按下的X轴坐标

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // 按下
                        downX = event.getRawX(); // 更新按下点的X轴坐标
                        if (downX <= maxSlideLength / 2) { // 检验是否从边缘开始滑动
                            isSideSlide = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE: // 移动
                        if (isSideSlide) { // 是从边缘开始滑动
                            float moveX = event.getRawX() - downX; // 获取X轴位移距离
                            if (Math.abs(moveX) <= maxSlideLength * 4) {
                                // 如果位移距离在可拉动距离内，更新SlideBackIconView的当前拉动距离并重绘
                                slideBackIconView.updateSlideLength(Math.abs(moveX) / 4);
                            }
                            // 根据Y轴位置给SlideBackIconView定位
                            setSlideBackPosition(slideBackIconView, (int) (event.getRawY()));
                        }
                        break;
                    case MotionEvent.ACTION_UP: // 抬起
                        // 是从边缘开始滑动 且 抬起点的X轴坐标大于某值(4倍最大滑动长度)
                        if (isSideSlide && event.getRawX() >= maxSlideLength * 4) {
                            if (null != SlideBackManager.this.callBack) {
                                // 不为空则响应回调事件
                                SlideBackManager.this.callBack.onSlideBack();
                            }
                        }
                        isSideSlide = false; // 从边缘开始滑动结束
                        slideBackIconView.updateSlideLength(0); // 恢复0
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

    /**
     * 给根布局包上一层事件拦截处理Layout
     */
    private void addInterceptLayout(ViewGroup decorView, SlideBackInterceptLayout interceptLayout) {
        View rootLayout = decorView.getChildAt(0); // 取出根布局
        decorView.removeView(rootLayout); // 先移除根布局
        // 用事件拦截处理Layout将原根布局包起来，再添加回去
        interceptLayout.addView(rootLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        decorView.addView(interceptLayout);
    }

    /**
     * 给SlideBackIconView设置topMargin，起到定位效果
     *
     * @param view     SlideBackIconView
     * @param position 触点位置
     */
    private void setSlideBackPosition(SlideBackIconView view, int position) {
        // 触点位置减去SlideBackIconView一半高度即为topMargin
        int topMargin = (int) (position - (view.getBackViewHeight() / 2));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(view.getLayoutParams());
        layoutParams.topMargin = topMargin;
        view.setLayoutParams(layoutParams);
    }

    private float dp2px(float dpValue) {
        return dpValue * activity.getResources().getDisplayMetrics().density + 0.5f;
    }
}