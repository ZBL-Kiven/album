package com.zj.album

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.zj.album.graphy.PhotoTemporaryCache
import com.zj.album.graphy.module.LocalMedia
import java.lang.ref.WeakReference

/**
 * 相册启动入口
 * @author xxj
 * @date 2019/08/20
 */
class PhotoAlbum(activity: Activity? = null, fragment: Fragment? = null) {

    private val mActivity: WeakReference<Activity?> = WeakReference(activity)
    private val mFragment: WeakReference<Fragment?> = WeakReference(fragment)

    companion object {
        const val CATCH_NAME = "PhotoAlbum"

        @JvmStatic
        fun from(activity: Activity): PhotoAlbum {
            return PhotoAlbum(activity = activity)
        }

        @JvmStatic
        fun from(fragment: Fragment): PhotoAlbum {
            return PhotoAlbum(activity = fragment.activity, fragment = fragment)
        }

        @JvmStatic
        fun obtainPathResult(data: Intent?): List<String> {
            val infos: List<LocalMedia>? = PhotoTemporaryCache.getCacheImages(CATCH_NAME)
            PhotoTemporaryCache.removeChoosePhotos(CATCH_NAME)
            return infos?.map { it.uri } ?: arrayListOf()
        }
    }

    /**
     * [mimeTypes] MIME types set user can choose from.
     * [mediaTypeExclusive] Whether can choose images and videos at the same time during one single choosing
     *                      process. true corresponds to not being able to choose images and videos at the same
     *                      time, and false corresponds to being able to do this.
     */
    fun choose(mimeTypes: Set<MimeType>, mediaTypeExclusive: Boolean = true): SelectionCreator {
        return SelectionCreator(this, mimeTypes, mediaTypeExclusive)
    }

    internal fun getActivity(): Activity? {
        return mActivity.get()
    }

    internal fun getFragment(): Fragment? {
        return mFragment.get()
    }
}