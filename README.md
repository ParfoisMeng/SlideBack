# SlideBack
无需继承的 Activity 侧滑返回库，类全面屏返回手势效果 仿 “即刻App” 侧滑返回。 [![JitPack](https://jitpack.io/v/ParfoisMeng/SlideBack.svg)](https://jitpack.io/#ParfoisMeng/SlideBack)

- - - - -

### 前情
最近一直在研究侧滑返回效果的实现，目前比较多的方案如下：

1. 背景透明主题。问题是性能与神坑 "Only fullscreen activities can request orientation"。
2. 将上页 ContentView 绘制到当前页，侧滑时动画推入推出。（也许挺不错？）
3. 类全面屏返回手势。[即刻App](https://www.ruguoapp.com/) 的效果（下图）。

本库这里选择了方案3。

### 预览
| 即刻App | 本库 | Demo 下载 |
| :---: | :---: | :---: |
| <img src="https://github.com/ParfoisMeng/SlideBack/raw/master/screenshot/jike.gif" width="260px"/> | <img src="https://github.com/ParfoisMeng/SlideBack/raw/master/screenshot/mine.gif" width="260px"/> | <img src="https://github.com/ParfoisMeng/SlideBack/raw/master/demo/demo_qr.gif" width="260px"/><br><br>[Demo 下载](https://raw.githubusercontent.com/ParfoisMeng/SlideBack/master/demo/demo.apk) |

### 使用
 - 引用类库 *请将 last-version 替换为最新版本号 [![](https://jitpack.io/v/ParfoisMeng/SlideBack.svg)](https://jitpack.io/#ParfoisMeng/SlideBack)
```
    // 1.添加 Jitpack 仓库
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
    // 2.添加项目依赖（ last-version 替换为最新版本号）
    dependencies {
        implementation 'com.github.ParfoisMeng:SlideBack:last-version'
    }
```

- 代码使用
```
    // 在需要滑动返回的 Activity 中注册
    Activity.registerSlideBack(haveScroll: Boolean = true, callBack: () -> Unit)

    // 可以在 [custom: SlideBack.() -> Unit] 中修改 IconView 的样式
    Activity.registerSlideBack(haveScroll: Boolean = true, callBack: () -> Unit, custom: SlideBack.() -> Unit)

    // onDestroy 时解绑
    // 内部使用 WeakHashMap，理论上不解绑也行
    Activity.unregisterSlideBack()

    // 在 Java 中使用
    // 注册
    SlideBackKt.registerSlideBack(Activity $this$registerSlideBack, boolean haveScroll, Function0 callBack, Function1 custom)
    // 解绑
    SlideBackKt.unregisterSlideBack(Activity $this$unregisterSlideBack);
```

OJBK！So easy！

- Fragment 支持，详见 [issues#2](https://github.com/ParfoisMeng/SlideBack/issues/2)
  1. 在 Fragment 的父级 Activity 中注册 SlideBack
  2. 在 CallBack 中 remove 栈顶的 Fragment

### 性能
附一张性能截图。可以看出来中间进行了很多次 onCreate & onDestroy，最后内存和开始时一致：<br>
![MEMORY](https://github.com/ParfoisMeng/SlideBack/raw/master/screenshot/memory.png)

### 分析
源码分析MD：[Analysis.md](https://github.com/ParfoisMeng/SlideBack/blob/master/Analysis.md) & [Analysis_v2.md](https://github.com/ParfoisMeng/SlideBack/blob/master/Analysis_v2.md)

### 感谢
感谢 [ChenTianSaber](https://github.com/ChenTianSaber)  的开源库 [SlideBack](https://github.com/ChenTianSaber/SlideBack) （[掘金](https://juejin.im/post/5b7a837cf265da432f653617)）提供的思路与源码

### 更新
* 1.1.1 - 修改触摸监听中触点坐标获取方法 `event.getRawX() -> event.getX()` [issues#12](https://github.com/ParfoisMeng/SlideBack/issues/12)
* 1.1.0 - 改动较大，请酌情考虑是否升级
    * Migrate to AndroidX 且全部改用 Kotlin 语言
    * 移除 annotation 约束 (为了去掉一个依赖包)
    * 不再支持反直觉的右侧侧滑
* 1.0.7 - Bugfix - 快速滑动时指示箭头未到达最大状态 [issues#8](https://github.com/ParfoisMeng/SlideBack/issues/8)
* 1.0.6 - Bugfix - 包含滑动控件的页面设置 EDGE_BOTH 时右侧滑动失效 [issues#6](https://github.com/ParfoisMeng/SlideBack/issues/6)
* 1.0.5 - 支持设置屏幕左右侧侧滑 [issues#4](https://github.com/ParfoisMeng/SlideBack/issues/4)
* 1.0.4 - 提供新的可配置更多参数的注册方法（旧方法仍然可用）
* 1.0.3 - Bugfix - 滑动事件冲突 [issues#1](https://github.com/ParfoisMeng/SlideBack/issues/1)
* 1.0.2 - 删除无用依赖，添加Java引用示例
* 1.0.1 - 检查警告，修改类名，更新README.md
* 1.0.0 - 初版发布

### 计划
- [x] 升级 AndroidX，改 Kotlin 语言 已完成
- [ ] 滑动事件冲突的解决方案 (1.0.3) 不是很理想，但大家好像都是这么解决的，如果您有更好的方案，请与我沟通，十分欢迎PR。
- [ ] 源码分析MD给郭婶投两稿也没通过……<del>啥时候有想法再重写吧 :-(</del>
- [ ] 看情况吧......

### 支持
劳烦各位大佬给个 **Star** 让我出去 **装B** 行嘛！

### 其他
已使用 **996 License**，为程序员发声，为自己发声。

[![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu)
