<p align="center" >
   <img src = "https://github.com/ZBL-Kiven/album/raw/master/demo/title.png"/>
   <br>
   <br>
   <a href = "http://cityfruit.io/">
   <img src = "https://img.shields.io/static/v1?label=By&message=CityFruit.io&color=2af"/>
   </a>
   <a href = "https://github.com/ZBL-Kiven/album">
      <img src = "https://img.shields.io/static/v1?label=platform&message=Android&color=6bf"/>
   </a>
   <a href = "https://github.com/ZBL-Kiven">
      <img src = "https://img.shields.io/static/v1?label=author&message=ZJJ&color=9cf"/>
  </a>
  <a href = "https://developer.android.google.cn/jetpack/androidx">
      <img src = "https://img.shields.io/static/v1?label=supported&message=AndroidX&color=8ce"/>
  </a>
  <a href = "https://www.android-doc.com/guide/components/android7.0.html">
      <img src = "https://img.shields.io/static/v1?label=minVersion&message=Nougat&color=cce"/>
  </a>
</p>

## Introduction：

###### Z-Album 是为 Android 应用设计的相册快速使用框架 ，使用 [java]() 和 [Kotlin]() 语言混合开发，支持广泛使用场景。并具备功能多、配置灵活、稳定性强、内存占用少、内存回收彻底、运行流畅等优点，另外，它的细节设计和交互设计也非常的多。

> ZAlbum 已为 AndroidX 提供，请升级至 V1.0.1 版本。

## Features：

> 基本功能：

- 支持 相册展示
- 支持 单选、多选
- 支持 原图勾选
- 支持 图片预览
- 支持 视频预览
- 支持 权限请求
- 支持 文件夹列表

> 扩展功能：

- 支持 图片视频同窗循环轮播
- 支持 视频播放时长跟踪
- 支持 视频进度条拖动及动态预览
- 支持 图片手势放大，多指触控
- 支持 全景预览

> 可配置支持：

- 支持 配置文件过滤条件
- 支持 配置指定展示文件类型
- 支持 配置指定路径文件忽略
- 支持 配置文件大小过滤
- 支持 配置初始默认选择文件
- 支持 配置原图选项默认值
- 支持 配置最大选择数量
- 支持 配置每种类型（MimeType）各自的最大选择数量
- 支持 配置图片视频选择逻辑
- 支持 配置原图属性为‘共用’ 或 ‘独立’
- 支持 配置换文案、换肤、换 iCon、换颜色、自定义布局
- 支持 配置转场特效，Page 切换特效、图片缩放特效等

> 单元测试

- 即将覆盖

## demo：

使用 Android 设备下载 [APK](https://github.com/ZBL-Kiven/album/raw/master/demo/album.apk) 安装包安装 demo 即可把玩。

## Installation :


ZAlbum 已发布至私有仓库，你可以使用如下方式安装它：

> by dependencies:

```grovy
repo{
     maven (url = "https://nexus.i-mocca.com/repository/cf_core")
}

implementation 'com.cf.core:album:+'
```

> by [aar](https://nexus.i-mocca.com/repository/cf_core/com/cf/core/album/1.0.0/album-1.0.0.aar) import:

```
copy the aar file in your app libs to use
```

> by [module](https://github.com/ZBL-Kiven/album/archive/master.zip) copy:

```
copy the module 'zj_album' into your app

implementation project(":zj_album")

```

## Usage:

> #### 调用：<br>

```
AlbumIns.with(ctx)
```

> #### 可选配置:<br>

配置名|简介|默认
:-:|:-:|:-:
.maxSelectedCount(int) |最大可选数量，默认不限制。当 mutableTypeSize 生效时该配置无效。|Int.maxValue
.mutableTypeSize() |每种类型的最大可选数量，不配置时跟随 maxSelectedCount 配置。|null
.ignorePaths(vararg String)|忽略目标文件或文件夹，可单传或多传，用逗号隔开。|null
.mimeTypes(AlbumOptions.of * ) |可精确指定从手机 SD 卡查询数据的类型，默认为全部视频和图片。|null
.imgSizeRange(longRange) |文件最小大小限制,单位为 B ,传人区间，如：10000, 20000000 ，小于 10KB 的和 大于 20M 的将被忽略。|0 .. Long.maxValue
.videoSizeRange(longRange) | 同上|同上
.selectedUris(uris:ArrayList<SimpleSelectInfo>)|配置默认选中项，相册初始化后默认选中项将被勾选，注：此选项与最大选择数配置冲突时，首次进入仍保持已选择状态，更改后按最大选择数配置生效。|null
.setOriginalPolymorphism(Boolean)|true 多选原图 or false 统一原图|true
.simultaneousSelection(Boolean)|图片视频是否允许同时选择|true
.sortWithDesc(Boolean)|默认排序还是时间倒序|true
.useOriginDefault(Boolean)|原图默认勾选|false
.pagerTransitionEffect(TransitionEffect)|预览页页面切换动效|TransitionEffect.Zoom
.imageScaleEffect(effect: ScaleEffect)|预览页图片缩放动效|ScaleEffect.CUBIC

> #### 跳转并接收回调：

```
.start(call: (Boolean, List<FileInfo>?) -> Unit)
```

## Example of usage

> 示例：直接用：

```
AlbumIns.with(ctx).start { isOK , data ->
     // isOk , or cancel
     // data is the selected files list
}
```

> 示例：全配置:

```
 AlbumIns.with(ctx)
       .maxSelectedCount(9)
       .sortWithDesc(true)
       .useOriginDefault(false)
       .simultaneousSelection(true)
       .setOriginalPolymorphism(true)
       /**--- 配置某种类型的文件可选数量 --- */
       .mutableTypeSize()
       .addNewRule("Image",3,AlbumOptions.ofStaticImage())
       .addNewRule("Gif", 1, EnumSet.of(MimeType.GIF))
       .addNewRule("Video",2,AlbumOptions.ofVideo())
       .set()
       /**------ */
       .selectedUris(arrayListOf("path1","path2"))
       .ignorePaths("QQ","wechat")
       .imgSizeRange(1, 10 * 1000 * 1000)
       .videoSizeRange(1, 200 * 1000 * 1000)
       .imageScaleEffect(ScaleEffect.QUAD)
       .pagerTransitionEffect(TransitionEffect.Zoom)
       .mimeTypes(AlbumOptions.pairOf(AlbumOptions.ofImage(), AlbumOptions.ofVideo()))
       .start { isOK , data ->
             // isOk ,or cancel
             // data is the selected files list
       }
```


## Theme Custom

* ZAlbum 允许使用者自定义页面 UI，<font color=red> 为防止使用者无意或随意更改 UI 导致相册框架不稳定，框架采取 UI & 逻辑完全分离的架构设计，故仅提供以下方式供使用者修改页面。</font>接入者在 App resource 中按给定 ID 自定义，运行时这些 ID 就会替换掉相册内的资源，以达到换肤、换字、换色、换资源图片等的效果，详细 ID 可直接在 [manifest](https://github.com/ZBL-Kiven/album/blob/master/demo/theme-manifest.txt) 中查看。

* example：

```
<resource>
    <string name="pg_str_all">全部</string>
    <string name="pg_str_cancel">返回</string>
    <string name="pg_str_preview">預覽</string>
</resource>
```
###### 以上代码定义在 APP 资源文件后，相册默认的资源则会失效，运行时使用的资源文件即为 APP 定制的。不按给定 ID 覆盖的视为 <font color=red> 无效操作 </font>

### Contributing

Contributions are very welcome 🎉

### Licence :
NTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON
Copyright (c) 2019 CityFruit zjj0888@gmail.com<br>
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.<br>
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRAINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.