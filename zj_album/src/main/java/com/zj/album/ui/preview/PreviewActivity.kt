package com.zj.album.ui.preview

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import com.zj.album.R
import com.zj.album.imageloader.impl.GlideLoader
import com.zj.album.nModule.FileInfo
import com.zj.album.nutils.getValueBySafe
import com.zj.album.ui.base.BaseActivity
import com.zj.album.ui.preview.images.BannerViewPager
import com.zj.album.ui.preview.images.OnPageChange
import com.zj.album.ui.preview.images.transformer.TransitionEffect
import com.zj.album.ui.preview.player.VideoView
import com.zj.album.ui.views.image.ImageViewTouch
import java.lang.ref.WeakReference

internal class PreviewActivity : BaseActivity() {

    companion object {

        private const val LAUNCH_IS_VIDEO = "is_video"
        private const val LAUNCH_PATH = "path"

        fun start(context: Context, data: FileInfo) {
            val i = Intent(context, PreviewActivity::class.java).apply {
                putExtra(LAUNCH_IS_VIDEO, data.isVideo)
                putExtra(LAUNCH_PATH, data.path)
            }
            context.startActivity(i)
        }
    }

    private var initPath: String = ""

    private var previewBanner: BannerViewPager<FileInfo>? = null
    private var selectedRv: RecyclerView? = null
    private var mVideoView: VideoView? = null
        get() {
            if (field == null) field = VideoView(this)
            return field
        }

    override fun getContentView(): Int {
        return R.layout.preview_image_activity
    }

    override fun initView() {
        previewBanner = findViewById(R.id.preview_vp)
        selectedRv = findViewById(R.id.preview_rv_select)
    }

    override fun initData() {
        initPath = intent.getValueBySafe(LAUNCH_PATH, "")
    }

    override fun onDataDispatch(data: List<FileInfo>?, isQueryTaskRunning: Boolean) {
        previewBanner?.init(0, TransitionEffect.Zoom, object : OnPageChange<FileInfo> {
            override fun onChange(data: FileInfo?, view: View) {
                data?.let {
                    val iv = view.findViewById<ImageViewTouch>(R.id.preview_base_iv_img)
                    val vPlay = view.findViewById<ImageView>(R.id.preview_base_btn_video_play)
                    val flContainer = view.findViewById<FrameLayout>(R.id.preview_base_fl_video_container)
                    iv.visibility = View.VISIBLE
                    GlideLoader().loadImage(iv, iv.measuredWidth, iv.measuredHeight, data.path)
                    vPlay.setOnClickListener(null)
                    vPlay.isSelected = false
                    vPlay.visibility = if (it.isVideo) View.VISIBLE else View.GONE
                    if (flContainer.childCount > 0) flContainer.removeAllViews()
                    if (it.isVideo) {
                        vPlay.setOnClickListener { play ->
                            val anim = AlphaAnimation(1.0f, 0.0f)
                            anim.duration = 600
                            play.startAnimation(anim)
                            play.isSelected = true
                            play.visibility = View.GONE
                            iv.startAnimation(anim)
                            iv.visibility = View.GONE
                            val videoView = WeakReference(mVideoView)
                            videoView.get()?.let { vv ->
                                (vv.parent as? ViewGroup)?.removeView(vv)
                                vv.autoPlay(true)
                                vv.setData(it.path)
                                flContainer.addView(vv, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                            }
                        }
                    }
                }
            }

            override fun onFocusChange(v: View, data: FileInfo?) {
                data?.let {
                    if (!it.isVideo) return
                    val flContainer = v.findViewById<FrameLayout>(R.id.preview_base_fl_video_container)
                    if (flContainer.childCount > 0) {
                        (flContainer.getChildAt(0) as? VideoView)?.let { videoView ->
                            val iv = v.findViewById<ImageViewTouch>(R.id.preview_base_iv_img)
                            val vPlay = v.findViewById<ImageView>(R.id.preview_base_btn_video_play)
                            vPlay.isSelected = false
                            if (videoView.isResume()) {
                                videoView.pause()
                                videoView.release()
                                val anim = AlphaAnimation(0.0f, 1.0f)
                                anim.duration = 1000
                                vPlay.startAnimation(anim)
                            }
                            vPlay.visibility = View.VISIBLE
                            iv.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onDisplayChange(dataPosition: Int) {

            }
        })
        previewBanner?.setData(R.layout.preview_item_base, data, (data?.indexOfFirst { it.path == initPath }) ?: 0)
    }

    override fun onSelectedStateChange(count: Int) {
        selectedRv?.visibility = if (count > 0) View.VISIBLE else View.GONE
    }
}