package com.zj.album.services

import android.os.Environment
import java.io.File

/**
 * Created by zhaojie on 2017/10/11.
 *
 *
 * this class save all static values or keys
 */

object Constancs {

    /**
     * 缓存文件名
     */
    val PhotoCachePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "photoCache" + File.separator
    const val MediaSortDesc = true

    /**
     * 配置忽略清单，包含这些名称的文件将不被读入自定义相册,新增请添加注释；
     *
     * @idCard_positive 身份证自定义拍照正面
     * @idCard_negative 身份证自定义拍照背面
     * @cacheImg 所有自定义拍照的临时缓存
     */
    val ignore = arrayOf<String>()
    //            "idCard_positive",
    //            "idCard_negative",
    //            "cacheImg"

    const val HelperEventCode = 0x10117a
    const val HelperEventCode_SelectedChange = 0x10118a
    const val HelperEvenErroCode = 0x10119a
    const val USER_DATA = "userData"
}
