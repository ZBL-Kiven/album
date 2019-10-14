package com.zj.album.graphy;

import com.zj.album.graphy.module.LocalMedia;
import com.zj.album.interfaces.PhotoEvent;
import com.zj.album.services.Constancs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaojie on 2017/10/20.
 */

public class PhotographHelper {

    private PhotographHelper(String cacheNameCode) {
        PhotographHelper.cacheNameCode = cacheNameCode;
        photoTemporaryCache = new PhotoTemporaryCache(cacheNameCode);
    }

    private static PhotographHelper helper;
    private static String cacheNameCode;

    public static PhotographHelper getHelper() {
        if (helper == null) {
            synchronized (PhotoFileHelper.class) {
                if (helper == null) {
                    init(cacheNameCode);
                }
            }
        }
        return helper;
    }

    private PhotoTemporaryCache photoTemporaryCache;

    public static void init(String cacheNameCode) {
        helper = new PhotographHelper(cacheNameCode);
        PhotoFileHelper.init(cacheNameCode);
    }

    //当前文件夹内所有的图片
    private List<LocalMedia> allPhotos = new ArrayList<>();

    private List<Integer> rankModules = new ArrayList<>();

    public List<Integer> getRankModules() {
        return rankModules;
    }

    public void addRankModule(boolean state, int position) {
        List<Integer> rs = new ArrayList<>();
        if (state) {
            rs.add(position);
        }
        for (int module : rankModules) {
            if (module != position) rs.add(module);
        }
        rankModules.clear();
        rankModules.addAll(rs);
        rs.clear();
    }

    public final List<LocalMedia> getAllPhotos() {
        return allPhotos;
    }

    /**
     * 从临时缓存内删除,注意！仅临时缓存
     */
    public void removeSelectedPhoto(String uri, PhotoEvent event) {
        photoTemporaryCache.deleteOne(uri);
        setAllPhotosSync(false, uri);
        if (event != null)
            event.onEvent(Constancs.HelperEventCode_SelectedChange, true);
    }

    //添加一个临时缓存，注意！仅临时缓存
    public void addSelectedPhoto(String uri, PhotoEvent event) {
        photoTemporaryCache.addAChoosePhoto(uri);
        setAllPhotosSync(true, uri);
        if (event != null)
            event.onEvent(Constancs.HelperEventCode_SelectedChange, true);
    }

    /**
     * 与所有图片进行选择同步，有所有图片的情况下；
     */
    private void setAllPhotosSync(boolean isSelected, String uri) {
        // TODO: 2018/12/29 photo 这个方法在每次选取取消图片的时候都要遍历 array 我觉得可以拿哈希表优化一下
        if (allPhotos != null && allPhotos.size() > 0)
            for (LocalMedia info : allPhotos) {
                if (info.uri.equals(uri)) {
                    info.isSelector = isSelected;
                    return;
                }
            }
    }

    public void removeAllCachePhotos() {
        PhotoTemporaryCache.removeChoosePhotos(cacheNameCode);
    }

    /**
     * 保存临时缓存
     */
    public void saveSelectedPhotos() {
        PhotoTemporaryCache.saveImageCache(cacheNameCode);
    }

    /**
     * 获取当前已选择的临时缓存
     */
    public List<LocalMedia> getCurSelectedPhotos() {
        return photoTemporaryCache.getChoosePhotos();
    }

    /**
     * 当前已选择的临时缓存数
     **/
    public int curSelectedSize() {
        return photoTemporaryCache.getChoosePhotos().size();
    }

    /**
     * 设置一个相册列表，当读取系统文件后，将缓存内图片和所有图片做操作融合
     * <p>
     * 融合前，先排除已经不存在的文件；
     */

    public void setAsAllPhotos(List<LocalMedia> infos, String cacheNameCode, PhotoEvent event) {
        if (infos == null || infos.size() == 0) {
            event.onEvent(Constancs.HelperEvenErroCode, true);
            return;
        }
        allPhotos.clear();
        allPhotos.addAll(infos);
        if (photoTemporaryCache == null) init(cacheNameCode);
        List<LocalMedia> ps = photoTemporaryCache.getChoosePhotos();
        if (ps == null || ps.size() == 0) {
            event.onEvent(Constancs.HelperEventCode, true);
            return;
        }
        for (LocalMedia info : allPhotos) {
            for (LocalMedia i : ps) {
                if (info.uri.equals(i.uri)) {
                    info.isSelector = true;
                    info.index = i.index;
                }
            }
        }
        ps.clear();
        event.onEvent(Constancs.HelperEventCode, true);
    }

    /**
     * [0]包含返回 0，不包含返回1 ；
     * [1]返回当前顺序值，每次都返回该数据在已选中列表内的序列号+1；
     * 如果不包含，则会返回当前size+1
     */
    public int[] isContainInSelected(String uri) {
        List<LocalMedia> infos = photoTemporaryCache.getChoosePhotos();
        int isContain = 1, index = infos.size() + 1;
        for (LocalMedia info : infos) {
            if (info.uri.equals(uri)) {
                isContain = 0;
                index = infos.indexOf(info) + 1;
                break;
            }
        }
        return new int[]{isContain, index};
    }

    /**
     * 1。进行已选择列表的更新，该更新作为缓存保存，即使以后进来，也能读取已选择数据；
     * 2。更改已储存的图片列表数据，该数据将在完全离开页面之后失效
     */
    public void onSelectedChanged(boolean state, String uri, PhotoEvent event) {
        if (state) {
            addSelectedPhoto(uri, event);
        } else {
            removeSelectedPhoto(uri, event);
        }
    }

    public void clearSelectedCache() {
        if (photoTemporaryCache != null) {
            photoTemporaryCache.destroy();
            photoTemporaryCache = null;
        }
        rankModules.clear();
        rankModules = null;
        allPhotos.clear();
        allPhotos = null;
        helper = null;
    }
}
