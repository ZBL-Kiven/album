package com.zj.album.ui.photograph

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.zj.album.PhotoAlbum
import com.zj.album.R
import com.zj.album.nHelpers.DataProxy
import com.zj.album.nHelpers.DataStore
import com.zj.album.nModule.FolderInfo
import com.zj.album.ui.base.BaseActivity
import com.zj.album.nutils.Constance
import com.zj.album.ui.folders.FolderActivity

internal class PhotoGraphActivity : BaseActivity() {

    override fun getContentView(): Int {
        return R.layout.graph_activity_photograph
    }

    private var curDisplayFolder: FolderInfo? = null

    private var tvTitle: TextView? = null
    private var tvFile: TextView? = null
    private var rvGraph: RecyclerView? = null
    private var vBack: View? = null
    private var vPreview: View? = null
    private var vOk: View? = null
    private var cbOriginal: CheckBox? = null


    override fun initView() {
        tvTitle = findViewById(R.id.photo_tv_tittle)
        tvFile = findViewById(R.id.photo_tv_file)
        rvGraph = findViewById(R.id.photo_rv)
        vBack = findViewById(R.id.photo_tv_cancel)
        vPreview = findViewById(R.id.photo_tv_preview)
        vOk = findViewById(R.id.photo_tv_done)
        cbOriginal = findViewById(R.id.photo_cb_original)
    }

    override fun initData() {
        val data = DataStore.getCurData()
        tvTitle?.text = data?.parentName
    }

    override fun initListener() {
        vBack?.setOnClickListener {
            finish()
        }
        tvFile?.setOnClickListener {
            startActivityForResult(Intent(this, FolderActivity::class.java), Constance.REQUEST_OPEN_FOLDER)
        }
        vOk?.setOnClickListener {
            doneAndFinish()
        }
        cbOriginal?.setOnClickListener {
            PhotoAlbum.useOriginDefault = cbOriginal?.isChecked ?: false
        }
    }

    private fun setData(folder: FolderInfo?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constance.REQUEST_OPEN_FOLDER -> {
                curDisplayFolder?.let {
                    if (DataStore.isCurDisplayFolder(it.id)) {
                        setData(DataStore.getCurData())
                    }
                }
            }
            Constance.REQUEST_OPEN_PREVIEW -> {

            }
            Constance.REQUEST_VIDEO_PREVIEW -> {

            }
        }
    }

    private fun doneAndFinish() {
        val i = Intent()
        i.putExtra("data", DataStore.getCurSelectedData())
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    override fun finish() {
        DataProxy.clear()
        super.finish()
    }

}
