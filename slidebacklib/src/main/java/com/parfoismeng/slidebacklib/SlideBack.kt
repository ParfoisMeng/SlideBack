package com.parfoismeng.slidebacklib

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.FrameLayout
import com.parfoismeng.slidebacklib.widget.SlideBackIconView
import com.parfoismeng.slidebacklib.widget.SlideBackInterceptLayout
import java.util.*
import kotlin.math.max
import kotlin.math.min

// 使用WeakHashMap防止内存泄漏
private val slideBackMap = WeakHashMap<Activity, SlideBack>()

/**
 * 注册
 */
fun Activity.registerSlideBack(haveScroll: Boolean = true, callBack: () -> Unit) {
    registerSlideBack(haveScroll, callBack) { }
}

/**
 * 注册
 */
fun Activity.registerSlideBack(haveScroll: Boolean = true, callBack: () -> Unit, custom: SlideBack.() -> Unit) {
    slideBackMap[this] = SlideBack(this, haveScroll, callBack).apply {
        custom()
        register()
    }
}

/**
 * 注销
 */
fun Activity.unregisterSlideBack() {
    slideBackMap.remove(this)
}

/**
 * author : ParfoisMeng
 * time   : 2018/12/19
 * desc   : SlideBack管理器
 */
class SlideBack constructor(private val activity: Activity, private var haveScroll: Boolean = true, private var callBack: () -> Unit) {
    /**
     * 侧滑返回 IconView
     */
    private val iconView: SlideBackIconView by lazy {
        // 初始化SlideBackIconView 左侧
        SlideBackIconView(activity).apply {
            viewBackgroundColor = iconViewBackgroundColor
            viewHeight = iconViewHeight
            viewArrowSize = iconViewArrowSize
            viewMaxLength = iconViewMaxLength
        }
    }

    /**
     * IconView 背景色 默认黑色
     */
    private var iconViewBackgroundColor = Color.BLACK

    /**
     * IconView 高度 默认160dp
     */
    var iconViewHeight: Float = dp2px(160)

    /**
     * IconView 箭头图标大小 默认5dp
     */
    var iconViewArrowSize: Float = dp2px(5)

    /**
     * IconView 最大拉动距离 默认30dp
     */
    var iconViewMaxLength: Float = dp2px(30)


    /**
     * 侧滑响应距离 默认控件最大宽度/2
     */
    var sideSlideLength: Float = iconViewMaxLength / 2

    /**
     * 阻尼系数 默认3
     */
    var dragRate: Float = 3f

    /**
     * 需要使用滑动的页面注册
     */
    @SuppressLint("ClickableViewAccessibility")
    fun register() {
        // 获取 decorView
        val container = activity.window.decorView as FrameLayout
        // 如果设置有滑动冲突的话，需要在根布局添加拦截布局
        if (haveScroll) {
            addInterceptLayout(container, SlideBackInterceptLayout(activity).withSideSlideLength(sideSlideLength))
        }
        // 添加 IconView
        container.addView(iconView)
        // 设置触摸监听
        container.setOnTouchListener(object : OnTouchListener {
            private var isSideSlide = false // 是否从边缘开始滑动
            private var downX = 0f // 按下的X轴坐标
            private var moveXLength = 0f // 位移的X轴距离

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 更新按下点的X轴坐标
                        downX = event.rawX

                        // 检验是否从边缘开始滑动
                        if (downX <= sideSlideLength) {
                            isSideSlide = true
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // 从边缘开始滑动
                        if (isSideSlide) {
                            // 获取X轴位移距离
                            moveXLength = max(event.rawX - downX, 0F)

                            // 如果位移距离在可拉动距离内，更新SlideBackIconView的当前拉动距离并重绘，区分左右
                            iconView.updateSlideLength(min(moveXLength / dragRate, iconViewMaxLength))

                            // 根据Y轴位置给SlideBackIconView定位
                            iconView.setSlideBackPosition(event.rawY)
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        // 从边缘开始滑动
                        if (isSideSlide) {
                            // 抬起点的X轴坐标大于某值(默认3倍最大滑动长度)则Callback
                            if (moveXLength / dragRate >= iconViewMaxLength) {
                                callBack()
                            }

                            // 恢复SlideBackIconView的状态
                            iconView.updateSlideLength(0f)

                            isSideSlide = false
                        }
                    }
                }
                return isSideSlide
            }
        })
    }

    /**
     * 给根布局包上一层事件拦截处理Layout
     */
    private fun addInterceptLayout(decorView: ViewGroup, interceptLayout: SlideBackInterceptLayout) {
        val rootLayout = decorView.getChildAt(0) // 取出根布局
        decorView.removeView(rootLayout) // 先移除根布局
        // 用事件拦截处理Layout将原根布局包起来，再添加回去
        interceptLayout.addView(rootLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        decorView.addView(interceptLayout)
    }

    fun dp2px(dpValue: Int): Float {
        return dpValue * activity.resources.displayMetrics.density + 0.5f
    }
}