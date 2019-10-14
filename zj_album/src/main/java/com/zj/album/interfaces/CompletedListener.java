package com.zj.album.interfaces;

import java.util.List;

/**
 * Created by zhaojie on 2018/2/26.
 */

public interface CompletedListener {

    void Success(String code, List<String> params);
}
