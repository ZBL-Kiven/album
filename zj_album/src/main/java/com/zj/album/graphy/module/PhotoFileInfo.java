package com.zj.album.graphy.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaojie on 2017/10/19.
 */

public class PhotoFileInfo implements Serializable {
    public String parentPath;
    public int imageCounts;
    public String topImgUri;
    public List<LocalMedia> localMedias = new ArrayList<>();
}
