package com.zj.album.graphy.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.util.Log
import android.view.View
import android.widget.TextView

import com.zj.album.MimeType
import com.zj.album.R
import com.zj.album.graphy.PhotoFileHelper
import com.zj.album.graphy.PhotoTemporaryCache
import com.zj.album.graphy.PhotographHelper
import com.zj.album.graphy.adapter.PhotoGraphAdapter
import com.zj.album.graphy.module.LocalMedia
import com.zj.album.graphy.module.PhotoFileInfo
import com.zj.album.interfaces.PhotoEvent
import com.zj.album.nutils.Constance
import com.zj.album.nutils.Constance.REQUEST_VIDEO_PREVIEW
import com.zj.album.utils.ToastUtils
import com.zj.album.nutils.getValueBySafe

import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by ZJJ on 2019/10/16.
 */

class PhotoGraphActivity : AppCompatActivity() {

    private var hasEventData = false
    private var findType: Int = 0
    private var context: WeakReference<Context>? = null

    private var tvFile: TextView? = null
    private var tvPreview: TextView? = null
    private var tvCount: TextView? = null
    private var tvSend: TextView? = null
    private var adapter: PhotoGraphAdapter? = null
//    private var event: PhotoEvent? = PhotoEvent { code, isValidate -> onPhotoEvent(code, isValidate) }

    private var isSaveChooseWhenExit = false

    private val selectedCount: Int
        get() = PhotographHelper.getHelper().curSelectedSize()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photograph)
        getBundleData()
        this.context = WeakReference(this)
        initView()
        initData()
//        initListener()
    }

    /**activity start code*/
    private var requestCode: Int = -1
    /**selected uris*/
    /**the max  selected size*/
    private var maxSelectSize: Int = Int.MAX_VALUE
    /**is use original data default*/
    private var useOriginDefault: Boolean = false

    private fun getBundleData() {
        val bundle = intent.extras ?: return
        try {
            maxSelectSize = bundle.getValueBySafe(Constance.MAX_SELECT_COUNT, 0)
            requestCode = bundle.getValueBySafe(Constance.REQUEST_CODE, -1)
            useOriginDefault = bundle.getValueBySafe(Constance.USE_ORIGINAL_DEFAULT, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initView() {
        val rvPhoto = findViewById<RecyclerView>(R.id.photo_rv)
        tvCount = findViewById(R.id.photo_count)
        tvSend = findViewById(R.id.tv_send)
        tvFile = findViewById(R.id.photo_tvFile)
        tvPreview = findViewById(R.id.tv_preview)
        findViewById<View>(R.id.photo_cancel).setOnClickListener { finish() }
        tvSend!!.setOnClickListener {
            if (selectedCount > 0) {
                isSaveChooseWhenExit = true
                saveAndFinish(true)
            } else {
                ToastUtils.show(applicationContext, getString(R.string.pg_str_please_select_at_least_one_image))
            }
        }
        adapter = PhotoGraphAdapter(object : PhotoGraphAdapter.ChangeListener {
            override fun onSelectChange(position: Int, isSelected: Boolean, uri: String) {
                //todo
//                PhotographHelper.getHelper().onSelectedChanged(isSelected, uri, event)
                for (i in PhotographHelper.getHelper().rankModules) {
                    if (i != position) adapter?.notifyItemChanged(i)
                }
                adapter?.notifyItemChanged(position)
            }

            override fun canSelected(curNum: Int): Boolean {
                return curNum < maxSelectSize
            }

            override fun onFailedSelected() {
                ToastUtils.show(this@PhotoGraphActivity, getString(R.string.pg_str_at_best, maxSelectSize))
            }

            override fun onImgClick(isSelected: Boolean, media: LocalMedia) {
                if (media.isVideo) {
                    if (PhotographHelper.getHelper().curSelectedSize() == 0) {
                        VideoPreviewActivity.start(this@PhotoGraphActivity, media.uri, REQUEST_VIDEO_PREVIEW)
                    } else {
                        ToastUtils.show(this@PhotoGraphActivity, getString(R.string.pg_str_images_choose_tip_message))
                    }
                } else {
                    //打开所有图片预览
//                    startPreviewActivity(isSelected, true, media.uri)
                }
            }
        })
        rvPhoto.adapter = adapter
        val animator = rvPhoto.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    private fun initData() {
//
//        mediaLoaderTask = MediaLoaderTask(findType, object : MediaLoaderTask.MediaLoaderInterface {
//            override fun onSuccess(infos: ArrayList<PhotoFileInfo>) {
//                PhotoFileHelper.getInstance().setFileInfos(infos, event)
//            }
//
//            override fun onFailure() {
//                if (adapter!!.count > 0) adapter!!.clear()
//                Log.e("photo", getString(R.string.pg_str_the_album_failed_to_read))
//            }
//        })
//        mediaLoaderTask!!.execute(applicationContext)
    }
//
//    private fun initListener() {
//        tvFile!!.setOnClickListener { startFolderActivity() }
//        tvPreview!!.setOnClickListener {
//            startPreviewActivity(
//                true,
//                false,
//                PhotographHelper.getHelper().curSelectedPhotos[0].uri
//            )
//        }
//    }

//    private fun onPhotoEvent(code: Int, isValidate: Boolean) {
//        if (isValidate)
//            when (code) {
//                Constancs.HelperEvenErroCode -> ToastUtils.show(
//                    applicationContext,
//                    getString(R.string.pg_str_no_picture)
//                )
//                Constancs.HelperEventCode -> {
//                    hasEventData = true
//                    setData()
//                }
//                Constancs.HelperEventCode_SelectedChange -> setDisplayView()
//                else -> {
//                }
//            }
//    }

//    /**
//     * 设置显示方式，预览／完成等
//     */
//    private fun setDisplayView() {
//        val size = selectedCount
//        if (size == 0) {
//            tvCount!!.visibility = View.GONE
//            tvSend!!.isEnabled = false
//            tvSend!!.setTextColor(resources.getColor(R.color.greyish, theme))
//            tvPreview!!.isEnabled = false
//            tvPreview!!.setTextColor(resources.getColor(R.color.greyish, theme))
//        } else {
//            tvCount!!.text = size.toString()
//            tvCount!!.visibility = View.VISIBLE
//            tvSend!!.isEnabled = true
//            tvSend!!.setTextColor(resources.getColor(R.color.darkBlueGrey, theme))
//            tvPreview!!.isEnabled = true
//            tvPreview!!.setTextColor(resources.getColor(R.color.darkBlueGrey, theme))
//        }
//    }

    /**
     * 设置图片数据
     */
//    private fun setData() {
//        hasEventData = false
//        val infos = PhotographHelper.getHelper().allPhotos
//        if (infos == null || infos.size <= 0) return
//        setDisplayView()
//        adapter!!.clear()
//        adapter!!.add(infos)
//    }

    override fun onResume() {
        super.onResume()
//        if (hasEventData)
//            setData()
//        else {
//            setDisplayView()
//            adapter!!.notifyDataSetChanged()
//        }
    }

    private fun saveAndFinish(postDefault: Boolean) {
        if (isSaveChooseWhenExit) PhotographHelper.getHelper().saveSelectedPhotos()
        isSaveChooseWhenExit = false
        if (postDefault) {
            val intent = Intent()
            intent.putExtra("fullSize", true)
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }

//    override fun onDestroy() {
//        PhotographHelper.getHelper().clearSelectedCache()
//        PhotoFileHelper.getInstance().release()
//        adapter!!.clear()
//        adapter = null
//        mediaLoaderTask!!.cancel(true)
//        mediaLoaderTask = null
//        event = null
//        context!!.clear()
//        context = null
//        super.onDestroy()
//    }

//    private fun startPreviewActivity(isSelected: Boolean, isAll: Boolean, uri: String) {
//        val intent = Intent(context!!.get(), PhotoPreviewActivity::class.java)
//        intent.putExtra("curImgUri", uri)
//        intent.putExtra("isSelected", isSelected)
//        intent.putExtra("maxPhotoSize", maxPhotoSize)
//        intent.putExtra("isAll", isAll)
//        startActivityForResult(intent, REQUEST_OPEN_PREVIEW)
//    }

//    private fun startFolderActivity() {
//        startActivityForResult(Intent(this@PhotoGraphActivity, FolderActivity::class.java), REQUEST_OPEN_FOLDER)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            when (requestCode) {
//                REQUEST_OPEN_PREVIEW -> {
//                    isSaveChooseWhenExit = true
//                    setResult(Activity.RESULT_OK, data)
//                    saveAndFinish(false)
//                }
//                REQUEST_OPEN_FOLDER -> {
//                    val code = data!!.getIntExtra("code", -1)
//                    val isValidate = data.getBooleanExtra("isValidate", false)
//                    onPhotoEvent(code, isValidate)
//                }
//                REQUEST_VIDEO_PREVIEW -> {
//                    PhotoTemporaryCache.removeChoosePhotos(cacheNameCode)
//                    PhotoTemporaryCache.saveAPhoto(cacheNameCode, data!!.getStringExtra("uri")!!)
//                    finish()
//                }
//            }
//        }
//    }

    companion object {

        val IMAGES_AND_VIDEOS = 0
        val IMAGES = 1
        val VIDEOS = 2

        fun start(context: Context, maxPhotoSize: Int, cacheNameCode: String, findType: Int) {
            val intent = Intent(context, PhotoGraphActivity::class.java)
            intent.putExtra("max", maxPhotoSize)
            intent.putExtra("cacheNameCode", cacheNameCode)
            intent.putExtra("findType", findType)
            context.startActivity(intent)
        }
    }
}
