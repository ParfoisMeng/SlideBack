package com.parfoismeng.slidebacklib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.parfoismeng.slidebacklib.callback.SlideBackCallBack;
import com.parfoismeng.slidebacklib.widget.SlideBackIconView;
import com.parfoismeng.slidebacklib.widget.SlideBackInterceptLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * author : ParfoisMeng
 * time   : 2018/12/19
 * desc   : SlideBack管理器
 */
public class SlideBackManager {
    private SlideBackIconView slideBackIconView;

    private Activity activity;
    private boolean haveScroll;

    private SlideBackCallBack callBack;

    private float backViewHeight; // 控件高度
    private float arrowSize; // 箭头图标大小
    private float maxSlideLength; // 最大拉动距离

    private float sideSlideLength; // 侧滑响应距离
    private float dragRate; // 阻尼系数

    SlideBackManager(Activity activity) {
        this.activity = activity;
        haveScroll = false;

        // 获取屏幕信息，初始化控件设置
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        float screenWidth = dm.widthPixels;
        float screenHeight = dm.heightPixels;

        backViewHeight = screenHeight / 4; // 高度默认 屏高/4
        arrowSize = dp2px(5); // 箭头大小默认 5dp
        maxSlideLength = screenWidth / 12; // 最大宽度默认 屏宽/12

        sideSlideLength = maxSlideLength / 2; // 侧滑响应距离默认 控件最大宽度/2
        dragRate = 3; // 阻尼系数默认 3
    }

    /**
     * 是否包含滑动控件 默认false
     */
    public SlideBackManager haveScroll(boolean haveScroll) {
        this.haveScroll = haveScroll;
        return this;
    }

    /**
     * 回调
     */
    public SlideBackManager callBack(SlideBackCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    /**
     * 控件高度 默认屏高/4
     */
    public SlideBackManager viewHeight(float backViewHeightDP) {
        this.backViewHeight = dp2px(backViewHeightDP);
        return this;
    }

    /**
     * 箭头大小 默认5dp
     */
    public SlideBackManager arrowSize(float arrowSizeDP) {
        this.arrowSize = dp2px(arrowSizeDP);
        return this;
    }

    /**
     * 最大拉动距离（控件最大宽度） 默认屏宽/12
     */
    public SlideBackManager maxSlideLength(float maxSlideLengthDP) {
        this.maxSlideLength = dp2px(maxSlideLengthDP);
        return this;
    }

    /**
     * 侧滑响应距离 默认控件最大宽度/2
     */
    public SlideBackManager sideSlideLength(float sideSlideLengthDP) {
        this.sideSlideLength = dp2px(sideSlideLengthDP);
        return this;
    }

    /**
     * 阻尼系数 默认3（越小越灵敏）
     */
    public SlideBackManager dragRate(float dragRate) {
        this.dragRate = dragRate;
        return this;
    }

    /**
     * 需要使用滑动的页面注册
     */
    @SuppressLint("ClickableViewAccessibility")
    public void register() {
        // 初始化SlideBackIconView
        slideBackIconView = new SlideBackIconView(activity);
        slideBackIconView.setBackViewHeight(backViewHeight);
        slideBackIconView.setArrowSize(arrowSize);
        slideBackIconView.setMaxSlideLength(maxSlideLength);

        // 获取decorView并设置OnTouchListener监听
        FrameLayout container = (FrameLayout) activity.getWindow().getDecorView();
        if (haveScroll) {
            SlideBackInterceptLayout interceptLayout = new SlideBackInterceptLayout(activity);
            interceptLayout.setSideSlideLength(sideSlideLength);
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
                        if (downX <= sideSlideLength) { // 检验是否从边缘开始滑动
                            isSideSlide = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE: // 移动
                        if (isSideSlide) { // 是从边缘开始滑动
                            float moveX = event.getRawX() - downX; // 获取X轴位移距离
                            if (Math.abs(moveX) <= maxSlideLength * dragRate) {
                                // 如果位移距离在可拉动距离内，更新SlideBackIconView的当前拉动距离并重绘
                                slideBackIconView.updateSlideLength(Math.abs(moveX) / dragRate);
                            }
                            // 根据Y轴位置给SlideBackIconView定位
                            setSlideBackPosition(slideBackIconView, (int) (event.getRawY()));
                        }
                        break;
                    case MotionEvent.ACTION_UP: // 抬起
                        // 是从边缘开始滑动 且 抬起点的X轴坐标大于某值(默认3倍最大滑动长度)
                        if (isSideSlide && event.getRawX() >= maxSlideLength * dragRate) {
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
    }

    /**
     * 页面销毁时记得解绑
     * 其实就是置空防止内存泄漏
     */
    @SuppressLint("ClickableViewAccessibility")
    void unregister() {
        FrameLayout container = (FrameLayout) activity.getWindow().getDecorView();
        if (haveScroll) removeInterceptLayout(container);
        container.removeView(slideBackIconView);
        container.setOnTouchListener(null);

        activity = null;
        callBack = null;
        slideBackIconView = null;
    }

    /**
     * 给根布局包上一层事件拦截处理Layout
     */
    private void addInterceptLayout(ViewGroup decorView, SlideBackInterceptLayout interceptLayout) {
        // 取出根布局
        List<View> rootLayouts = new ArrayList<>();
        for (int i = 0, count = decorView.getChildCount(); i < count; i++) {
            rootLayouts.add(decorView.getChildAt(i));
        }
        for (View rootLayout : rootLayouts) {
            decorView.removeView(rootLayout); // 先移除根布局
            // 用事件拦截处理Layout将原根布局包起来，再添加回去
            interceptLayout.addView(rootLayout);
        }
        decorView.addView(interceptLayout);
    }

    /**
     * 将根布局还原，移除SlideBackInterceptLayout
     */
    private void removeInterceptLayout(ViewGroup decorView) {
        FrameLayout rootLayout = (FrameLayout) decorView.getChildAt(0); // 取出根布局
        decorView.removeView(rootLayout); // 先移除根布局
        // 将原布局取出放回decorView
        List<View> oriLayouts = new ArrayList<>();
        for (int i = 0, count = rootLayout.getChildCount(); i < count; i++) {
            oriLayouts.add(rootLayout.getChildAt(i));
        }
        for (View oriLayout : oriLayouts) {
            rootLayout.removeView(oriLayout);
            decorView.addView(oriLayout);
        }
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