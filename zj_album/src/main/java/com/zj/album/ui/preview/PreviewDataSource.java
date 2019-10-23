package com.zj.album.ui.preview;

import android.annotation.SuppressLint;


import java.util.ArrayList;
import java.util.List;

/**
 * @author yangji
 */
public class PreviewDataSource {

    private List<String> selected = new ArrayList<>();

    @SuppressLint("SdCardPath")
    public List<String> getPath() {

        List<String> paths = new ArrayList<>();
        paths.add("/storage/emulated/0/DCIM/Live message/LiveMessage_2019-06-18-17-12-24.gif");
        paths.add("/sdcard/DCIM/Camera/20190725_161732.mp4");
        paths.add("/storage/emulated/0/Download/SunPeople/SunPeople_2019-06-05-07-16-40.jpg");
        paths.add("/sdcard/DCIM/Camera/20190627_221226.jpg");
        paths.add("/sdcard/DCIM/Camera/20190903_145445.jpg");
        paths.add("/sdcard/DCIM/Camera/20190923_111443.jpg");
        paths.add("/sdcard/DCIM/Camera/20190923_111430.jpg");
        return paths;
    }

    public List<String> getSelected() {
        return selected;
    }

    public int getSelectCount() {
        return selected.size();
    }

    public boolean getSingle() {
        return false;
    }


    public boolean switchover(int currentItem) {
        String item = getPath().get(currentItem);
        if (selected.contains(item)) {
            selected.remove(item);
            return false;
        } else {
            selected.add(item);
            return true;
        }
    }

    public int getSelectIndex(String path) {
        return selected.indexOf(path);
    }
}
