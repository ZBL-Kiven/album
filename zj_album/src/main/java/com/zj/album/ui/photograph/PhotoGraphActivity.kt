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
import com.zj.album.nModule.FileInfo
import com.zj.album.nutils.Constance
import com.zj.album.ui.base.BaseActivity
import com.zj.album.ui.base.list.listeners.ItemClickListener
import com.zj.album.ui.folders.FolderActivity
import com.zj.album.ui.preview.PreviewActivity
import com.zj.album.ui.views.BaseLoadingView

/**
 * @author ZJJ on 2019.10.24
 * */
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
        photoGraphAdapter = PhotoGraphAdapter {
            cbOriginal?.isChecked ?: false && DataStore.isOriginalInAutoMode() == Constance.ORIGINAL_POLY
        }
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
        cbOriginal?.setOnCheckedChangeListener { _, checked ->
            if (DataStore.isOriginalInAutoMode() == Constance.ORIGINAL_POLY) {
                photoGraphAdapter?.let { it.notifyItemRangeChanged(0, it.itemCount, Constance.NOTIFY_ORIGINAL) }
            } else {
                DataProxy.onOriginalChanged(checked, "")
            }
        }
        loadingView?.setRefreshListener {
            loadingView?.setMode(BaseLoadingView.DisplayMode.loading)
            PhotoAlbum.loadData()
        }
        vPreview?.setOnClickListener {
            DataStore.getCurSelectedData()?.firstOrNull()?.let {
                startPreviewActivity(true, it.path)
            }
        }
        photoGraphAdapter?.setOnItemClickListener(object : ItemClickListener<FileInfo>() {

            override fun onItemClick(position: Int, v: View?, m: FileInfo?) {
                m?.let { data ->
                    startPreviewActivity(false, data.path)
                }
            }
        })
    }

    override fun onDataDispatch(data: List<FileInfo>?, isQueryTaskRunning: Boolean) {
        when {
            isQueryTaskRunning -> loadingView?.setMode(BaseLoadingView.DisplayMode.loading)
            data.isNullOrEmpty() -> loadingView?.setMode(BaseLoadingView.DisplayMode.noData)
            else -> {
                loadingView?.setMode(BaseLoadingView.DisplayMode.normal)
                tvTitle?.text = DataStore.getCurData()?.parentName
                photoGraphAdapter?.change(data)
            }
        }
    }

    override fun onSelectedStateChange(count: Int) {
        vOk?.isEnabled = count > 0
        vPreview?.isEnabled = count > 0
        val s = if (count <= 0) "" else "($count)"
        vOk?.text = getString(R.string.pg_str_send).plus(s)
        cbOriginal?.visibility = if (count <= 0 || DataStore.hasImageSelected()) View.VISIBLE else {
            cbOriginal?.isChecked = false; View.GONE
        }
        photoGraphAdapter?.let {
            it.notifyItemRangeChanged(0, it.itemCount, Constance.NOTIFY_SELECTED)
        }
    }

    override fun onResume() {
        super.onResume()
        val isOriginalInAutoMod = DataStore.isOriginalInAutoMode()
        if (isOriginalInAutoMod != Constance.ORIGINAL_POLY) cbOriginal?.isChecked = isOriginalInAutoMod == Constance.ORIGINAL_AUTO_SET

    }

    private fun startPreviewActivity(isPreviewSelected: Boolean, path: String) {
        PreviewActivity.start(this@PhotoGraphActivity, isPreviewSelected, path)
    }

    private fun doneAndFinish() {
        val intent = Intent()
        intent.putExtra("data", "") //DataStore.getCurSelectedData()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun finish() {
        DataProxy.clear()
        PhotoAlbum.clear()
        super.finish()
    }
}
