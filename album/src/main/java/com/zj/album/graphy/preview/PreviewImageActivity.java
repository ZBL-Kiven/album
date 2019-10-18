package com.zj.album.graphy.preview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zj.album.R;
import com.zj.album.graphy.preview.adapter.LandscapeAdapter;
import com.zj.album.graphy.preview.adapter.PreviewImageAdapter;
import com.zj.album.graphy.preview.listener.FullPreviewListener;
import com.zj.album.graphy.views.IRecyclerAdapter;
import com.zj.album.widget.JViewPager;

/**
 * @author yangji
 */
public class PreviewImageActivity extends AppCompatActivity implements FullPreviewListener {


    private JViewPager viewPager;
    private View rlPreviewTop, rlPreviewBottom;
    private TextView selectCountView;
    private RecyclerView dlPreviewLvSelect;

    private View dlPreviewFlSelected;
    private TextView dlPreviewTvSelected;

    private boolean mIsFullPreview = false;
    private PreviewDataSource dataSource;

    private PreviewImageAdapter previewAdapter ;
    private LandscapeAdapter mLandscapeAdapter;

    private IRecyclerAdapter.OnItemCLickListener mOnItemCLickListener = new IRecyclerAdapter.OnItemCLickListener() {
        @Override
        public void onItemClick(int postion, View view) {
            indexOf(mLandscapeAdapter.getItem(postion));
        }
    };


    @SuppressLint("SdCardPath")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previce);
        dataSource = new PreviewDataSource();

        initView();
        initListener();
        initData();
    }

    private void initView() {
        rlPreviewTop = findViewById(R.id.rl_preview_top);
        dlPreviewFlSelected = findViewById(R.id.dl_preview_fl_selected);
        dlPreviewTvSelected = findViewById(R.id.dl_preview_tv_selected);

        rlPreviewBottom = findViewById(R.id.rl_preview_bottom);

        selectCountView = findViewById(R.id.selectCountView);
        dlPreviewLvSelect = findViewById(R.id.dl_preview_lvSelect);

        viewPager = findViewById(R.id.dl_preview_viewpager);
        viewPager.setPageTransformer(true, new ZoomPageTransformer());

        //设置横向预览
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        dlPreviewLvSelect.setLayoutManager(linearLayoutManager);
        dlPreviewLvSelect.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mLandscapeAdapter = new LandscapeAdapter(mOnItemCLickListener);
        dlPreviewLvSelect.setAdapter(mLandscapeAdapter);
    }


    private void initListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int currentItem) {
                previewAdapter.reset(currentItem);



                updateTopSelected(currentItem);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        dlPreviewFlSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                dataSource.switchover(currentItem);
                updateTopSelected(currentItem);
                updateLandscapeAdapter();
                updateSelectCount();
            }
        });
    }


    private void initData() {
        updateSelectCount();
        updateLandscapeAdapter();
        updateSelectCount();

        previewAdapter = new PreviewImageAdapter(this);
        previewAdapter.setItems(dataSource.getPath());
        viewPager.setAdapter(previewAdapter);
        indexOf("");
    }

    /**
     * 全预览
     *
     * @param path
     */
    private void indexOf(String path) {
        setCurrentItem(dataSource.getPath().indexOf(path));
    }

    void updateTopSelected(int currentItem) {
        String item = dataSource.getPath().get(currentItem);
        int selectIndex = dataSource.getSelectIndex(item) + 1;
        if (selectIndex > 0) {
            dlPreviewTvSelected.setSelected(true);
            //更新里面的内容
            dlPreviewTvSelected.setText(String.valueOf(selectIndex));
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
        int selectedCount = dataSource.getSelectCount();
        if (selectedCount == 0) {
            selectCountView.setVisibility(View.GONE);
        } else {
            selectCountView.setVisibility(View.VISIBLE);
            selectCountView.setText(String.valueOf(selectedCount));
        }
    }

    private void updateLandscapeAdapter() {
        mLandscapeAdapter.clear();
        mLandscapeAdapter.add(dataSource.getSelected());
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

}
