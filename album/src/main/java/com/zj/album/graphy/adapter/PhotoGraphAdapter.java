package com.zj.album.graphy.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.album.R;
import com.zj.album.graphy.PhotographHelper;
import com.zj.album.graphy.module.LocalMedia;
import com.zj.album.graphy.views.IBaseRecyclerAdapter;
import com.zj.album.graphy.views.RecyclerHolder;
import com.zj.album.utils.ImageLoaderUtils;
import com.zj.album.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zhaojie on 2017/10/19.
 */

public class PhotoGraphAdapter extends IBaseRecyclerAdapter<LocalMedia, PhotoGraphAdapter.PhotoHolder> {

    private LayoutInflater inflater;
    private ChangeListener listener;

    public PhotoGraphAdapter(ChangeListener listener) {
        super(null);
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.v_photoselected, parent, false);
        return new PhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoHolder holder, int position) {
        final LocalMedia media = getData().get(position);
        final boolean state = media.isSelector;
        PhotographHelper.getHelper().addRankModule(state, position);
        holder.initData(media);
        holder.tvNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    if (!state)
                        if (listener.canSelected(PhotographHelper.getHelper().curSelectedSize())) {
                            changeSelected(true);
                        } else {
                            listener.onFailedSelected();
                        }
                    else {
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
        holder.fl_Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onImgClick(state, media);
            }
        });
    }

    public static class PhotoHolder extends RecyclerHolder {
        private FrameLayout selectedView;
        private ImageView fl_Img;
        private TextView tvNum, tvDuration;

        PhotoHolder(View v) {
            super(v);
            selectedView = v.findViewById(R.id.v_photoView_flSelected);
            fl_Img = v.findViewById(R.id.v_photoView_flImg);
            tvNum = v.findViewById(R.id.v_photoView_tvNum);
            tvDuration = v.findViewById(R.id.durationView);
        }

        public void initData(LocalMedia media) {
            if (media.isVideo()) {
                tvNum.setVisibility(GONE);
                tvDuration.setVisibility(VISIBLE);
                tvDuration.setText(getDuration(media.duration));
            }
            selectedView.setVisibility(media.isSelector ? VISIBLE : GONE);
            tvNum.setSelected(media.isSelector);
            ImageLoaderUtils.load(fl_Img, media.getFileUri());
            tvNum.setText(media.isSelector ? String.valueOf(PhotographHelper.getHelper().isContainInSelected(media.uri)[1]) : "");
        }

        private String getDuration(long mediaDuration) {
            long duration = mediaDuration / 1000;
            long minute = duration / 60;
            long second = duration % 60;
            return String.format(Locale.getDefault(), "%d:%d", minute, second);
        }
    }

    public interface ChangeListener {

        void onSelectChange(int position, boolean isSelected, String uri);

        boolean canSelected(int curNum);

        void onFailedSelected();

        void onImgClick(boolean isSelected, LocalMedia uri);
    }
}
