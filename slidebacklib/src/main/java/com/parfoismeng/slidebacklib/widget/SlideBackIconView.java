package com.parfoismeng.slidebacklib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * author : ParfoisMeng
 * time   : 2018/12/19
 * desc   : 边缘返回的图标View
 */
public class SlideBackIconView extends View {
    private Path bgPath, arrowPath;
    private Paint bgPaint, arrowPaint;

    private float slideLength = 0; // 拉动距离
    private float maxSlideLength = 0; // 最大拉动距离

    private float arrowSize = 10; // 箭头图标大小

    @ColorInt
    private int backViewColor = Color.BLACK; // 默认值

    private float backViewHeight = 0;

    public SlideBackIconView(Context context) {
        this(context, null);
    }

    public SlideBackIconView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBackIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     * Path & Paint
     */
    private void init() {
        bgPath = new Path();
        arrowPath = new Path();

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bgPaint.setColor(backViewColor);
        bgPaint.setStrokeWidth(1);

        arrowPaint = new Paint();
        arrowPaint.setAntiAlias(true);
        arrowPaint.setStyle(Paint.Style.STROKE);
        arrowPaint.setColor(Color.WHITE);
        arrowPaint.setStrokeWidth(8);
        arrowPaint.setStrokeJoin(Paint.Join.ROUND);

        setAlpha(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 背景
        bgPath.reset();
        bgPath.moveTo(0, 0);
        bgPath.cubicTo(0, backViewHeight * 2 / 9, slideLength, backViewHeight / 3, slideLength, backViewHeight / 2);
        bgPath.cubicTo(slideLength, backViewHeight * 2 / 3, 0, backViewHeight * 7 / 9, 0, backViewHeight);
        canvas.drawPath(bgPath, bgPaint);

        // 箭头
        float arrowZoom = slideLength / maxSlideLength; // 箭头大小变化率
        float arrowAngle = arrowZoom < 0.75f ? 0 : (arrowZoom - 0.75f) * 2; // 箭头角度变化率
        arrowPath.reset();
        arrowPath.moveTo(slideLength / 2 + (arrowSize * arrowAngle), backViewHeight / 2 - (arrowZoom * arrowSize));
        arrowPath.lineTo(slideLength / 2 - (arrowSize * arrowAngle), backViewHeight / 2);
        arrowPath.lineTo(slideLength / 2 + (arrowSize * arrowAngle), backViewHeight / 2 + (arrowZoom * arrowSize));
        canvas.drawPath(arrowPath, arrowPaint);

        setAlpha(slideLength / maxSlideLength - 0.2f); // 最多0.8透明度
    }

    public void updateSlideLength(float slideLength) {
        this.slideLength = slideLength;
        invalidate();
    }

    /**
     * 设置最大拉动距离
     *
     * @param maxSlideLength px值
     */
    public void setMaxSlideLength(float maxSlideLength) {
        this.maxSlideLength = maxSlideLength;
    }

    /**
     * 设置箭头图标大小
     *
     * @param arrowSize px值
     */
    public void setArrowSize(float arrowSize) {
        this.arrowSize = arrowSize;
    }

    /**
     * 设置返回Icon背景色
     *
     * @param backViewColor ColorInt
     */
    public void setBackViewColor(@ColorInt int backViewColor) {
        this.backViewColor = backViewColor;
    }

    /**
     * 设置返回Icon的高度
     *
     * @param backViewHeight px值
     */
    public void setBackViewHeight(float backViewHeight) {
        this.backViewHeight = backViewHeight;
    }

    public float getBackViewHeight() {
        return backViewHeight;
    }
}