package com.zj.album.graphy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zj.album.R;
import com.zj.album.entity.SelectionSpec;
import com.zj.album.graphy.PhotographHelper;
import com.zj.album.graphy.adapter.BannerItemAdapter;
import com.zj.album.graphy.adapter.PreviewAdapter;
import com.zj.album.graphy.module.LocalMedia;
import com.zj.album.graphy.views.IRecyclerAdapter;
import com.zj.album.imageloader.utils.ImageEvaluate;
import com.zj.album.interfaces.PhotoEvent;
import com.zj.album.preview_banner.PreviewBanner;
import com.zj.album.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;


/**
 * @author yangji
 */
public class PhotoPreviewActivity_j extends AppCompatActivity {

    private View.OnClickListener onClickListener;
    private FrameLayout flScreen;
    private View vSend;
    private View ivBack;
    private CheckBox vCheck;
    private RecyclerView rvSelectedPhotos;
    private TextView tvComplete, tvSelectCount;
    private PreviewBanner previewBanner;
    private PreviewAdapter adapter;
    private List<LocalMedia> datas = new ArrayList<>();
    private int maxPhotoSize;
    private String curImgUri;
    private boolean isSelected;
    private static final int bannerHackSize = 3;
    private BannerItemAdapter bannerItemAdapter;
    private AlphaAnimation alphaAnimationIn, alphaAnimationOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_preview_photo);
        getIntentData();
        initView();
        initData();
        initListener();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        curImgUri = intent.getStringExtra("curImgUri");
        isSelected = intent.getBooleanExtra("isSelected", false);
        maxPhotoSize = intent.getIntExtra("maxPhotoSize", 0);
        boolean isAll = intent.getBooleanExtra("isAll", false);
        if (isAll) {
            datas = new ArrayList<>(PhotographHelper.getHelper().getAllPhotos());
        } else {
            datas = new ArrayList<>(PhotographHelper.getHelper().getCurSelectedPhotos());
        }
    }

    private void initView() {
        ivBack = findViewById(R.id.dl_preview_ivBack);
        vSend = findViewById(R.id.photo_send);
        vCheck = findViewById(R.id.sendFullImageView);
        tvComplete = findViewById(R.id.dl_preview_tvSelected);
        tvSelectCount = findViewById(R.id.selectCountView);
        flScreen = findViewById(R.id.dl_preview_flScreen);
        rvSelectedPhotos = findViewById(R.id.dl_preview_lvSelect);
        previewBanner = findViewById(R.id.dl_preview_bgaBanner);
        tvComplete.setSelected(isSelected);
        vCheck.setChecked(true);
    }

    private void initData() {
        alphaAnimationIn = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimationOut = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimationIn.setDuration(400);
        alphaAnimationOut.setDuration(400);
        adapter = new PreviewAdapter(new IRecyclerAdapter.OnItemCLickListener() {
            @Override
            public void onItemClick(int postion, View view) {
                String uri = adapter.getData().get(postion).uri;
                for (int i = 0; i < datas.size(); i++) {
                    LocalMedia info = datas.get(i);
                    if (info.uri.equals(uri)) {
                        initBannerAdapter(i);
                        return;
                    }
                }
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvSelectedPhotos.setLayoutManager(manager);
        rvSelectedPhotos.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvSelectedPhotos.setAdapter(adapter);
        initDatas(curImgUri);
    }

    private void initListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean state = !tvComplete.isSelected();
                if (state && PhotographHelper.getHelper().curSelectedSize() >= maxPhotoSize) {
                    ToastUtils.show(PhotoPreviewActivity_j.this, PhotoPreviewActivity_j.this.getString(R.string.im_at_best, "" + maxPhotoSize));
                } else {
                    PhotoPreviewActivity_j.this.selectImg(state);
                }
            }
        });
        vSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 是否要原图
                Intent intent = new Intent();
                intent.putExtra("fullSize", vCheck.isChecked());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isVisible = flScreen.getVisibility() == View.VISIBLE;
                if (alphaAnimationIn != null && alphaAnimationOut != null) {
                    alphaAnimationIn.cancel();
                    alphaAnimationOut.cancel();
                    flScreen.startAnimation(isVisible ? alphaAnimationOut : alphaAnimationIn);
                }
                flScreen.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            }
        };
        vCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && PhotographHelper.getHelper().curSelectedSize() == 0) {
                    PhotoPreviewActivity_j.this.selectImg(true);
                }
            }
        });
    }

    private void selectImg(boolean state) {
        PhotographHelper.getHelper().onSelectedChanged(state, datas.get(bannerItemAdapter.getDisplayPosition()).uri, new PhotoEvent() {
            @Override
            public void onEvent(int code, boolean isValidate) {
                updateDisplayView(bannerItemAdapter.getDisplayPosition());
            }
        });
    }

    /**
     * 设置数据
     */
    private void initDatas(String curImgUri) {
        int curSelectPosition = 0;
        for (int i = 0; i < datas.size(); i++) {
            LocalMedia info = datas.get(i);
            if (info.uri.equals(curImgUri)) {
                previewBanner.setCurrentItem(i);
                curSelectPosition = i;
                refreshTvComplete(info);
                break;
            }
        }
        adapter.clear();
        adapter.add(PhotographHelper.getHelper().getCurSelectedPhotos());
        initBannerAdapter(curSelectPosition);
    }

    private void refreshTvComplete(LocalMedia info) {
        int[] containInSelected = PhotographHelper.getHelper().isContainInSelected(info.uri);
        if (containInSelected[0] == 0) {
            tvComplete.setSelected(true);
            tvComplete.setText(String.valueOf(containInSelected[1]));
        } else {
            tvComplete.setSelected(false);
        }
        int size = PhotographHelper.getHelper().curSelectedSize();
        if (size > 0) {
            tvSelectCount.setText(String.valueOf(size));
            tvSelectCount.setVisibility(View.VISIBLE);
        } else {
            tvSelectCount.setVisibility(View.GONE);
        }
    }

    private void initBannerAdapter(int curSelectPosition) {

        if (bannerItemAdapter != null) {
            int current = previewBanner.getViewPager().getCurrentItem();
            if (current % datas.size() == curSelectPosition) {
                return;
            } else {
                int index = (current / datas.size()) + curSelectPosition;
                previewBanner.getViewPager().setCurrentItem(index);
            }
//            previewBanner.setCurrentItem(curSelectPosition);
            System.out.println("curSelectPosition:" + curSelectPosition);
            return;
        }
        bannerItemAdapter = new BannerItemAdapter(bannerHackSize, curSelectPosition, previewBanner, datas, new BannerItemAdapter.OnPageChange() {
            @Override
            public void onChange(int position, View v) {
                String uri = datas.get(position).getFileUri();
                if (v instanceof ImageViewTouch) {
                    final ImageViewTouch it = (ImageViewTouch) v;
                    Point size = ImageEvaluate.getBitmapSize(uri);
                    SelectionSpec.INSTANCE.getImageLoader().loadImage(it, size.x, size.y, uri);
                    it.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
                        @Override
                        public void onSingleTapConfirmed() {
                            onClickListener.onClick(it);
                        }
                    });

                }
            }

            @Override
            public void onDisplayChange(int position) {
                updateDisplayView(position);
            }
        });
    }

    /**
     * 设置显示情景
     */
    private void updateDisplayView(int position) {
        if (datas == null || datas.size() <= 0) {
            return;
        }
        List<LocalMedia> infos = PhotographHelper.getHelper().getCurSelectedPhotos();
        refreshTvComplete(datas.get(position));
        //选择数据发生改变的话，更新下面显示
        if (adapter.getData().size() != infos.size()) {
            adapter.clear();
            adapter.add(infos);
        }
    }

    @Override
    protected void onDestroy() {
        if (datas != null) {
            datas.clear();
            datas = null;
        }
        super.onDestroy();
    }
}
