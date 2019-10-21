package com.zj.album.ui.preview;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int currentItem) {
                previewAdapter.reset(currentItem);
                updateTopSelected(previewAdapter.getItem(currentItem));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        dlPreviewFlSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                FileInfo info = previewAdapter.getItem(currentItem);
                info.setSelected$zj_album_debug(info.isSelected());
                updateTopSelected(info);
                updateLandscapeAdapter();
                updateSelectCount();
            }
        });
    }


    @Override
    public void initData() {
        updateLandscapeAdapter();
        updateSelectCount();
    }

    /**
     * 全预览
     */
    private void indexOf(FileInfo info) {
        setCurrentItem(dataSource.getPath().indexOf(path));
    }

    void updateTopSelected(FileInfo info) {
        if (info.isSelected()) {
            dlPreviewTvSelected.setSelected(true);
            //更新里面的内容
            dlPreviewTvSelected.setText(String.valueOf(1));
        } else {
            dlPreviewTvSelected.setSelected(false);
        }
    }

    private void setCurrentItem(int index) {
        if (index == -1) {
            index = 0;
        }
        viewPager.setCurrentItem(index, false);
    }

    private void updateSelectCount() {
        int selectedCount = DataStore.getCurSelectedData().size();
        if (selectedCount == 0) {
            tvSelectCount.setVisibility(View.GONE);
        } else {
            tvSelectCount.setVisibility(View.VISIBLE);
            tvSelectCount.setText(String.valueOf(selectedCount));
        }
    }

    private void updateLandscapeAdapter() {
        mLandscapeAdapter.clear();
        mLandscapeAdapter.add(DataStore.getCurSelectedData());
        mLandscapeAdapter.notifyDataSetChanged();
    }

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


    @Override
    public void onDataGot(@Nullable List<FileInfo> data) {
        super.onDataGot(data);
        previewAdapter = new PreviewImageAdapter(this);
        viewPager.setAdapter(previewAdapter);
        previewAdapter.notifyDataSetChanged();
    }

}
