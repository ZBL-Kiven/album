package com.zj.album.graphy;

import com.zj.album.graphy.module.PhotoFileInfo;
import com.zj.album.interfaces.PhotoEvent;
import com.zj.album.services.Constancs;
import com.zj.album.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaojie on 2017/10/24.
 * 应该与Activity生命周期相等，需实例化使用
 */

public class PhotoFileHelper {

    private static PhotoFileHelper helper;
    private static String cacheNameCode;

    private PhotoFileHelper(String cCode) {
        cacheNameCode = cCode;
    }

    public static PhotoFileHelper getInstance() {
        if (helper == null) {
            synchronized (PhotoFileHelper.class) {
                if (helper == null) {
                    init(cacheNameCode);
                }
            }
        }
        return helper;
    }

    private static void init(String cacheNameCode) {
        helper = new PhotoFileHelper(cacheNameCode);
    }

    //当前所预览的文件夹
    private List<PhotoFileInfo> fileInfos = new ArrayList<>();

    //当前显示的文件夹
    private PhotoFileInfo curDisplayFile;

    public void setCurFileInfo(PhotoFileInfo info, PhotoEvent event) {
        if (curDisplayFile != info) {
            curDisplayFile = info;
            PhotographHelper.getHelper().setAsAllPhotos(curDisplayFile.localMedias, cacheNameCode, event);
        } else {
            event.onEvent(Constancs.HelperEventCode, false);
        }
    }

    public void setFileInfos(List<PhotoFileInfo> infos, PhotoEvent event) {
        if (infos == null || infos.size() == 0) {
            DebugUtils.e("-------setFileInfos----    infos must is not a null object ");
            return;
        }
        fileInfos.clear();
        this.fileInfos.addAll(infos);
        setCurFileInfo(fileInfos.get(0), event);
    }

    public List<PhotoFileInfo> getFileInfos() {
        return fileInfos;
    }

    public void release() {
        cacheNameCode = null;
        fileInfos.clear();
        curDisplayFile = null;
        helper = null;
    }
}
