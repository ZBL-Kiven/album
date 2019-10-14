package com.zj.album.graphy.views.gestures_view.frescoUtils.libs;

import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.imagepipeline.request.Postprocessor;

public interface FrescoController {

    String HTTP_PERFIX = "http://";
    String HTTPS_PERFIX = "https://";
    String FILE_PERFIX = "file://";

    /**
     * 加载网络图片
     *  lowUrl 低分辨率图片
     *  url 网络图片
     *  defaultResID 默认图
     */
    void loadView(String lowUrl, String url, int defaultResID);

    /**
     * 加载网络图片
     *  url 网络图片
     *  defaultResID 默认图
     */
    void loadView(String url, int defaultResID);

    /**
     * 加载本地图片
     *  path 图片路劲
     *  defaultRes 默认图
     */
    void loadLocalImage(String path, int defaultRes);

    /**
     * 将该Fresco处理为圆形
     */
    void asCircle();

    /**
     * 用一种颜色来遮挡View以实现圆形，在一些内存较低的机器上推荐使用
     *  overlay_color
     */
    void setCircle(int overlay_color);

    /**
     * 设置圆角
     *  radius
     */
    void setCornerRadius(float radius);

    /**
     * 用一种颜色来遮挡View以实现圆角，在一些内存较低的机器上推荐使用
     *  radius
     *  overlay_color
     */
    void setCornerRadius(float radius, int overlay_color);

    /**
     * 设置边框
     *  color
     *  width
     */
    void setBorder(int color, float width);

    /**
     * 清除所使用的RoundingParams
     */
    void clearRoundingParams();

    /**
     * 设置RoundingParams
     *  roundingParmas
     */
    void setRoundingParmas(RoundingParams roundingParmas);

    /**
     * 设置下载监听器
     *  controllerListener
     */
    void setControllerListener(ControllerListener controllerListener);


    /**
     * 设置后处理
     *  postProcessor
     */
    void setPostProcessor(Postprocessor postProcessor);


    /**
     * 是否开启动画
     *  anim
     */
    void setAnim(boolean anim);

    /**
     * 是否可以点击重试
     *  tapToRetryEnabled
     */
    void setTapToRetryEnabled(boolean tapToRetryEnabled);

    /**
     * 是否自动旋转
     *  autoRotateEnabled
     */
    void setAutoRotateEnabled(boolean autoRotateEnabled);

    /**
     * 设置图片缩放type
     *  scaleType
     */
    void setActualImageScaleType(ScalingUtils.ScaleType scaleType);

    /**
     * 设置图片切换动时间
     *  time
     * */
    void setFadeTime(int time);

}
