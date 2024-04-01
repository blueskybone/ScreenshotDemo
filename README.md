# ScreenshotDemo
A demo shows how to take a screenshot in global system field in native android. Detached from another project Arkscreen. Written in kotlin.

# TODO
先开个坑，晚点在写

## 原理
通常有3种方法可以捕获应用外画面，但一种需要root权限，另一种通过反射触发系统原生截图会产生截图动画效果（并且高版本的android中反射很不方便），最后的方案是从录屏活动中获取画面缓冲序列，取出作为截图。

基本原理上是打开一个透明Activity，创建virtualdisplay，启动录屏，获取图片缓冲，结束录屏，释放virtualdisplay。

为什么创建一个透明Activity，而不是直接获取整个系统的virtualdisplay？~~可能会导致应用卡死，你可以逝一逝。原因暂时没去深究，只能确定是创建全局virtualdisplay会导致某些冲突。~~

## 效果
由于获取屏幕的时间极短，测试截图导致屏幕占用平均时间约0.3s，体感上停顿可以忽略不计。作为一种非入侵式的屏幕获取方案还是很不错的。

## 其他
为展示全局效果，使用android快捷开关面板触发截图选项。


有一些局限性：

1. 除了华为系的系统，此方法无法和系统录屏共存。如果在系统录屏时使用截图由于virtualdisplay正在被占用，会导致创建失败。华为系可能采用了其他的录屏方案。

2. 无法突破某些不允许录屏的应用，比如你B的漫画。~~题外话，以前有通过录屏的手段来规避禁止截图的机制，现在不知道还行不行。~~
