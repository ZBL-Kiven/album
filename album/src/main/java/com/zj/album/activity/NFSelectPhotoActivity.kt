package com.zj.album.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.util.Log
import android.view.View
import com.zj.album.R
import com.zj.album.entity.SelectionSpec
import com.zj.album.graphy.MediaLoaderTask
import com.zj.album.graphy.PhotoFileHelper
import com.zj.album.graphy.PhotoTemporaryCache
import com.zj.album.graphy.PhotographHelper
import com.zj.album.graphy.activity.FolderActivity
import com.zj.album.graphy.activity.PhotoPreviewActivity
import com.zj.album.graphy.activity.VideoPreviewActivity
import com.zj.album.graphy.adapter.PhotoGraphAdapter
import com.zj.album.graphy.module.LocalMedia
import com.zj.album.graphy.module.PhotoFileInfo
import com.zj.album.interfaces.PhotoEvent
import com.zj.album.services.Constancs
import com.zj.album.utils.ToastUtils
import com.zj.album.utils.Utils
import kotlinx.android.synthetic.main.activity_nf_select.*
import java.lang.ref.WeakReference

class NFSelectPhotoActivity : AppCompatActivity() {

    private val REQUST_OPEN_PREVIEW = 0x21
    private val REQUST_OPEN_FOLDER = 0x22
    private val REQUEST_VIDEO_PREVIEW = 200
    private val REQUEST_CUSTOMER = 300
    private var cacheNameCode: String? = null
    private var maxPhotoSize: Int = 1
    private var hasEventData = false
    private var findType: Int = 0
    private var context: WeakReference<Context>? = null
    private lateinit var adapter: PhotoGraphAdapter
    private var mediaLoaderTask: MediaLoaderTask? = null
    private var event: PhotoEvent? = PhotoEvent { code, isValidate -> onPhotoEvent(code, isValidate) }

    private var isSaveChooseWhenExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nf_select)
        Utils.context = this.applicationContext
        getBundleData()
        context = WeakReference(this)
        initView()
        initData()
    }

    private fun getBundleData() {
        val bundle = intent.extras
        cacheNameCode = bundle.getString("cacheNameCode", "defaultCache")
        findType = bundle.getInt("findType", 0)
        PhotographHelper.init(cacheNameCode)
    }

    private fun initView() {
        val rvPhoto = findViewById<RecyclerView>(R.id.photo_rv)
        photo_cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        photo_camera.setOnClickListener {
            //            startFolderActivity()
            SelectionSpec.onOpenCameraClick?.let {
                it.invoke(this, REQUEST_CUSTOMER)
            }
        }
        tv_preview.setOnClickListener {
            startPreviewActivity(isSelected = true, isAll = false, uri = PhotographHelper.getHelper().curSelectedPhotos[0].uri)
        }
        tv_send.setOnClickListener {
            if (getSelectedCount() > 0) {
                isSaveChooseWhenExit = true
                setResult(Activity.RESULT_OK)
                saveAndFinish()
            } else {
                ToastUtils.show(applicationContext, getString(R.string.im_please_select_at_least_one_image))
            }
        }
        adapter = PhotoGraphAdapter(object : PhotoGraphAdapter.ChangeListener {
            override fun onSelectChange(position: Int, isSelected: Boolean, uri: String) {
                PhotographHelper.getHelper().onSelectedChanged(isSelected, uri, event)
                for (i in PhotographHelper.getHelper().rankModules) {
                    if (i != position) adapter.notifyItemChanged(i)
                }
                adapter.notifyItemChanged(position)
            }

            override fun canSelected(curNum: Int): Boolean {
                return curNum < maxPhotoSize
            }

            override fun onFailedSelected() {
                ToastUtils.show(this@NFSelectPhotoActivity, getString(R.string.im_at_best, "" + maxPhotoSize))
            }

            override fun onImgClick(isSelected: Boolean, media: LocalMedia) {
                if (media.isVideo) {
                    if (PhotographHelper.getHelper().curSelectedSize() == 0) {
                        if (media.duration > SelectionSpec.maxVideoDuration) {
                            ToastUtils.show(this@NFSelectPhotoActivity, getString(R.string.nf_video_max_time))
                            return
                        }
                        VideoPreviewActivity.start(this@NFSelectPhotoActivity, media.uri, REQUEST_VIDEO_PREVIEW)
                    } else {
                        ToastUtils.show(this@NFSelectPhotoActivity, getString(R.string.im_images_choose_tip_message))
                    }
                } else {
                    //打开所有图片预览
                    startPreviewActivity(isSelected, true, media.uri)
                }
            }
        })
        rvPhoto.adapter = adapter
        (rvPhoto.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun initData() {
        maxPhotoSize = SelectionSpec.maxSelectable
        mediaLoaderTask = MediaLoaderTask(findType, object : MediaLoaderTask.MediaLoaderInterface {
            override fun onSuccess(photoFileInfos: ArrayList<PhotoFileInfo>) {
                PhotoFileHelper.getInstance().setFileInfos(photoFileInfos, event)
            }

            override fun onFailure() {
                if (adapter.count > 0) adapter.clear()
                Log.e("photo", getString(com.zj.album.R.string.im_the_album_failed_to_read))
            }
        })
        mediaLoaderTask?.execute(applicationContext)
    }

    private fun onPhotoEvent(code: Int, isValidate: Boolean) {
        if (isValidate)
            when (code) {
                Constancs.HelperEvenErroCode -> ToastUtils.show(applicationContext, getString(com.zj.album.R.string.im_no_picture))
                Constancs.HelperEventCode -> {
                    hasEventData = true
                    setData()
                }
                Constancs.HelperEventCode_SelectedChange -> setDisplayView()
                else -> {
                }
            }
    }

    /**
     * 设置显示方式，预览／完成等
     */
    private fun setDisplayView() {
        val size = getSelectedCount()
        if (size == 0) {
            photo_count.visibility = View.GONE
            tv_send.isEnabled = false
            tv_send.setTextColor(resources.getColor(R.color.greyish))
            tv_preview.isEnabled = false
            tv_preview.setTextColor(resources.getColor(R.color.greyish))
        } else {
            photo_count.text = size.toString()
            photo_count.visibility = View.VISIBLE
            tv_send.isEnabled = true
            tv_send.setTextColor(resources.getColor(R.color.darkBlueGrey))
            tv_preview.isEnabled = true
            tv_preview.setTextColor(resources.getColor(R.color.darkBlueGrey))
        }
    }

    private fun getSelectedCount(): Int {
        return PhotographHelper.getHelper().curSelectedSize()
    }

    /**
     * 设置图片数据
     */
    private fun setData() {
        hasEventData = false
        val infos = PhotographHelper.getHelper().allPhotos
        if (infos == null || infos.size <= 0) return
        setDisplayView()
        adapter.clear()
        adapter.add(infos)
    }

    override fun onResume() {
        super.onResume()
        if (hasEventData)
            setData()
        else {
            setDisplayView()
            adapter.notifyDataSetChanged()
        }
    }

    private fun saveAndFinish() {
        if (isSaveChooseWhenExit) PhotographHelper.getHelper().saveSelectedPhotos()
        isSaveChooseWhenExit = false
        finish()
    }

    override fun onDestroy() {
        PhotographHelper.getHelper().clearSelectedCache()
        PhotoFileHelper.getInstance().release()
        adapter.clear()
        mediaLoaderTask?.cancel(true)
        mediaLoaderTask = null
        event = null
        context?.clear()
        context = null
        super.onDestroy()
    }

    private fun startPreviewActivity(isSelected: Boolean, isAll: Boolean, uri: String) {
        val intent = Intent(this, PhotoPreviewActivity::class.java)
        intent.putExtra("curImgUri", uri)
        intent.putExtra("isSelected", isSelected)
        intent.putExtra("maxPhotoSize", maxPhotoSize)
        intent.putExtra("isAll", isAll)
        startActivityForResult(intent, REQUST_OPEN_PREVIEW)
    }

    /**
     * 跳转文件夹页面
     */
    private fun startFolderActivity() {
        startActivityForResult(Intent(this, FolderActivity::class.java), REQUST_OPEN_FOLDER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUST_OPEN_PREVIEW -> {
                    isSaveChooseWhenExit = true
                    setResult(Activity.RESULT_OK, data)
                    saveAndFinish()
                }
                REQUST_OPEN_FOLDER -> {
                    if (data == null) return
                    val code = data.getIntExtra("code", -1)
                    val isValidate = data.getBooleanExtra("isValidate", false)
                    onPhotoEvent(code, isValidate)
                }
                REQUEST_VIDEO_PREVIEW -> {
                    PhotoTemporaryCache.removeChoosePhotos(cacheNameCode)
                    PhotoTemporaryCache.saveAPhoto(cacheNameCode, data?.getStringExtra("uri")
                            ?: return)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                REQUEST_CUSTOMER -> { // 用户自定义请求
                    SelectionSpec.onResult?.invoke(data)
                    finish()
                }
            }
        } else {
            setResult(resultCode)
        }
    }
}