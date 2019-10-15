package com.zj.album

import android.app.Activity
import android.content.Intent
import com.zj.album.entity.SelectionSpec
import com.zj.album.graphy.PhotoTemporaryCache
import com.zj.album.graphy.activity.PhotoGraphActivity

class SelectionCreator internal constructor(private val mPhotoAlbum: PhotoAlbum, mimeTypes: Set<MimeType>, mediaTypeExclusive: Boolean) {

    private val mSelectionSpec: SelectionSpec = SelectionSpec

    init {
        mSelectionSpec.reset()
        mSelectionSpec.mimeTypes = mimeTypes
        mSelectionSpec.mediaTypeExclusive = mediaTypeExclusive
        PhotoTemporaryCache.removeChoosePhotos(PhotoAlbum.CATCH_NAME)
    }

    /**
     * 添加打开相机的跳转方法，以配置自定义相机功能
     * [onOpenCameraClick] activity requestCode
     */
    fun onOpenCameraClick(onOpenCameraClick: (Activity, Int) -> Unit, onResult: ((Intent?) -> Unit)? = null): SelectionCreator {
        mSelectionSpec.onOpenCameraClick = onOpenCameraClick
        mSelectionSpec.onResult = onResult
        return this
    }

    /**
     * 这个方法是设置多种类型总共的最大数量，此方法和 [maxSelectablePerMediaType] 不可共用
     * [maxSelectable] 最大选择数量
     */
    fun maxSelectable(maxSelectable: Int): SelectionCreator {
        if (maxSelectable < 1)
            throw IllegalArgumentException("maxSelectable must be greater than or equal to one")
        if (mSelectionSpec.maxImageSelectable > 0 || mSelectionSpec.maxVideoSelectable > 0)
            throw IllegalStateException("already set maxImageSelectable and maxVideoSelectable")
        mSelectionSpec.maxSelectable = maxSelectable
        return this
    }

    /**
     * 视频和图片不同种类的最大限制，此方法和 [maxSelectable] 不可共用
     */
    fun maxSelectablePerMediaType(maxImageSelectable: Int, maxVideoSelectable: Int): SelectionCreator {
        if (maxImageSelectable < 1 || maxVideoSelectable < 1)
            throw IllegalArgumentException("max selectable must be greater than or equal to one")
        mSelectionSpec.maxSelectable = -1
        mSelectionSpec.maxImageSelectable = maxImageSelectable
        mSelectionSpec.maxVideoSelectable = maxVideoSelectable
        return this
    }

    /**
     * 最大视频大小限制
     * [size] 0 无限制
     */
    fun maxVideoSize(size: Int): SelectionCreator {
        mSelectionSpec.maxVideoSize = size
        return this
    }

    /**
     * 最大视频长度
     * [duration] 0 无限制
     */
    fun maxVideoDuration(duration: Int): SelectionCreator {
        mSelectionSpec.maxVideoDuration = duration
        return this
    }

    fun forResult(requestCode: Int, cls: Class<*>?) {
        val activity = mPhotoAlbum.getActivity() ?: return
        val intent = Intent(activity, cls ?: PhotoGraphActivity::class.java)
        intent.putExtra("max", mSelectionSpec.maxSelectable)
        intent.putExtra("cacheNameCode", PhotoAlbum.CATCH_NAME)
        val fragment = mPhotoAlbum.getFragment()
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode)
        } else {
            activity.startActivityForResult(intent, requestCode)
        }
    }
}