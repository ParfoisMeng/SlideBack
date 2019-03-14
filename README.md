# SlideBack

无需继承的Activity侧滑返回库 类全面屏返回手势效果 仿“即刻”侧滑返回  [![JitPack](https://jitpack.io/v/ParfoisMeng/SlideBack.svg)](https://jitpack.io/#ParfoisMeng/SlideBack)

- - - - - 

### 前情

最近一直在研究侧滑返回效果的实现，目前比较多的方案如下：

1. 背景透明主题。问题是性能与神坑"Only fullscreen activities can request orientation"。
2. 将上页ContentView绘制到当前页，侧滑时动画推入推出。（也许挺不错？）
3. 类全面屏返回手势。[即刻App](https://www.ruguoapp.com/)的效果（下图）。

本库这里选择了方案3。

### 预览

| 即刻App | 本库 | Demo下载 |
| :---: | :---: | :---: |
| <img src="https://github.com/ParfoisMeng/SlideBack/raw/master/screenshot/jike.gif" width="260px"/> | <img src="https://github.com/ParfoisMeng/SlideBack/raw/master/screenshot/mine.gif" width="260px"/> | <img src="https://github.com/ParfoisMeng/SlideBack/raw/master/demo/demo_qr.gif" width="260px"/><br><br>[Demo下载](https://raw.githubusercontent.com/ParfoisMeng/SlideBack/master/demo/demo.apk) |

### 使用

- 引用类库 *请将last-version替换为最新版本号 [![](https://jitpack.io/v/ParfoisMeng/SlideBack.svg)](https://jitpack.io/#ParfoisMeng/SlideBack)

```
// 1.添加jitpack仓库
allprojects {
repositories {
...
maven { url 'https://jitpack.io' }
}
}
// 2.添加项目依赖（last-version替换为最新版本号）
dependencies {
implementation 'com.github.parfoismeng:slideback:last-version'
}
```

- 代码简单使用，详见下方API

```
// 在需要滑动返回的Activity中注册，最好但非必须在onCreate中
SlideBack.with(this)
.callBack(new SlideBackCallBack() {
@Override
public void onSlideBack() {
finish();
}
})
.register();

// onDestroy时解绑
// 内部使用WeakHashMap，理论上不解绑也行，但最好还是手动解绑一下
SlideBack.unregister(this);
```

OJBK！So easy！

#### 其他问题

- 旧方法仍然可用

<details>
<summary>点击展开查看</summary>

```java
// Kotlin
class SecondActivity : AppCompatActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
// 在需要滑动返回的Activity中注册
SlideBack.register(this) {
Toast.makeText(this, "SlideBack", Toast.LENGTH_SHORT).show()
}
}

override fun onDestroy() {
super.onDestroy()
// onDestroy时记得解绑
// 内部使用WeakHashMap，理论上不解绑也行，但最好还是手动解绑一下
SlideBack.unregister(this)
}
}

// Java
public class SecondActivity extends AppCompatActivity {
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
// 在需要滑动返回的Activity中注册
SlideBack.register(this, new SlideBackCallBack() {
@Override
public void onSlideBack() {
Toast.makeText(SecondActivity.this, "SlideBack", Toast.LENGTH_SHORT).show();
}
});
}

@Override
protected void onDestroy() {
super.onDestroy();
// onDestroy时记得解绑
// 内部使用WeakHashMap，理论上不解绑也行，但最好还是手动解绑一下
SlideBack.unregister(this);
}
}

// 如果需要在有可滑动View(RecycleView/ScrollView等)的Activity中使用，请使用此注册方法。
// haveScroll：页面是否有滑动
SlideBack.register(Activity activity, boolean haveScroll, SlideBackCallBack callBack)
```

</details>

- Fragment 支持，详见 [issues#2](https://github.com/ParfoisMeng/SlideBack/issues/2)

1. 在Fragment的父级Activity中注册SlideBack
2. 在SlideBackCallBack中remove栈顶的Fragment

### API

```java
SlideBack.with(this) // 新 构建侧滑管理器 - 用于更丰富的自定义配置
.haveScroll(false) // 是否包含滑动控件 默认false
.callBack(new SlideBackCallBack() { // 回调
@Override
public void onSlideBack() {
// TODO 回调事件
}
})
.edgeMode(edgeMode) // 边缘侧滑模式 默认左
.callBack(new SlideCallBack() { // 回调2 适用于设置左右模式 *两种回调同时写，后者会覆盖前者
@Override
public void onSlide(int edgeFrom) {
// TODO edgeFrom(滑动来源): EDGE_LEFT(左侧) EDGE_RIGHT(右侧)
}
})
.viewHeight(viewHeightDP) // 控件高度 默认屏高/4
.arrowSize(arrowSizeDP) // 箭头大小 默认5dp
.maxSlideLength(maxSlideLengthDP) // 最大拉动距离（控件最大宽度） 默认屏宽/12
.sideSlideLength(sideSlideLengthDP) // 侧滑响应距离 默认控件最大宽度/2
.dragRate(dragRate) // 阻尼系数 默认3（越小越灵敏）
.register();
```

### 性能

附一张性能截图。可以看出来中间进行了很多次 onCreate & onDestroy，最后内存和开始时一致：  
![
MEMORY](https://github.com/ParfoisMeng/SlideBack/raw/master/screenshot/memory.png)

### 分析

源码分析MD：[Analysis.md](https://github.com/ParfoisMeng/SlideBack/blob/master/Analysis.md) & [Analysis_v2.md](https://github.com/ParfoisMeng/SlideBack/blob/master/Analysis_v2.md)

### 感谢

感谢 [ChenTianSaber](https://github.com/ChenTianSaber)  的开源库 [SlideBack](https://github.com/ChenTianSaber/SlideBack) （[掘金](https://juejin.im/post/5b7a837cf265da432f653617)）提供的思路与源码

### 更新

1. 提供新的可配置更多参数的注册方法（旧方法仍然可用） - 1.0.4
2. 滑动事件冲突问题，修复 [issues#1](https://github.com/ParfoisMeng/SlideBack/issues/1) - 1.0.3
3. 删除无用依赖，添加Java引用示例 - 1.0.2
4. 检查警告，修改类名，更新README.md - 1.0.1
5. 初版发布 - 1.0.0

### 计划

1. 源码分析MD没通过。准备抽时间重写一下。<del>再投一遍试试 :-)</del>
2. 貌似有屏幕右侧侧滑的需求？[issues#4](https://github.com/ParfoisMeng/SlideBack/issues/4)
3. 目前滑动事件冲突的解决方案(1.0.3)不是很理想，但市面上好像都是这么解决的，如果您有更好的方案，请与我沟通，十分欢迎PR。
4. 看情况吧......

### 支持

劳烦各位大佬给个Star让我出去好装B行嘛！

### 其他

<!--
- 加我好友（请注明来意）

| QQ | WeChat | Email |
| :---: | :---: | :---: |
| 757479544<br><br><img src="https://github.com/ParfoisMeng/SlideBack/raw/master/screenshot/qq_qr.jpg" width="260px"/> | youshi520000<br><br><img src="https://github.com/ParfoisMeng/SlideBack/raw/master/screenshot/wx_qr.png" width="260px"/> | youshi520000@163.com<br><br>yi520000@vip.qq.com<br><br>youshi.meng@gmail.com<br><br>parfois.meng@icloud.com<br><br>parfois.meng@outlook.com |
-->

<details>
<summary> 赏杯咖啡 </summary>

<br>

| Alipay | WeChat Pay |
| --- | --- |
| <img src="https://github.com/ParfoisMeng/SlideBack/raw/master/screenshot/alipay.jpg" width="260px"/> | <img src="https://github.com/ParfoisMeng/SlideBack/raw/master/screenshot/wechat.png" width="260px"/> |

</details>
