# LoadingView
贝塞尔曲线绘制的LoadingView

## 引入项目
1. 在Project下的build.gradle中

```
buildscript {
    ext.kotlin_version = '1.3.60'
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" } //添加jitpack仓
    } 
 }
```

2. 在app下的build.gradle中

```
dependencies {
    ...
    implementation 'com.github.baimadev:LoadingView:1.0.3'
}
```

## 如何使用

beginAnim()开始动画

pauseAnim()暂停动画

 ``` 
 <com.example.loadingview.BMLoadingView
        android:id="@+id/anim"
        app:gradientColor1="@color/wh"
        app:gradientColor2="@color/light_green"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
       
```
       
gradientColor设置渐变色。
