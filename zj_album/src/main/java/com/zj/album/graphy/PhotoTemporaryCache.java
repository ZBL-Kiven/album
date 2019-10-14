package com.zj.album.graphy;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.zj.album.graphy.module.LocalMedia;
import com.zj.album.cache.PhotoCacheUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaojie on 2017/10/24.
 * <p>
 * 临时文件夹，用于多进程通信文件共享，目前此功能下不会出现并发访问
 */

public class PhotoTemporaryCache {

    private static String temporaryName = "temporary";

    public static void setTemporaryName(String tm) {
        temporaryName += tm;
    }

    /**
     * 在构造函数内，需要对曾经确定过的（点击完成所保存在缓存内的，非临时缓存）文件进行同步；
     * 如需要与服务器同步上传完成数据，则在构造此方法前，先完成同步；
     */
    public PhotoTemporaryCache(String cacheNameCode) {
        String cache = PhotoCacheUtils.getInstance().getCache(imageCacheName + cacheNameCode);
        PhotoCacheUtils.getInstance().saveCache(temporaryName, cache);
    }

    /**
     * 刷新临时缓存文件
     */
    public void saveAChoosePhotoList(List<LocalMedia> infos) {
        PhotoCacheUtils.getInstance().saveCache(temporaryName, JSON.toJSONString(infos));
    }

    /**
     * 获取临时选择的图片，自动排除已失效的文件；
     */
    public List<LocalMedia> getChoosePhotos() {
        String result = PhotoCacheUtils.getInstance().getCache(temporaryName);
        List<LocalMedia> datas = JSON.parseArray(result, LocalMedia.class);
        if (datas == null) return new ArrayList<>();
        return datas;
    }

    public void addAChoosePhoto(String uri) {
        List<LocalMedia> photoInfos = getChoosePhotos();
        if (photoInfos == null) {
            photoInfos = new ArrayList<>();
        }
        for (LocalMedia info : photoInfos) {
            if (info.getFileUri().equals(uri)) return;
        }
        photoInfos.add(new LocalMedia(uri, true));
        saveAChoosePhotoList(photoInfos);
        photoInfos.clear();
    }

    /**
     * 删除某一条临时缓存
     */
    public void deleteOne(String uri) {
        List<LocalMedia> curSelectedPhotos = getChoosePhotos();
        if (curSelectedPhotos == null || curSelectedPhotos.size() <= 0) return;
        List<LocalMedia> removeIndexInfos = new ArrayList<>();
        for (int i = 0; i < curSelectedPhotos.size(); i++) {
            LocalMedia info = curSelectedPhotos.get(i);
            if (info.uri.equals(uri)) {
                removeIndexInfos.add(info);
            }
        }
        curSelectedPhotos.removeAll(removeIndexInfos);
        saveAChoosePhotoList(curSelectedPhotos);
        curSelectedPhotos.clear();
    }

    public void destroy() {
        PhotoCacheUtils.getInstance().removeFile(temporaryName);
    }

    //-----------------------------------------------非临时缓存--------------------------------------------------------//
    /**
     * @version 1.0.0
     * <p>
     * 在这之后，不再是对临时文件进行操作，此操作都是静态方法，允许用户在外部调用对最终缓存做任何操作，
     * cacheNameCode 为分区用，允许用户保存多个相册选择列表；
     */
    /**
     * 缓存文件夹目录
     */
    private static final String imageCacheName = "cache_";


    public static void clear() {
        PhotoCacheUtils.getInstance().clearAll();
    }

    /**
     * 通过缓存区名，拿到该分区下的所有缓存
     */
    public static final List<LocalMedia> getCacheImages(String cacheNameCode) {
        String result = PhotoCacheUtils.getInstance().getCache(imageCacheName + cacheNameCode);
        List<LocalMedia> resultInfos = new ArrayList<>();
        List<LocalMedia> infos = JSON.parseArray(result, LocalMedia.class);
        if (infos == null || infos.size() == 0) {
            return null;
        }
        for (LocalMedia info : infos) {
            File file = new File(info.uri.replaceAll("file://", ""));
            if (file.exists() && !file.isDirectory()) {
                info.uri = file.getPath();
                resultInfos.add(info);
            }
        }
        if (resultInfos.size() != infos.size()) {
            //内存中数据发生改变，需要刷新缓存
            refreshImageCache(cacheNameCode, resultInfos);
        }
        infos.clear();
        return resultInfos;
    }

    /**
     * 从缓存移除一个选中的图片（用于删除已选择）
     */
    public static void deleteAPhoto(String cacheNameCode, String uri) {
        List<LocalMedia> photos = getCacheImages(cacheNameCode);
        if (photos == null || photos.size() <= 0) return;
        List<LocalMedia> removeIndexInfos = new ArrayList<>();
        for (int i = 0; i < photos.size(); i++) {
            LocalMedia info = photos.get(i);
            if (info.uri.equals(uri)) {
                removeIndexInfos.add(info);
            }
        }
        photos.removeAll(removeIndexInfos);
        refreshImageCache(cacheNameCode, photos);
        photos.clear();
    }

    /**
     * 保存一个选中的图片在缓存内，已去重；
     */
    public static void saveAPhoto(String cacheNameCode, @NonNull String uri) {
        List<LocalMedia> photoInfos = getCacheImages(cacheNameCode);
        if (photoInfos == null) {
            photoInfos = new ArrayList<>();
        }
        for (LocalMedia info : photoInfos) {
            if (info.uri.equals(uri)) return;
        }
        photoInfos.add(new LocalMedia(uri, true));
        refreshImageCache(cacheNameCode, photoInfos);
        photoInfos.clear();
    }

    /**
     * 为缓存保存操作,将会默认的把临时缓存文件数据转载至缓存文件
     */
    public static void saveImageCache(String cacheNameCode) {
        String temporary = PhotoCacheUtils.getInstance().getCache(temporaryName);
        PhotoCacheUtils.getInstance().saveCache(imageCacheName + cacheNameCode, temporary);
    }

    /**
     * 刷新缓存，替换内容
     */
    public static void refreshImageCache(String cacheNameCode, List<LocalMedia> infos) {
        PhotoCacheUtils.getInstance().saveCache(imageCacheName + cacheNameCode, JSON.toJSONString(infos));
    }

    /**
     * 删除某一命名下的所有缓存
     */
    public static void removeChoosePhotos(String cacheNameCode) {
        PhotoCacheUtils.getInstance().removeFile(imageCacheName + cacheNameCode);
    }
}
