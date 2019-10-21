package com.zj.album.ui.photograph

import android.view.View
import android.widget.TextView

import com.zj.album.R

import com.zj.album.nModule.FileInfo
import com.zj.album.ui.base.list.adapters.BaseAdapter
import com.zj.album.ui.base.list.holders.BaseViewHolder

import android.view.View.GONE
import android.view.View.VISIBLE
import com.zj.album.nHelpers.DataStore
import com.zj.album.nutils.getDuration

class PhotoGraphAdapter : BaseAdapter<FileInfo>(R.layout.graph_item_selected) {

    private val mImageResize = 0

    override fun initData(holder: BaseViewHolder, position: Int, data: FileInfo) {
        val tvDuration = holder.getView<TextView>(R.id.graph_item_tv_duration)
        tvDuration.visibility = if (data.isVideo) VISIBLE else GONE
        val tvSelected = holder.getView<View>(R.id.graph_item_fl_selected)
        tvSelected.visibility = if (data.isVideo) GONE else VISIBLE
        if (data.isVideo) {
            tvDuration.text = getDuration(data.duration)
        } else {
            val tvNum = holder.getView<TextView>(R.id.graph_item_tv_num)
            tvNum.visibility = if (data.isVideo) GONE else VISIBLE
            tvNum.isSelected = data.isSelected
            tvNum.setOnClickListener {
                data.setSelected(!data.isSelected).let {
                    if (it) {

                    }
                }
            }
            tvNum.text = "${DataStore.indexOfSelected(data.path)}"
        }


    }


    /**
     *

    private void initListener(BaseViewHolder holder, int position, FileInfo data) {
    holder.tvNum.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    if (listener != null) {
    if (!state) {
    if (listener.canSelected(PhotographHelper.getHelper().curSelectedSize())) {
    changeSelected(true);
    } else {
    listener.onFailedSelected();
    }
    } else {
    changeSelected(false);
    }
    } else {
    ToastUtils.show(view.getContext(), holder.itemView.getContext().getResources().getString(R.string.pg_str_data_error));
    }
    }

    private void changeSelected(boolean state) {
    setSelectedDisplay(state);
    holder.tvNum.setSelected(state);
    listener.onSelectChange(holder.getAdapterPosition(), state, media.uri);
    }

    private void setSelectedDisplay(boolean state) {
    holder.selectedView.clearAnimation();
    if (state) {
    AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
    animation.setDuration(400);
    holder.selectedView.startAnimation(animation);
    } else {
    AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
    animation.setDuration(400);
    holder.selectedView.startAnimation(animation);
    }
    holder.selectedView.setVisibility(state ? VISIBLE : GONE);
    }
    });
    holder.flImg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    if (listener != null) {
    listener.onImgClick(state, media);
    }
    }
    });
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoHolder holder, int position) {
    final LocalMedia media = getData().get(position);
    final boolean state = media.isSelector;
    PhotographHelper.getHelper().addRankModule(state, position);
    holder.initData(media);

    }

    class PhotoHolder extends RecyclerHolder {
    private FrameLayout selectedView;
    private ImageView flImg;
    private TextView tvNum, tvDuration;

    PhotoHolder(View v) {
    super(v);
    selectedView = v.findViewById(R.id.v_photoView_flSelected);
    flImg = v.findViewById(R.id.v_photoView_flImg);
    tvNum = v.findViewById(R.id.v_photoView_tvNum);
    tvDuration = v.findViewById(R.id.durationView);
    }

    private void initData(LocalMedia media) {
    if (media.isVideo()) {
    tvNum.setVisibility(GONE);
    tvDuration.setVisibility(VISIBLE);
    tvDuration.setText(TimeConversionUtils.getDuration(media.duration));
    }
    selectedView.setVisibility(media.isSelector ? VISIBLE : GONE);
    tvNum.setSelected(media.isSelector);

    SelectionSpec.INSTANCE.getImageLoader().loadThumbnail(
    flImg,
    getImageResize(),
    R.color.gray,
    media.getFileUri()
    );

    //            ImageLoaderUtils.load(flImg, media.getFileUri());
    tvNum.setText(media.isSelector ? String.valueOf(PhotographHelper.getHelper().isContainInSelected(media.uri)[1]) : "");
    }

    }

    /**
     * 获取图片缩略图长宽
     *
     * @return 图片长宽
    */
    private int getImageResize() {
    if (this.mImageResize == 0) {
    RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
    assert lm != null;
    int spanCount = ((GridLayoutManager) lm).getSpanCount();
    int screenWidth = mRecyclerView.getResources().getDisplayMetrics().widthPixels;
    mImageResize = screenWidth / spanCount;
    //0.5 图片缩放比例 为了节省内存
    mImageResize = (int) (mImageResize * 0.6f);
    }
    return mImageResize;
    }

    public interface ChangeListener {

    void onSelectChange(int position, boolean isSelected, String uri);

    boolean canSelected(int curNum);

    void onFailedSelected();

    void onImgClick(boolean isSelected, LocalMedia uri);
    }
     *
     * */


}
