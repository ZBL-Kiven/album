package com.zj.album.ui.preview

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.zj.album.R
import com.zj.album.imageloader.impl.GlideLoader
import com.zj.album.nHelpers.DataStore
import com.zj.album.nModule.FileInfo
import com.zj.album.nutils.*
import com.zj.album.options.AlbumConfig
import com.zj.album.options.AlbumConfig.simultaneousSelection
import com.zj.album.ui.base.BaseActivity
import com.zj.album.ui.base.list.listeners.ItemClickListener
import com.zj.album.ui.preview.images.BannerViewPager
import com.zj.album.ui.preview.images.OnPageChange
import com.zj.album.ui.preview.player.VideoController
import com.zj.album.ui.views.image.TouchScaleImageView
import java.util.*

/**
 * @author ZJJ on 2019.10.24
 * */
internal class PreviewActivity : BaseActivity() {

    companion object {
        private const val LAUNCH_PATH = "path"
        private const val LAUNCH_PREVIEW_SELECTED = "preview_selected"
        fun start(context: Activity, isPreviewSelected: Boolean, path: String) {
            val i = Intent(context, PreviewActivity::class.java).apply {
                putExtra(LAUNCH_PREVIEW_SELECTED, isPreviewSelected)
                putExtra(LAUNCH_PATH, path)
            }
            context.startActivityForResult(i, Constance.REQUEST_OPEN_PREVIEW)
        }
    }

    private var initPath = ""
    private var isSelectedPreview = false
    private var isFull = false
    private var previewBanner: BannerViewPager<FileInfo>? = null
    private var selectedRv: RecyclerView? = null
    private var vCancel: View? = null
    private var vSelect: TextView? = null
    private var cbOriginal: CheckBox? = null
    private var vComplete: TextView? = null
    private var seekBar: SeekBar? = null
    private var tvStart: TextView? = null
    private var tvEnd: TextView? = null
    private var llTopBar: View? = null
    private var llBottomBar: View? = null
    private var videoToolBar: View? = null
    private var curFileData: FileInfo? = null
    private var previewSelectedAdapter: PreviewSelectedAdapter? = null
    private var curPreviewData: List<FileInfo>? = null

    override fun getContentView(): Int {
        return R.layout.preview_image_activity
    }

    override fun initView() {
        previewBanner = findViewById(R.id.preview_vp)
        selectedRv = findViewById(R.id.preview_rv_select)
        vCancel = findViewById(R.id.preview_tv_cancel)
        vSelect = findViewById(R.id.preview_tv_num)
        cbOriginal = findViewById(R.id.preview_cb_original)
        vComplete = findViewById(R.id.preview_tv_done)
        seekBar = findViewById(R.id.preview_sb)
        tvStart = findViewById(R.id.preview_tv_start)
        tvEnd = findViewById(R.id.preview_tv_end)
        videoToolBar = findViewById(R.id.preview_tools_bar)
        llTopBar = findViewById(R.id.preview_ll_top)
        llBottomBar = findViewById(R.id.preview_ll_bottom)
    }

    override fun initData() {
        initPath = intent.getValueBySafe(LAUNCH_PATH, "")
        isSelectedPreview = intent.getValueBySafe(LAUNCH_PREVIEW_SELECTED, false)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        selectedRv?.layoutManager = layoutManager
        previewSelectedAdapter = PreviewSelectedAdapter()
        selectedRv?.adapter = previewSelectedAdapter
        initPreviewBanner()
    }

    override fun initListener() {
        vCancel?.setOnClickListener { finish() }
        vSelect?.setOnClickListener {
            curFileData?.let {
                val nextStatus = !it.isSelected()
                it.setSelected(nextStatus)
            }
        }

        cbOriginal?.setOnCheckedChangeListener { _, check ->
            curFileData?.let {
                if (it.isOriginal() == check) return@let
                if (it.isVideo) {
                    cbOriginal?.isChecked = false;return@let
                }
                if (!it.setOriginal(check)) {
                    cbOriginal?.isChecked = false;return@let
                }
            } ?: run {
                cbOriginal?.isChecked = false
            }
        }
        previewSelectedAdapter?.setOnItemClickListener(object : ItemClickListener<FileInfo>() {
            override fun onItemClick(position: Int, v: View?, m: FileInfo?) {
                m?.path?.let {
                    VideoController.stop()
                    initPreviewBanner()
                    setPreviewData(it)
                }
            }
        })

        vComplete?.setOnClickListener {
            completedAndFinish()
        }
    }

    override fun onDataDispatch(data: List<FileInfo>?, isQueryTaskRunning: Boolean) {
        curPreviewData = if (isSelectedPreview) ArrayList(DataStore.getCurSelectedData()) else data
        setPreviewData(initPath)
        setCompletedText(false)
    }

    override fun onSelectedStateChange(count: Int) {
        val hasData = count > 0
        previewSelectedAdapter?.change(DataStore.getCurSelectedData())
        if (hasData && selectedRv?.visibility != View.VISIBLE || !hasData && selectedRv?.visibility != View.GONE) {
            val start = if (hasData) 0.0f else 1.0f
            val end = if (hasData) 1.0f else 0.0f
            showOrHideView(selectedRv, hasData, floatArrayOf(start, end), Constance.ANIMATE_DURATION)
        }
        updateSelectState()
    }

    private fun setPreviewData(curPath: String) {
        previewBanner?.setData(curPreviewData, (curPreviewData?.indexOfFirst { it.path == curPath }) ?: 0)
    }

    private fun initPreviewBanner() {
        previewBanner?.init(R.layout.preview_item_base, 0, AlbumConfig.pageTransformer, object : OnPageChange<FileInfo> {

            private val onItemClickListener: (v: View) -> Unit = {
                if (VideoController.full(isFull)) full()
            }

            override fun onBindData(data: FileInfo?, view: View) {
                data?.let {
                    val iv = view.findViewById<TouchScaleImageView>(R.id.preview_base_iv_img)
                    val vPlay = view.findViewById<ImageView>(R.id.preview_base_btn_video_play)
                    initViewsWithPagerItemInit(data, vPlay, iv)
                    iv.setSingleTapListener { onItemClickListener(iv) }
                    view.setOnClickListener(onItemClickListener)
                }
            }

            override fun onFocusChange(v: View?, data: FileInfo?, focus: Boolean) {
                if (v != null && data != null) {
                    val iv = v.findViewById<TouchScaleImageView>(R.id.preview_base_iv_img)
                    if (!focus && data.isVideo) {
                        iv.visibility = View.VISIBLE
                    }
                    previewBanner?.setAllowUserScrollable {
                        if (!focus) {
                            true
                        } else {
                            val ivCanScroll = iv.canScroll(1) && iv.canScroll(-1)
                            data.isVideo || !ivCanScroll
                        }
                    }
                    onCurDataChanged(v, data, focus)
                }
            }

            override fun onScrollStateChanged(interval: Float, state: Int) {
                setViewsEnable(state == ViewPager.SCROLL_STATE_IDLE)
            }
        })
    }

    private fun initViewsWithPagerItemInit(data: FileInfo, play: View, iv: TouchScaleImageView) {
        iv.setScaleEnabled(!data.isVideo)
        iv.doubleTapEnabled = !data.isVideo
        iv.setDoubleTapListener(null)
        val path = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) data.path else data.getContentUri()
        GlideLoader().loadImage(iv, iv.measuredWidth, iv.measuredHeight, path)
        if (iv.visibility != View.VISIBLE) showOrHideView(iv, true, floatArrayOf(0.0f, 1.0f), Constance.ANIMATE_DURATION)
        play.setOnClickListener(null)
        play.isSelected = false
        play.visibility = if (data.isVideo) View.VISIBLE else View.GONE
    }

    private fun onCurDataChanged(v: View, data: FileInfo, focus: Boolean) {
        VideoController.onFocus(focus)
        if (!focus) {
            if (isFull) full()
            VideoController.stop()
        } else {
            curFileData = data
            initDataWithPagerSelected(v as FrameLayout)
            updateSelectState()
        }
    }

    private fun updateSelectState() {
        curFileData?.let { data ->
            vSelect?.isSelected = data.isSelected()
            cbOriginal?.isChecked = data.isOriginal()
            val isVideoOnly = !data.isVideo || simultaneousSelection
            if (isVideoOnly) {
                vSelect?.setBackgroundResource(R.drawable.bg_choose_local_media)
                val index = DataStore.indexOfSelected(data.path)
                vSelect?.text = if (index >= 0) "${(index + 1)}" else ""
            } else {
                vSelect?.setBackgroundResource(R.drawable.bg_choose_local_video)
            }
            setCompletedText(isVideoOnly)
        }
    }

    private fun setCompletedText(isVideoOnly: Boolean) {
        val curSelectedCount = DataStore.curSelectedCount()
        val firstIsVideo = getPointIndexItem(DataStore.getCurSelectedData(), 0)?.isVideo ?: false
        if (isVideoOnly && curSelectedCount == 1 && firstIsVideo) {
            vComplete?.text = getString(R.string.pg_str_send)
        } else {
            val s = if (curSelectedCount <= 0) "" else "($curSelectedCount)"
            vComplete?.text = getString(R.string.pg_str_send).plus(s)
        }
        setViewsEnable(true)
    }

    private fun setViewsEnable(isEnable: Boolean) {
        setSelectedEnable(isEnable)
        setCompleteEnable(isEnable)
        setOriginalEnable(isEnable)
    }

    private fun setSelectedEnable(isEnable: Boolean) {
        vSelect?.let {
            if (it.isEnabled != isEnable) it.isEnabled = isEnable
        }
    }

    private fun setCompleteEnable(isEnable: Boolean) {
        vComplete?.let {
            val enabled = isEnable && DataStore.curSelectedCount() > 0
            if (it.isEnabled != enabled) it.isEnabled = enabled
        }
    }

    private fun setOriginalEnable(isEnable: Boolean) {
        cbOriginal?.let {
            if (it.isEnabled != isEnable) it.isEnabled = isEnable
        }

    }

    private fun initDataWithPagerSelected(v: FrameLayout) {
        curFileData?.let { data ->
            if (data.isVideo) {
                cbOriginal?.visibility = View.GONE
                videoToolBar?.visibility = View.VISIBLE
                VideoController.setData(v, seekBar, tvStart, tvEnd, curFileData)
            } else {
                videoToolBar?.visibility = View.GONE
                cbOriginal?.visibility = View.VISIBLE
            }
        }
    }

    private var anim: PreviewFullValueAnimator? = null
        get() {
            if (field == null) field = PreviewFullValueAnimator(fullListener)
            field?.duration = Constance.ANIMATE_DURATION
            return field
        }

    private val fullListener = object : PreviewFullValueAnimator.FullAnimatorListener {

        override fun onDurationChange(animation: ValueAnimator, duration: Float, isFull: Boolean) {
            val toolsTopHeight = (llTopBar?.measuredHeight ?: 0) * 1.0f
            val toolsBottomHeight = (llBottomBar?.measuredHeight ?: 0) * 1.0f
            val d = if (isFull) duration else -duration
            val topTrans = d * toolsTopHeight
            val bottomTrans = d * toolsBottomHeight
            var tTranslateY = llTopBar?.translationY ?: 0f
            var bTranslateY = llBottomBar?.translationY ?: 0f
            tTranslateY += topTrans
            bTranslateY -= bottomTrans
            llTopBar?.translationY = tTranslateY
            llBottomBar?.translationY = bTranslateY
        }

        override fun onAnimEnd(animation: Animator, isFull: Boolean) {
            val toolsTopHeight = (llTopBar?.measuredHeight ?: 0) * 1.0f
            val toolsBottomHeight = (llBottomBar?.measuredHeight ?: 0) * 1.0f
            val d = if (isFull) 0f else 1f
            val tTranslateY = toolsTopHeight * d
            val bTranslateY = toolsBottomHeight * d
            llTopBar?.translationY = -tTranslateY
            llBottomBar?.translationY = bTranslateY
        }
    }

    private fun full() {
        if (anim?.isRunning == true) return
        llTopBar?.clearAnimation()
        llBottomBar?.clearAnimation()
        anim?.start(isFull)
        isFull = !isFull
    }

    private fun completedAndFinish() {
        if (DataStore.curSelectedCount() > 0) {
            setResult(Constance.RESULT_PREVIEW)
            finish()
        }
    }

    override fun onDestroy() {
        VideoController.release()
        super.onDestroy()
    }
}