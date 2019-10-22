package com.zj.album.ui.photograph

import android.annotation.SuppressLint
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
import com.zj.album.nModule.FileInfo
import com.zj.album.ui.base.BaseActivity
import com.zj.album.ui.folders.FolderActivity
import com.zj.album.ui.views.BaseLoadingView

internal class PhotoGraphActivity : BaseActivity() {

    override fun getContentView(): Int {
        return R.layout.graph_activity_photograph
    }

    private var tvTitle: TextView? = null
    private var tvFile: TextView? = null
    private var rvGraph: RecyclerView? = null
    private var vBack: View? = null
    private var vPreview: View? = null
    private var vOk: TextView? = null
    private var cbOriginal: CheckBox? = null
    private var loadingView: BaseLoadingView? = null

    private var photoGraphAdapter: PhotoGraphAdapter? = null

    override fun initView() {
        tvTitle = findViewById(R.id.photo_tv_tittle)
        tvFile = findViewById(R.id.photo_tv_file)
        rvGraph = findViewById(R.id.photo_rv)
        vBack = findViewById(R.id.photo_tv_cancel)
        vPreview = findViewById(R.id.photo_tv_preview)
        vOk = findViewById(R.id.photo_tv_done)
        cbOriginal = findViewById(R.id.photo_cb_original)
        loadingView = findViewById(R.id.photo_loading)
    }

    override fun initData() {
        photoGraphAdapter = PhotoGraphAdapter()
        rvGraph?.adapter = photoGraphAdapter
    }

    override fun initListener() {
        vBack?.setOnClickListener {
            finish()
        }
        tvFile?.setOnClickListener {
            startActivity(Intent(this, FolderActivity::class.java))
        }
        vOk?.setOnClickListener {
            doneAndFinish()
        }
        cbOriginal?.setOnClickListener {
            PhotoAlbum.useOriginDefault = cbOriginal?.isChecked ?: false
        }
    }

    override fun onDataGot(data: List<FileInfo>?, curAccessKey: String) {
        super.onDataGot(data, curAccessKey)
        if (data != null) loadingView?.setMode(
            BaseLoadingView.DisplayMode.normal,
            "", false
        )
        else loadingView?.setMode(
            BaseLoadingView.DisplayMode.noData,
            PhotoAlbum.getString(R.string.loading_no_data),
            false
        )
        tvTitle?.text = DataStore.getCurData()?.parentName
        photoGraphAdapter?.change(data)
    }

    @SuppressLint("SetTextI18n")
    override fun onSelectedChanged(count: Int) {
        super.onSelectedChanged(count)
        vOk?.isEnabled = count > 0
        val s = if (count <= 0) "" else "($count)"
        vOk?.text = "${PhotoAlbum.getString(R.string.pg_str_send)}$s"
    }

    private fun doneAndFinish() {
        val intent = Intent()
        intent.putExtra("data", "")//DataStore.getCurSelectedData()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun finish() {
        DataProxy.clear()
        super.finish()
    }

}
