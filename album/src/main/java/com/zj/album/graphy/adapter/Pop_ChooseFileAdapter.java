package com.zj.album.graphy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.album.R;
import com.zj.album.graphy.PhotoFileHelper;
import com.zj.album.graphy.module.PhotoFileInfo;
import com.zj.album.graphy.views.IBaseAdapter;
import com.zj.album.graphy.views.IRecyclerAdapter;
import com.zj.album.utils.ImageLoaderUtils;

/**
 * Created by zhaojie on 2017/10/25.
 */

public class Pop_ChooseFileAdapter extends IBaseAdapter<PhotoFileInfo> {

    private IRecyclerAdapter.OnItemCLickListener clickListener;
    private Context context;

    public Pop_ChooseFileAdapter(Context context, IRecyclerAdapter.OnItemCLickListener listener) {
        this.context = context;
        this.clickListener = listener;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FileHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_pop_choosefile, viewGroup, false);
            holder = new FileHolder(view);
            view.setTag(holder);
        } else {
            holder = (FileHolder) view.getTag();
        }
        holder.initData(i, view);
        return view;
    }

    class FileHolder {
        private TextView tvName;
        private ImageView ivImg;
//        private View vSelect;
        private TextView tv_count;

        public FileHolder(View view) {
            ivImg = view.findViewById(R.id.item_cp_ivImg);
            tvName = view.findViewById(R.id.item_cp_tvName);
//            vSelect = view.findViewById(R.id.item_cp_vSelect);
            tv_count = view.findViewById(R.id.tv_count);
        }

        public void initData(final int i, final View v) {
            final PhotoFileInfo info = getInfos().get(i);
            ImageLoaderUtils.load(ivImg, info.topImgUri);
            tvName.setText(info.parentPath);
            tv_count.setText(String.valueOf(info.imageCounts));
//            String uri = PhotoFileHelper.getInstance("").getCurDisplayFile().topImgUri;
//            vSelect.setSelected(info.topImgUri.equals(uri));
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(i, v);
                }
            });
        }
    }
}















