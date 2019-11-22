package com.zj.album.ui.photograph

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.zj.album.R
import com.zj.album.nHelpers.DataProxy
import com.zj.album.nHelpers.DataStore
import com.zj.album.nModule.FileInfo
import com.zj.album.nModule.OptionInfo
import com.zj.album.nutils.*
import com.zj.album.options.AlbumConfig
import com.zj.album.ui.base.BaseActivity
import com.zj.album.ui.base.list.listeners.ItemClickListener
import com.zj.album.ui.folders.FolderActivity
import com.zj.album.ui.preview.PreviewActivity
import com.zj.album.ui.views.BaseLoadingView
import java.lang.NullPointerException
import java.util.ArrayList

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
        intent.getBundleExtra(Constance.REQUEST_BODY)?.let {
            val optionInfo = it.getSerializable(Constance.I_OPTIONS_INFO) as? OptionInfo ?: throw NullPointerException("options not found in bundle")
            AlbumConfig.setOptions(this.application, optionInfo)
        }
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
            loadingView?.setMode(BaseLoadingView.DisplayMode.LOADING)
            AlbumConfig.loadData()
        }
        vPreview?.setOnClickListener {
            DataStore.getCurSelectedData().firstOrNull()?.let {
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
            isQueryTaskRunning -> loadingView?.setMode(BaseLoadingView.DisplayMode.LOADING)
            data.isNullOrEmpty() -> loadingView?.setMode(BaseLoadingView.DisplayMode.NO_DATA)
            else -> {
                tvTitle?.text = DataStore.getCurData()?.parentName
                photoGraphAdapter?.change(data)
                loadingView?.hideDelay(500)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constance.REQUEST_OPEN_PREVIEW && resultCode == Constance.RESULT_PREVIEW) {
            doneAndFinish()
        }
    }

    private fun doneAndFinish() {
        val result = ArrayList(DataStore.getCurSelectedData())
        intent.putExtra(Constance.RESULT_BODY, result)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun finish() {
        DataProxy.clear()
        AlbumConfig.clear()
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        GlideCacheUtil.clearImageAllCache(this)
    }
}
