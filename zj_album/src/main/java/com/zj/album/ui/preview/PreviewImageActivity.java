package com.zj.album.ui.preview;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zj.album.R;
import com.zj.album.nHelpers.DataStore;
import com.zj.album.nModule.FileInfo;
import com.zj.album.ui.base.BaseActivity;
import com.zj.album.ui.base.list.listeners.ItemClickListener;
import com.zj.album.ui.preview.adapter.LandscapeAdapter;
import com.zj.album.ui.preview.adapter.PreviewImageAdapter;
import com.zj.album.ui.preview.listener.FullPreviewListener;
import com.zj.album.widget.JViewPager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yangji
 */
public class PreviewImageActivity extends BaseActivity implements FullPreviewListener {

    private JViewPager viewPager;
    private View rlPreviewTop, rlPreviewBottom;
    private TextView tvSelectCount;

    private View dlPreviewFlSelected;
    private TextView dlPreviewTvSelected;

    private boolean mIsFullPreview = false;

    private PreviewImageAdapter previewAdapter;
    private LandscapeAdapter mLandscapeAdapter;

    private ItemClickListener<FileInfo> itemClickListener = new ItemClickListener<FileInfo>() {
        @Override
        public void onItemClick(int position, View v, @Nullable FileInfo m) {
            indexOf(mLandscapeAdapter.getItem(position));
        }
    };

    @Override
    public int getContentView() {
        return R.layout.album_activity_preview;
    }

    @Override
    public void initView() {
        rlPreviewTop = findViewById(R.id.rl_preview_top);
        dlPreviewFlSelected = findViewById(R.id.dl_preview_fl_selected);
        dlPreviewTvSelected = findViewById(R.id.dl_preview_tv_selected);

        rlPreviewBottom = findViewById(R.id.rl_preview_bottom);

        tvSelectCount = findViewById(R.id.tv_select_count);


        viewPager = findViewById(R.id.dl_preview_viewpager);
        viewPager.setLooper(true);
        viewPager.setPageTransformer(true, new ZoomPageTransformer());

        RecyclerView rvSelectImg = findViewById(R.id.rv_select_img);
        //设置横向预览
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        rvSelectImg.setLayoutManager(linearLayoutManager);
        rvSelectImg.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mLandscapeAdapter = new LandscapeAdapter();
        mLandscapeAdapter.setOnItemClickListener(itemClickListener);
        rvSelectImg.setAdapter(mLandscapeAdapter);
    }


    @Override
    public void initListener() {
        viewPager.addOnPageChangeListener(new JViewPager.PageChangeListener() {
            @Override
            public void onPageSelected(int currentItem) {
                previewAdapter.reset(currentItem);
                updateTopSelected(previewAdapter.getItem(currentItem));
            }
        });

        dlPreviewFlSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置选中
                int currentItem = viewPager.getCurrentItem();
                FileInfo info = previewAdapter.getItem(currentItem);
                info.setSelected$zj_album_debug(!info.isSelected$zj_album_debug(), false);
                updateTopSelected(info);
                updateLandscapeAdapter();
//                updateSelectCount();
            }
        });

        findViewById(R.id.rv_select_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        findViewById(R.id.photo_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }


    @Override
    public void initData() {
        updateLandscapeAdapter();
//        updateSelectCount();
    }

    /**
     * 全预览
     */
    private void indexOf(FileInfo info) {
        setCurrentItem(previewAdapter.getItemPosition(info));
    }

    /**
     * 更新当前图片是否选中，选中下标
     *
     * @param info
     */
    private void updateTopSelected(FileInfo info) {
        if (info.isSelected$zj_album_debug()) {
            dlPreviewTvSelected.setSelected(true);
            //更新里面的内容
            dlPreviewTvSelected.setText(String.valueOf(1 + DataStore.indexOfSelected(info.getPath())));
        } else {
            dlPreviewTvSelected.setSelected(false);
        }
    }

    /**
     * 跳转到选中位置
     *
     * @param index 选中的下标
     */
    private void setCurrentItem(int index) {
        if (index == -1) {
            index = 0;
        }
        viewPager.setCurrentItem(index, false);
    }

    /**
     * 更新当前选中数量
     */
    @Override
    public void onSelectedChanged(int count) {
        if (count == 0) {
            tvSelectCount.setVisibility(View.GONE);
        } else {
            tvSelectCount.setVisibility(View.VISIBLE);
            tvSelectCount.setText(String.valueOf(count + 1));
        }
    }

    /**
     * 更新横向选中预览
     */
    private void updateLandscapeAdapter() {
        mLandscapeAdapter.clear();
        mLandscapeAdapter.add(DataStore.getCurSelectedData());
        mLandscapeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDataGot(@Nullable List<FileInfo> data, @NotNull String curAccessKey) {
        super.onDataGot(data, curAccessKey);
        previewAdapter = new PreviewImageAdapter(true,this);
        previewAdapter.setItems(data);
        viewPager.setAdapter(previewAdapter);
        previewAdapter.notifyDataSetChanged();
    }

    /**
     * 全屏
     */
    @Override
    public void onFull() {
        if (mIsFullPreview) {
            rlPreviewTop.animate()
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .translationYBy(rlPreviewTop.getMeasuredHeight())
                    .setDuration(200)
                    .start();
            rlPreviewBottom.animate()
                    .translationYBy(-rlPreviewBottom.getMeasuredHeight())
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(200)
                    .start();
        } else {
            rlPreviewTop.animate()
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .translationYBy(-rlPreviewTop.getMeasuredHeight())
                    .setDuration(200)
                    .start();
            rlPreviewBottom.animate()
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .translationYBy(rlPreviewBottom.getMeasuredHeight())
                    .setDuration(200)
                    .start();
        }
        mIsFullPreview = !mIsFullPreview;
    }

    /**
     * 取消
     */
    private void cancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    /**
     * 提交
     */
    private void commit() {
        setResult(Activity.RESULT_OK);
        finish();
    }

}
