package com.parfoismeng.slidebacklib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.parfoismeng.slidebacklib.callback.SlideBackCallBack;
import com.parfoismeng.slidebacklib.callback.SlideCallBack;
import com.parfoismeng.slidebacklib.widget.SlideBackIconView;
import com.parfoismeng.slidebacklib.widget.SlideBackInterceptLayout;

import static com.parfoismeng.slidebacklib.SlideBack.*;

/**
 * author : ParfoisMeng
 * time   : 2018/12/19
 * desc   : SlideBack管理器
 */
public class SlideBackManager {
    private SlideBackIconView slideBackIconViewLeft;
    private SlideBackIconView slideBackIconViewRight;

    private Activity activity;
    private boolean haveScroll;

    private SlideCallBack callBack;

    private float backViewHeight; // 控件高度
    private float arrowSize; // 箭头图标大小
    private float maxSlideLength; // 最大拉动距离

    private float sideSlideLength; // 侧滑响应距离
    private float dragRate; // 阻尼系数

    private boolean isEdgeLeft; // 使用左侧侧滑
    private boolean isEdgeRight; // 使用右侧侧滑

    private float screenWidth; // 屏幕宽

    SlideBackManager(Activity activity) {
        this.activity = activity;
        haveScroll = false;

        // 获取屏幕信息，初始化控件设置
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;

        backViewHeight = dm.heightPixels / 4f; // 高度默认 屏高/4
        arrowSize = dp2px(5); // 箭头大小默认 5dp
        maxSlideLength = screenWidth / 12; // 最大宽度默认 屏宽/12

        sideSlideLength = maxSlideLength / 2; // 侧滑响应距离默认 控件最大宽度/2
        dragRate = 3; // 阻尼系数默认 3

        // 侧滑返回模式 默认:左
        isEdgeLeft = true;
        isEdgeRight = false;
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
        this.callBack = new SlideCallBack(callBack) {
            @Override
            public void onSlide(int edgeFrom) {
                onSlideBack();
            }
        };
        return this;
    }

    /**
     * 回调 适用于新的左右模式
     */
    public SlideBackManager callBack(SlideCallBack callBack) {
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
     * 边缘侧滑模式 默认左
     */
    public SlideBackManager edgeMode(@SlideBack.EdgeMode int edgeMode) {
        switch (edgeMode) {
            case EDGE_LEFT:
                isEdgeLeft = true;
                isEdgeRight = false;
                break;
            case EDGE_RIGHT:
                isEdgeLeft = false;
                isEdgeRight = true;
                break;
            case EDGE_BOTH:
                isEdgeLeft = true;
                isEdgeRight = true;
                break;
            default:
                throw new RuntimeException("未定义的边缘侧滑模式值：EdgeMode = " + edgeMode);
        }
        return this;
    }

    /**
     * 需要使用滑动的页面注册
     */
    @SuppressLint("ClickableViewAccessibility")
    public void register() {
        if (isEdgeLeft) {
            // 初始化SlideBackIconView 左侧
            slideBackIconViewLeft = new SlideBackIconView(activity);
            slideBackIconViewLeft.setBackViewHeight(backViewHeight);
            slideBackIconViewLeft.setArrowSize(arrowSize);
            slideBackIconViewLeft.setMaxSlideLength(maxSlideLength);
        }
        if (isEdgeRight) {
            // 初始化SlideBackIconView - Right
            slideBackIconViewRight = new SlideBackIconView(activity);
            slideBackIconViewRight.setBackViewHeight(backViewHeight);
            slideBackIconViewRight.setArrowSize(arrowSize);
            slideBackIconViewRight.setMaxSlideLength(maxSlideLength);
            // 右侧侧滑 需要旋转180°
            slideBackIconViewRight.setRotationY(180);
        }

        // 获取decorView并设置OnTouchListener监听
        FrameLayout container = (FrameLayout) activity.getWindow().getDecorView();
        if (haveScroll) {
            SlideBackInterceptLayout interceptLayout = new SlideBackInterceptLayout(activity);
            interceptLayout.setSideSlideLength(sideSlideLength);
            addInterceptLayout(container, interceptLayout);
        }
        if (isEdgeLeft) {
            container.addView(slideBackIconViewLeft);
        }
        if (isEdgeRight) {
            container.addView(slideBackIconViewRight);
        }
        container.setOnTouchListener(new View.OnTouchListener() {
            private boolean isSideSlideLeft = false;  // 是否从左边边缘开始滑动
            private boolean isSideSlideRight = false;  // 是否从右边边缘开始滑动
            private float downX = 0; // 按下的X轴坐标
            private float moveXLength = 0; // 位移的X轴距离

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // 按下
                        // 更新按下点的X轴坐标
                        downX = event.getRawX();

                        // 检验是否从边缘开始滑动，区分左右
                        if (isEdgeLeft && downX <= sideSlideLength) {
                            isSideSlideLeft = true;
                        } else if (isEdgeRight && downX >= screenWidth - sideSlideLength) {
                            isSideSlideRight = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE: // 移动
                        if (isSideSlideLeft || isSideSlideRight) {
                            // 从边缘开始滑动
                            // 获取X轴位移距离
                            moveXLength = Math.abs(event.getRawX() - downX);
                            if (moveXLength / dragRate <= maxSlideLength) {
                                // 如果位移距离在可拉动距离内，更新SlideBackIconView的当前拉动距离并重绘，区分左右
                                if (isEdgeLeft && isSideSlideLeft) {
                                    slideBackIconViewLeft.updateSlideLength(moveXLength / dragRate);
                                } else if (isEdgeRight && isSideSlideRight) {
                                    slideBackIconViewRight.updateSlideLength(moveXLength / dragRate);
                                }
                            }

                            // 根据Y轴位置给SlideBackIconView定位
                            if (isEdgeLeft && isSideSlideLeft) {
                                setSlideBackPosition(slideBackIconViewLeft, (int) (event.getRawY()));
                            } else if (isEdgeRight && isSideSlideRight) {
                                setSlideBackPosition(slideBackIconViewRight, (int) (event.getRawY()));
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP: // 抬起
                        // 是从边缘开始滑动 且 抬起点的X轴坐标大于某值(默认3倍最大滑动长度) 且 回调不为空
                        if ((isSideSlideLeft || isSideSlideRight) && moveXLength / dragRate >= maxSlideLength && null != callBack) {
                            // 区分左右
                            callBack.onSlide(isSideSlideLeft ? EDGE_LEFT : EDGE_RIGHT);
                        }

                        // 恢复SlideBackIconView的状态
                        if (isEdgeLeft && isSideSlideLeft) {
                            slideBackIconViewLeft.updateSlideLength(0);
                        } else if (isEdgeRight && isSideSlideRight) {
                            slideBackIconViewRight.updateSlideLength(0);
                        }

                        // 从边缘开始滑动结束
                        isSideSlideLeft = false;
                        isSideSlideRight = false;
                        break;
                }
                return isSideSlideLeft;
            }
        });
    }

    /**
     * 页面销毁时记得解绑
     * 其实就是置空防止内存泄漏
     */
    @SuppressLint("ClickableViewAccessibility")
    void unregister() {
//        FrameLayout container = (FrameLayout) activity.getWindow().getDecorView();
//        if (haveScroll) removeInterceptLayout(container);
//        container.removeView(slideBackIconViewLeft);
//        container.setOnTouchListener(null);

        activity = null;
        callBack = null;
        slideBackIconViewLeft = null;
        slideBackIconViewRight = null;
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
     * 将根布局还原，移除SlideBackInterceptLayout
     */
    private void removeInterceptLayout(ViewGroup decorView) {
        FrameLayout rootLayout = (FrameLayout) decorView.getChildAt(0); // 取出根布局
        decorView.removeView(rootLayout); // 先移除根布局
        // 将根布局的第一个布局(原根布局)取出放回decorView
        View oriLayout = rootLayout.getChildAt(0);
        rootLayout.removeView(oriLayout);
        decorView.addView(oriLayout);
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