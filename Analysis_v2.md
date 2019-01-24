#### SlideBack库：https://github.com/ParfoisMeng/SlideBack

---

### 先看效果，做分析

从即刻效果图（下图）分析一下：
![即刻效果图](https://github.com/ParfoisMeng/SlideBack/raw/master/screenshot/jike.gif)

##### 拆解情景：

1. 手指从屏幕侧边一定范围内滑出，贴边出现黑底白箭头View。不贴边无响应。
2. 位移时View跟随触点变化。其X轴影响形状(凸起幅度/透明度/箭头形状)，Y轴影响位置。当滑动到某阈值位置后View不再跟随X轴变形。
3. View不再跟随X轴变形时(即滑动距离大于某阈值)，松手会触发返回事件。

##### 初步思考：

以上拆解，确定要监听`OnTouchListener`，1、2、3情景分别对应`ACTION_DOWN`、`ACTION_MOVE`、`ACTION_UP`。

1. `ACTION_DOWN` - 判断按下点的X轴位置`downX`是否靠近边缘，即`event.getRawX()`是否小于某值。
2. `ACTION_MOVE` - 黑底白箭头View肯定要自定义实现。View跟随位移距离`moveXLength`(即`event.getRawX() - downX`,同View当前宽度*某系数)变化，同时设置View的topMargin为`event.getRawY() - viewHeight/2`，以达到定位效果。当`moveXLength`大于某阈值时，不再跟随其变化。
3. `ACTION_UP` - 手指抬起时，`moveXLength`大于某阈值(即View最大宽度*某系数)则触发事件，事件需要定义一个接口回调给外部。

我们要监听谁的`OnTouchListener`？
要在整个Activity响应，且与具体布局`ContentView`无关。随后就想到了`decorView`。

---

### 分析完毕，开搞

根据上述分析，我们需要自定义一个*黑底白箭头View* - [SlideBackIconView](https://github.com/ParfoisMeng/SlideBack/blob/master/slidebacklib/src/main/java/com/parfoismeng/slidebacklib/widget/SlideBackIconView.java)。将*黑底白箭头View*添加到`decorView`中，在`OnTouchListener`事件中做上文分析的操作。

##### 黑底白箭头的自定义View

创建SlideBackIconView直接继承View，绘制黑底时需要贝塞尔曲线(可用[css贝塞尔曲线可视化](http://cubic-bezier.com/)测试参数)，绘制白箭头直接画个折线即可。

需要注意的点：
1. 黑底是整个背景，所以画笔需要设置填充，`Paint.Style.FILL_AND_STROKE`或`Paint.Style.FILL`。
2. 白箭头是折线，画笔描边`Paint.Style.STROKE`，需设置结合处为圆弧，`Paint.setStrokeJoin(Paint.Join.ROUND)`(如果不设置则拐角处棱角戳出来很难看)。
3. `onDraw`时需要先`Path.reset()`重置路径对象，因为onDraw会多次调用。
4. 注意位移过程中的效果，View随位移距离变化时，白箭头是透明度渐变，同时直线由短边长再折弯成角。由此可知，需要用View最大宽度与当前宽度的比例来控制透明度、线段长度与角度。

省略其他set代码，核心代码如下：
```java
    /**
     * 因为过程中会多次绘制，所以要先重置路径再绘制。
     * 贝塞尔曲线没什么好说的，相关文章有很多。此曲线经我测试比较类似“即刻App”。
     * <p>
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

    /**
     * 更新当前拉动距离并重绘
     *
     * @param slideLength 当前拉动距离
     */
    public void updateSlideLength(float slideLength) {
        this.slideLength = slideLength;
        invalidate(); // 会再次调用onDraw
    }
```

##### `decorView`的`OnTouchListener`事件

在一个单独的管理器里，传入Activity对象，用以获取该Activity的`decorView`对象，将*黑底白箭头View*添加进去并设置触摸监听`OnTouchListener` - [SlideBackManager](https://github.com/ParfoisMeng/SlideBack/blob/master/slidebacklib/src/main/java/com/parfoismeng/slidebacklib/SlideBackManager.java)。
监听事件的逻辑上面已经分析过，核心代码如下：
```
        FrameLayout container = (FrameLayout) activity.getWindow().getDecorView();
        container.addView(slideBackIconView);
        container.setOnTouchListener(new View.OnTouchListener() {
            private boolean isSideSlide = false;  // 是否从边缘开始滑动
            private float downX = 0; // 按下的X轴坐标
            private float moveXLength = 0; // 位移的X轴距离

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
                            moveXLength = Math.abs(event.getRawX() - downX); // 获取X轴位移距离
                            if (moveXLength / dragRate <= maxSlideLength) {
                                // 如果位移距离在可拉动距离内，更新SlideBackIconView的当前拉动距离并重绘
                                slideBackIconView.updateSlideLength(moveXLength / dragRate);
                            }
                            // 根据Y轴位置给SlideBackIconView定位
                            setSlideBackPosition(slideBackIconView, (int) (event.getRawY()));
                        }
                        break;
                    case MotionEvent.ACTION_UP: // 抬起
                        // 是从边缘开始滑动 且 抬起点的X轴坐标大于某值(默认3倍最大滑动长度)
                        if (isSideSlide && moveXLength / dragRate >= maxSlideLength) {
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
```

##### 其他

侧滑管理器中两个主要方法：
1. 注册 - 传入Activity对象，获取对应的`decorView`，将上述自定义的View添加进布局`container.addView(slideBackIconView)`，并`setOnTouchListener`。
2. 解绑 - 将管理器中的变量置空垃圾回收。此操作不做也可以，无引用时会自动回收。

方便统一管理，我们使用`WeakHashMap<Activity, SlideBackManager>`去管理每个Activity对应的侧滑管理器。注册时加入map，解绑时移除。同时因为`WeakHashMap`内部使用弱引用，也避免了误操作导致的内存泄漏。
其他不详述了，具体参见[SlideBack](https://github.com/ParfoisMeng/SlideBack/blob/master/slidebacklib/src/main/java/com/parfoismeng/slidebacklib/SlideBack.java)。

### 总结

身为一个程序猿，抽象思维很重要。脱离现象去看本质，把现象拆解成好几步，只要你拆解得够细致，每一步都有可以实现的代码。串起来就是一个实现思路，有了思路再实践去验证。做了就明白了。
另外，多学多看。Github那么多优质开源项目可学习，没事多看看，从中学习别人思路与逻辑，理解转化成自己的东西。长此以往好处不言而喻。
PS：看完还有不懂的话，请阅读源码。还有问题可以[issues](https://github.com/ParfoisMeng/SlideBack/issues)提问。
