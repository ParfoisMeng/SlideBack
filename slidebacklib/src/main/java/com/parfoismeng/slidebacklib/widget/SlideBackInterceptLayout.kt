package com.parfoismeng.slidebacklib.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * author : ParfoisMeng
 * time   : 2019/01/10
 * desc   : 处理事件拦截的Layout
 */
class SlideBackInterceptLayout(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var sideSlideLength = 0f // 边缘滑动响应距离

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return ev.action == MotionEvent.ACTION_DOWN && (ev.rawX <= sideSlideLength || ev.rawX >= width - sideSlideLength)
    }

    fun withSideSlideLength(sideSlideLength: Float): SlideBackInterceptLayout {
        this.sideSlideLength = sideSlideLength
        return this
    }
}