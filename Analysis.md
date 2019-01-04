#### SlideBack库：https://github.com/ParfoisMeng/SlideBack

### 看效果，做分析
先从即刻效果图（下图）分析一下。
![即刻效果图](https://github.com/ParfoisMeng/SlideBack/blob/master/screenshot/jike.gif)
##### 拆解情景如下：
1. 手指从屏幕侧边一定范围内滑出
2. 屏幕边缘出现黑底白箭头的指示View，随手指触点位移变化
3. 手指滑动到某个位置时松手则触发返回事件
##### 初步思考（与情景拆解对应）：
1. 基本确定要监听`OnTouchListener -> ACTION_DOWN`，判断触点的X轴位置是否靠近边缘，即`event.getRawX()`是否小于某值
2. 需要实现一个黑底白箭头的自定义View，靠内曲线圆滑（贝塞尔）。白箭头随手指触点位移时，先是直线由短变长，然后折弯变成箭头，过程中透明度渐变。需要监听`OnTouchListener -> ACTION_MOVE`，手指触点X轴位移影响动画，Y轴位移影响控件定位。
3. `OnTouchListener -> ACTION_UP`，如果松手时X轴坐标大于某值则触发事件。除监听外需要定义一个回调接口。
+ 我们要监听谁的`OnTouchListener`？要在页面响应，与具体布局无关，基本上就是`decorView`了。
---
### 分析完毕，开搞
##### 黑底白箭头的自定义View
根据上述分析，为了写出来方便，我们先分析*黑底白箭头的自定义View* - [SlideBackIconView](https://github.com/ParfoisMeng/SlideBack/blob/master/slidebacklib/src/main/java/com/parfoismeng/slidebacklib/widget/SlideBackIconView.java)
1. 创建SlideBackIconView继承View就行：
```
public class SlideBackIconView extends View {
    private Path bgPath, arrowPath; // 路径对象
    private Paint bgPaint, arrowPaint; // 画笔对象

    private float slideLength = 0; // 当前拉动距离
    private float maxSlideLength = 0; // 最大拉动距离

    private float arrowSize = 10; // 箭头图标大小

    @ColorInt
    private int backViewColor = Color.BLACK; // 默认值

    private float backViewHeight = 0; // 控件高度

   // 省略构造方法等
   // ...
}
```
2. 初始化背景和箭头的路径与画笔，变量名还算规范：
```
    /**
     * 初始化 路径与画笔
     * Path & Paint
     */
    private void init() {
        bgPath = new Path();
        arrowPath = new Path();

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL_AND_STROKE); // 填充内部和描边
        bgPaint.setColor(backViewColor);
        bgPaint.setStrokeWidth(1); // 画笔宽度

        arrowPaint = new Paint();
        arrowPaint.setAntiAlias(true);
        arrowPaint.setStyle(Paint.Style.STROKE); // 描边
        arrowPaint.setColor(Color.WHITE);
        arrowPaint.setStrokeWidth(8); // 画笔宽度
        arrowPaint.setStrokeJoin(Paint.Join.ROUND); // * 结合处的样子 ROUND:圆弧

        setAlpha(0);
    }
```
3. 绘制：
```
    /**
     * 因为过程中会多次绘制，所以要先重置路径再绘制。
     * 贝塞尔曲线没什么好说的，相关文章有很多。此曲线经我测试比较类似“即刻App”。
     *
     * 方便阅读再写一遍，此段代码中的变量定义：
     * backViewHeight   控件高度
     * slideLength      当前拉动距离
     * maxSlideLength   最大拉动距离
     * arrowSize        箭头图标大小
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 背景
        bgPath.reset(); // 会多次绘制，所以先重置
        bgPath.moveTo(0, 0);
        bgPath.cubicTo(0, backViewHeight * 2 / 9, slideLength, backViewHeight / 3, slideLength, backViewHeight / 2);
        bgPath.cubicTo(slideLength, backViewHeight * 2 / 3, 0, backViewHeight * 7 / 9, 0, backViewHeight);
        canvas.drawPath(bgPath, bgPaint); // 根据设置的贝塞尔曲线路径用画笔绘制

        // 箭头是先直线由短变长再折弯变成箭头状的
        // 依据当前拉动距离和最大拉动距离计算箭头大小值
        // 大小到一定值后开始折弯，计算箭头角度值
        float arrowZoom = slideLength / maxSlideLength; // 箭头大小变化率
        float arrowAngle = arrowZoom < 0.75f ? 0 : (arrowZoom - 0.75f) * 2; // 箭头角度变化率
        // 箭头
        arrowPath.reset(); // 先重置
        // 结合箭头大小值与箭头角度值设置折线路径
        arrowPath.moveTo(slideLength / 2 + (arrowSize * arrowAngle), backViewHeight / 2 - (arrowZoom * arrowSize));
        arrowPath.lineTo(slideLength / 2 - (arrowSize * arrowAngle), backViewHeight / 2);
        arrowPath.lineTo(slideLength / 2 + (arrowSize * arrowAngle), backViewHeight / 2 + (arrowZoom * arrowSize));
        canvas.drawPath(arrowPath, arrowPaint);

        setAlpha(slideLength / maxSlideLength - 0.2f); // 最多0.8透明度
    }
```
4. View是会随着位移产生变化的，更新绘制的方法：
```
    /**
     * 更新当前拉动距离并重绘
     * @param slideLength 当前拉动距离
     */
    public void updateSlideLength(float slideLength) {
        this.slideLength = slideLength;
        invalidate(); // 会再次调用onDraw
    }
```
5. 其他set方法看源码吧

##### 监听`decorView`的`OnTouchListener`事件
获取Activity的`decorView`并设置监听`OnTouchListener`在一个单独的管理器里 - [SlideBackManager](https://github.com/ParfoisMeng/SlideBack/blob/master/slidebacklib/src/main/java/com/parfoismeng/slidebacklib/SlideBackManager.java)
1. 注册：
```
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

        maxSlideLength = screenWidth / 12; // 这里我设置为 屏宽/12

        // 初始化SlideBackIconView
        slideBackIconView = new SlideBackIconView(activity);
        slideBackIconView.setBackViewColor(Color.BLACK);
        slideBackIconView.setBackViewHeight(screenHeight / 4);
        slideBackIconView.setArrowSize(dp2px(5));
        slideBackIconView.setMaxSlideLength(maxSlideLength);

        // 获取decorView并设置OnTouchListener监听
        FrameLayout container = (FrameLayout) activity.getWindow().getDecorView();
        container.addView(slideBackIconView);
        container.setOnTouchListener(new View.OnTouchListener() {
            private boolean isSideSlide = false;  // 是否从边缘开始滑动
            private float downX = 0; // 按下的X轴坐标

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // 按下
                        downX = event.getRawX(); // 更新按下点的X轴坐标
                        if (downX <= maxSlideLength) { // 检验是否从边缘开始滑动
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
     * 给SlideBackIconView设置topMargin，起到定位效果
     * @param view SlideBackIconView
     * @param position 触点位置
     */
    private void setSlideBackPosition(SlideBackIconView view, int position) {
        // 触点位置减去SlideBackIconView一半高度即为topMargin
        int topMargin = (int) (position - (view.getBackViewHeight() / 2));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(view.getLayoutParams());
        layoutParams.topMargin = topMargin;
        view.setLayoutParams(layoutParams);
    }
```
2. 注销：
```
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
```

##### 防止某Activity重复注册
将管理类方法包装一下提供给外层使用，同时也防止某个Activity重复注册，我们再加个使用类 - [SlideBack](https://github.com/ParfoisMeng/SlideBack/blob/master/slidebacklib/src/main/java/com/parfoismeng/slidebacklib/SlideBack.java)
```
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
```

### 总结
没啥好总结的，就是拆解分析然后开搞，做了就明白了。
这篇源码分析，其实我就是把源码copy出来整了个MD。
看完还有啥不懂的，[issues](https://github.com/ParfoisMeng/SlideBack/issues)提问吧。也可以加好友问我。
[QQ](757479544) / [微信](youshi520000) / [邮箱](youshi520000@163.com)