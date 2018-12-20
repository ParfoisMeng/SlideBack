# SlideBack
无需继承的Activity侧滑返回库 类全面屏返回手势效果
[![](https://jitpack.io/v/ParfoisMeng/SlideBack.svg)](https://jitpack.io/#ParfoisMeng/SlideBack)

---

##### 前情
最近一直在研究侧滑返回效果的实现，目前比较多的方案如下：

 1. 背景透明主题。问题是性能与神坑"Only fullscreen activities can request
    orientation"。
 2. 将上页ContentView绘制到当前页，侧滑时动画推入推出。（也许挺不错？）
 3. 类全面屏返回手势。[即刻App](https://www.ruguoapp.com/)的效果（下图）。

本库这里选择了方案3。

#### 预览
![即刻App](https://github.com/ParfoisMeng/SlideBack/blob/master/screenshot/jike.gif)
![本库](https://github.com/ParfoisMeng/SlideBack/blob/master/screenshot/mine.gif)
[Demo下载](https://github.com/ParfoisMeng/SlideBack/blob/master/demo/demo.apk)

#### 使用
[![](https://jitpack.io/v/ParfoisMeng/SlideBack.svg)](https://jitpack.io/#ParfoisMeng/SlideBack)
 - 引用类库 *请将last-version替换为最新版本号
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
		implementation 'com.github.parfoismeng:slidebacklib:last-version'
	}
```
- 代码使用
```
class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 在需要滑动返回的Activity中注册
        SlideBackUtils.register(this) {
            Toast.makeText(this, "SlideBack", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // onDestory时记得解绑
        // 内部使用WeakHashMap，理论上不解绑也行，但最好还是手动解绑一下
        SlideBackUtils.unregister(this)
    }
}
```
OJBK！So easy！

#### 性能
附一张性能截图。可以看出来中间进行了很多次 onCreate & onDestory，最后内存和开始时一致：
![
MEMORY](https://github.com/ParfoisMeng/SlideBack/blob/master/screenshot/memory.png)

#### 感谢
感谢 [ChenTianSaber](https://github.com/ChenTianSaber)  的开源库 [SlideBack](https://github.com/ChenTianSaber/SlideBack) （[掘金](https://juejin.im/post/5b7a837cf265da432f653617)）提供的思路与源码

#### 支持
<img src="https://github.com/ParfoisMeng/SlideBack/blob/master/screenshot/alipay.jpg" width="420px"/>
<img src="https://github.com/ParfoisMeng/SlideBack/blob/master/screenshot/wechat.png" width="420px"/>