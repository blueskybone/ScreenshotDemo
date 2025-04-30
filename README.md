# ScreenshotDemo
Android获取全局截图demo。无root。

## 原理
创建虚拟显示器进行录屏，从录屏中获取画面缓冲序列，取出后转化为bitmap。

## 方法

动态获取录屏权限后，创建VirtualDisplay，从ImageReader中获取图片缓冲，释放VirtualDisplay。获取的图片转化为bitmap后，可以保存或做其他用途。此demo的后处理为保存bitmap到本地相册。

此外，通过MediaProjection获取屏幕信息时，必须启动一个前台Notification显式提示用户当前正在获取屏幕。整个过程还要额外开启一个Notification并设置为foreground。

## 实现细节

1. 因为无法避免要开启一个前台Service显示通知，故实现上整个屏幕捕获过程托管在一个Service实现，包括管理Notification，维护MediaProjection，维护一个定时器用于Service自杀。每次需要使用截图时StartService即可。
2. 整个实现的关键在于获取录屏权限后，创建MediaProjection，并维护该MediaProjection。A14版本以前有另一种方法是保存录屏权限的intent，后续创建MediaProjection都依赖该Intent，这个漏洞（并非漏洞）在A14被修复了，现在最好也只能做到每一次运行期间维护一个MediaProjection以避免反复授权。
3. **关于 VirtualDisplay 参数设置**：demo中设置为**VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR**，直接获取屏幕的缓冲。但有些情况无法使用这个FLAG，另一种实现思路为设置Flag为**VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY**，这个标志将只捕获自己应用的显示器内容，所以还要额外开启一个透明的Activity覆盖在屏幕上，然后获取这个透明Activity的缓冲，达到截图其他应用画面的目的，这个方法因为会快速进行Activity的切换，可能会造成一些卡顿，甚至较为严重的bug。

> #### VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR
>
> 虚拟显示标志：当没有显示内容时，允许将内容镜像到私人显示器上。此标志仅用于在创建私有显示时覆盖默认行为。
>
> **另外：创建自动镜像虚拟显示器需要 CAPTURE_VIDEO_OUTPUT 或 CAPTURE_SECURE_VIDEO_OUTPUT 权限。 这些权限保留供系统组件使用，第三方应用程序无法使用。 或者，可以使用适当的MediaProjection来创建自动镜像虚拟显示。**
>
> #### VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
>
> 虚拟显示标志：只显示该显示器本身的内容； 不要镜像另一个显示器的内容。
> 此标志与VIRTUAL_DISPLAY_FLAG_PUBLIC结合使用。 通常，如果公共虚拟显示器没有自己的窗口，它们将自动镜像默认显示器的内容。 当这个标志被指定时，虚拟显示器将只显示它自己的内容，如果它没有窗口，它将被消隐。
>
> 来自[VirtualDisplay的Flag参数讲解](https://www.cnblogs.com/liming-1943546556/p/15544714.html)

4. demo在代码中设置了1s截图延迟，可以自己修改。

## 效果

测试截图平均时间约0.5s，体感上的停顿可以忽略不计。

## 局限

此方法无法和系统录屏共存。当开启屏幕录制时，使用此方法截图会出现VirtualDisplay创建失败。

## 引用

悬浮窗框架：[EasyWindow](https://github.com/getActivity/EasyWindow)

吐司框架：[Toaster](https://github.com/getActivity/Toaster)

图标来源：https://ak.hypergryph.com/mymind
