package com.zj.album.graphy.views.gestures_view.frescoUtils.libs;

import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.Postprocessor;

public interface BaseFrescoImageView {

    /**
     * 获得当前监听
     * 
     */
    ControllerListener getControllerListener();

    /**
     * 获得当前使用的DraweeController
     * 
     */
    DraweeController getDraweeController();

    /**
     * 获得低级别ImageRequest
     * 
     */
    ImageRequest getLowImageRequest();

    /**
     * 获得当前使用的ImageRequest
     * 
     */
    ImageRequest getImageRequest();

    /**
     * 获得当前使用的RoundingParams
     */
    RoundingParams getRoundingParams();

    /**
     * 是否开启动画
     * 
     */
    boolean isAnim();

    /**
     * 获得当前后处理
     * 
     */
    Postprocessor getPostProcessor();

    /**
     * 获得当前使用的默认图
     * 
     */
    int getDefaultResID();

    /**
     * 获得当前加载的图片
     * 
     */
    String getThumbnailUrl();

    /**
     * 获得当前低分辨率图片
     * 
     */
    String getLowThumbnailUrl();

    /**
     * 获得加载的本地图片
     * 
     */
    String getThumbnailPath();

    /**
     * 是否可以点击重试,默认false
     * 
     */
    boolean getTapToRetryEnabled();

    /**
     * 是否自动旋转
     * 
     */
    boolean getAutoRotateEnabled();
}
