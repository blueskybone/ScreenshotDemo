# ScreenshotDemo
Android获取全局截图demo。无root。

此demo来源于项目[Arkscreen](github.com/blueskybone/ArkScreen)。

## 原理
创建虚拟显示器进行录屏，从录屏中获取画面缓冲序列，取出后转化为bitmap。

## 方法

动态获取录屏权限后，启动一个透明Activity，创建VirtualDisplay，从ImageReader中获取图片缓冲，释放VirtualDisplay，退出Activity。获取的图片转化为bitmap后，可以保存，或做其他用途。此demo的后处理为保存bitmap到本地相册。

另外从Android不知道哪个版本开始，通过MediaProjection获取屏幕信息时，必须启动一个前台Notification显式提示用户当前正在获取屏幕。所以整个过程还要额外开启一个Notification Service并设置为前台。

为什么创建一个透明Activity，而不是直接获取整个屏幕的VirtualDisplay？~~这可能会导致其他应用卡死，你可以逝一逝。~~ 原因暂时没去深究，只能定位到是由于创建了全局VirtualDisplay导致的某些冲突。

> #### VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
>
> 虚拟显示标志：只显示该显示器本身的内容； 不要镜像另一个显示器的内容。
> 此标志与VIRTUAL_DISPLAY_FLAG_PUBLIC结合使用。 通常，如果公共虚拟显示器没有自己的窗口，它们将自动镜像默认显示器的内容。 当这个标志被指定时，虚拟显示器将只显示它自己的内容，如果它没有窗口，它将被消隐。
>
> 来自[VirtualDisplay的Flag参数讲解](https://www.cnblogs.com/liming-1943546556/p/15544714.html)

## 效果
测试截图平均时间约0.5s，体感上的停顿可以忽略不计。

## 局限

1. A14开始无法保存录屏动态授权结果，导致每次获取屏幕都必须动态获取权限并更新mediaProjectionManager，当前源码版本暂时未加入此特性兼容，请自行修改。
  
2. 除了华为系统，此方法无法和系统录屏共存。当开启屏幕录制时，使用此方法截图会出现VirtualDisplay创建失败。

## 引用

悬浮窗框架：[EasyWindow](https://github.com/getActivity/EasyWindow)

吐司框架：[Toaster](https://github.com/getActivity/Toaster)

图标来源：https://ak.hypergryph.com/mymind
