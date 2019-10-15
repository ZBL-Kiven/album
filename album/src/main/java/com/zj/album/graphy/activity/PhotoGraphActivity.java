package com.zj.album.graphy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zj.album.MimeType;
import com.zj.album.R;
import com.zj.album.entity.SelectionSpec;
import com.zj.album.graphy.MediaLoaderTask;
import com.zj.album.graphy.PhotoFileHelper;
import com.zj.album.graphy.PhotoTemporaryCache;
import com.zj.album.graphy.PhotographHelper;
import com.zj.album.graphy.adapter.PhotoGraphAdapter;
import com.zj.album.graphy.module.LocalMedia;
import com.zj.album.graphy.module.PhotoFileInfo;
import com.zj.album.interfaces.PhotoEvent;
import com.zj.album.services.Constancs;
import com.zj.album.utils.ToastUtils;
import com.zj.album.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaojie on 2017/10/18.
 * <p>
 * the Intent data:
 * <p>
 * photoSelectedComplete :when you photo_select completed,you can use this code to notify getData on your activityResult;
 * max :the max num you can selected;
 * cacheNameCode :the cache name to save for this operate,as a file;
 * <p>
 * <example>
 * <p>
 * var intent = Intent(this, PhotoGraphActivity::class.java);
 * intent.putExtra("photoSelectedComplete", "")
 * intent.putExtra("max", "")
 * intent.putExtra("cacheNameCode", "")
 * startActivity(intent)
 * to get data:
 * <p>
 * 全部图片展示列表页面
 */

public class PhotoGraphActivity extends AppCompatActivity {

    private final int REQUST_OPEN_PREVIEW = 0x21;
    private final int REQUST_OPEN_FOLDER = 0x22;
    private final int REQUEST_VIDEO_PREVIEW = 200;

    public static final int IMAGES_AND_VIDEOS = 0;
    public static final int IMAGES = 1;
    public static final int VIDEOS = 2;

    public static void start(Context context, int maxPhotoSize, String cacheNameCode, int findType) {
        Intent intent = new Intent(context, PhotoGraphActivity.class);
        intent.putExtra("max", maxPhotoSize);
        intent.putExtra("cacheNameCode", cacheNameCode);
        intent.putExtra("findType", findType);
        context.startActivity(intent);
    }

    private String cacheNameCode;
    private int maxPhotoSize = 0;
    private boolean hasEventData = false;
    private int findType;
    private WeakReference<Context> context;

    private TextView tvFile, tvPreview, tvCount, tvSend;
    private PhotoGraphAdapter adapter;
    private MediaLoaderTask mediaLoaderTask;
    private PhotoEvent event = new PhotoEvent() {
        @Override
        public void onEvent(int code, boolean isValidate) {
            onPhotoEvent(code, isValidate);
        }
    };

    private boolean isSaveChooseWhenExit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photograph);
        Utils.context = this.getApplicationContext();
        getBundleData();
        this.context = new WeakReference<Context>(this);
        initView();
        initData();
        initListener();
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        try {
            maxPhotoSize = bundle.getInt("max");
            cacheNameCode = bundle.getString("cacheNameCode", "defaultCache");
            findType = bundle.getInt("findType", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PhotographHelper.init(cacheNameCode);
    }

    private void initView() {
        RecyclerView rvPhoto = findViewById(R.id.photo_rv);
        tvCount = findViewById(R.id.photo_count);
        tvSend = findViewById(R.id.tv_send);
        tvFile = findViewById(R.id.photo_tvFile);
        tvPreview = findViewById(R.id.tv_preview);
        findViewById(R.id.photo_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSelectedCount() > 0) {
                    isSaveChooseWhenExit = true;
                    saveAndFinish(true);
                } else {
                    ToastUtils.show(getApplicationContext(), getString(R.string.im_please_select_at_least_one_image));
                }
            }
        });
        adapter = new PhotoGraphAdapter(new PhotoGraphAdapter.ChangeListener() {
            @Override
            public void onSelectChange(int position, boolean isSelected, String uri) {
                PhotographHelper.getHelper().onSelectedChanged(isSelected, uri, event);
                for (int i : PhotographHelper.getHelper().getRankModules()) {
                    if (i != position) adapter.notifyItemChanged(i);
                }
                adapter.notifyItemChanged(position);
            }

            @Override
            public boolean canSelected(int curNum) {
                return curNum < maxPhotoSize;
            }

            @Override
            public void onFailedSelected() {
                ToastUtils.show(PhotoGraphActivity.this, getString(R.string.im_at_best, "" + maxPhotoSize));
            }

            @Override
            public void onImgClick(boolean isSelected, LocalMedia media) {
                if (media.isVideo()) {
                    if (PhotographHelper.getHelper().curSelectedSize() == 0) {
                        VideoPreviewActivity.start(PhotoGraphActivity.this, media.uri, REQUEST_VIDEO_PREVIEW);
                    } else {
                        ToastUtils.show(PhotoGraphActivity.this, getString(R.string.im_images_choose_tip_message));
                    }
                } else {
                    //打开所有图片预览
                    startPreviewActivity(isSelected, true, media.uri);
                }
            }
        });
        rvPhoto.setAdapter(adapter);
        ((SimpleItemAnimator) rvPhoto.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private void initData() {
        SelectionSpec.INSTANCE.setMimeTypes(MimeType.Companion.ofAll());
        mediaLoaderTask = new MediaLoaderTask(findType, new MediaLoaderTask.MediaLoaderInterface() {
            @Override
            public void onSuccess(@NonNull ArrayList<PhotoFileInfo> infos) {
                PhotoFileHelper.getInstance().setFileInfos(infos, event);
            }

            @Override
            public void onFailure() {
                if (adapter.getCount() > 0) adapter.clear();
                Log.e("photo", getString(R.string.im_the_album_failed_to_read));
            }
        });
        mediaLoaderTask.execute(getApplicationContext());
    }

    private void initListener() {
        tvFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFolderActivity();
            }
        });
        tvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPreviewActivity(true, false, PhotographHelper.getHelper().getCurSelectedPhotos().get(0).uri);
            }
        });
    }

    private void onPhotoEvent(int code, boolean isValidate) {
        if (isValidate)
            switch (code) {
                case Constancs.HelperEvenErroCode:
                    ToastUtils.show(getApplicationContext(), getString(R.string.im_no_picture));
                    break;
                case Constancs.HelperEventCode:
                    hasEventData = true;
                    setData();
                    break;
                case Constancs.HelperEventCode_SelectedChange:
                    setDisplayView();
                    break;
                default:
                    break;
            }
    }

    /**
     * 设置显示方式，预览／完成等
     */
    private void setDisplayView() {
        int size = getSelectedCount();
        if (size == 0) {
            tvCount.setVisibility(View.GONE);
            tvSend.setEnabled(false);
            tvSend.setTextColor(getResources().getColor(R.color.greyish));
            tvPreview.setEnabled(false);
            tvPreview.setTextColor(getResources().getColor(R.color.greyish));
        } else {
            tvCount.setText(String.valueOf(size));
            tvCount.setVisibility(View.VISIBLE);
            tvSend.setEnabled(true);
            tvSend.setTextColor(getResources().getColor(R.color.darkBlueGrey));
            tvPreview.setEnabled(true);
            tvPreview.setTextColor(getResources().getColor(R.color.darkBlueGrey));
        }
    }

    private int getSelectedCount() {
        return PhotographHelper.getHelper().curSelectedSize();
    }

    /**
     * 设置图片数据
     */
    private void setData() {
        hasEventData = false;
        List<LocalMedia> infos = PhotographHelper.getHelper().getAllPhotos();
        if (infos == null || infos.size() <= 0) return;
        setDisplayView();
        adapter.clear();
        adapter.add(infos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasEventData)
            setData();
        else {
            setDisplayView();
            adapter.notifyDataSetChanged();
        }
    }

    private void saveAndFinish(boolean postDefault) {
        if (isSaveChooseWhenExit) PhotographHelper.getHelper().saveSelectedPhotos();
        isSaveChooseWhenExit = false;
        if (postDefault) {
            Intent intent = new Intent();
            intent.putExtra("fullSize", true);
            setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        PhotographHelper.getHelper().clearSelectedCache();
        PhotoFileHelper.getInstance().release();
        adapter.clear();
        adapter = null;
        mediaLoaderTask.cancel(true);
        mediaLoaderTask = null;
        event = null;
        context.clear();
        context = null;
        super.onDestroy();
    }

    private void startPreviewActivity(boolean isSelected, boolean isAll, String uri) {
        Intent intent = new Intent(context.get(), PhotoPreviewActivity.class);
        intent.putExtra("curImgUri", uri);
        intent.putExtra("isSelected", isSelected);
        intent.putExtra("maxPhotoSize", maxPhotoSize);
        intent.putExtra("isAll", isAll);
        startActivityForResult(intent, REQUST_OPEN_PREVIEW);
    }

    private void startFolderActivity() {
        startActivityForResult(new Intent(PhotoGraphActivity.this, FolderActivity.class), REQUST_OPEN_FOLDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUST_OPEN_PREVIEW:
                    isSaveChooseWhenExit = true;
                    setResult(RESULT_OK, data);
                    saveAndFinish(false);
                    break;
                case REQUST_OPEN_FOLDER:
                    int code = data.getIntExtra("code", -1);
                    boolean isValidate = data.getBooleanExtra("isValidate", false);
                    onPhotoEvent(code, isValidate);
                    break;
                case REQUEST_VIDEO_PREVIEW:
                    PhotoTemporaryCache.removeChoosePhotos(cacheNameCode);
                    PhotoTemporaryCache.saveAPhoto(cacheNameCode, data.getStringExtra("uri"));
                    finish();
                    break;
            }
        }
    }
}
