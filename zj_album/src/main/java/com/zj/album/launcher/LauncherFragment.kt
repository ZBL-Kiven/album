package com.zj.album.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.zj.album.nModule.FileInfo
import com.zj.album.nutils.Constance
import com.zj.album.ui.photograph.PhotoGraphActivity

internal class LauncherFragment : Fragment() {

    private var requestId: Int = 0
    private var mRequestBody: Bundle? = null
    private var mListener: ((isCancel: Boolean, data: List<FileInfo>?) -> Unit)? = null

    companion object {
        fun launch(requestId: Int, requestBody: Bundle, listener: ((isCancel: Boolean, data: List<FileInfo>?) -> Unit)? = null): LauncherFragment {
            return LauncherFragment().apply {
                this.requestId = requestId
                this.mRequestBody = requestBody
                this.mListener = listener
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        val intent = Intent(this.context, PhotoGraphActivity::class.java)
        intent.putExtra(Constance.REQUEST_BODY, mRequestBody)
        startActivityForResult(intent, requestId)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == requestId) {
            val files = data?.getSerializableExtra(Constance.RESULT_BODY)
            (files as? List<FileInfo>)?.let {
                mListener?.invoke(true, it)
                return
            }
        }
        mListener?.invoke(false, null)
        remove()
    }

    private fun remove() {
        val fragmentManager = if (parentFragment == null && activity != null) {
            activity?.supportFragmentManager
        } else {
            parentFragment?.childFragmentManager
        }
        fragmentManager?.beginTransaction()?.remove(this)?.commitNow()
    }
}
