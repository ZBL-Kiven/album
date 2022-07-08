package com.zj.album.ui.preview.images.transformer

import androidx.viewpager.widget.ViewPager

/**
 * @author ZJJ on 2019.10.24
 */
@Suppress("unused")
enum class TransitionEffect(internal val effect: ViewPager.PageTransformer) {
    Default(DefaultPageTransformer()),
    Alpha(AlphaPageTransformer()),
    Rotate(RotatePageTransformer()),
    Cube(CubePageTransformer()),
    Flip(FlipPageTransformer()),
    Accordion(AccordionPageTransformer()),
    ZoomFade(ZoomFadePageTransformer()),
    Fade(FadePageTransformer()),
    ZoomCenter(ZoomCenterPageTransformer()),
    ZoomStack(ZoomStackPageTransformer()),
    Stack(StackPageTransformer()),
    Depth(DepthPageTransformer()),
    Zoom(ZoomPageTransformer())
}