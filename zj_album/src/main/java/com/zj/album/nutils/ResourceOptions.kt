package com.zj.album.nutils

import com.zj.album.R

open class ResourceOptions {

    open var pgStrLoadingNoData: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.loading_no_data); else field
        } //沒有找到可用圖片
    open var pgStrLoadingProgress: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.loading_progress); else field
        } //載入中，請稍候…
    open var pgStrLoadingRefresh: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.loading_refresh); else field
        } //轻触屏幕以重試
    open var pgStrLoadingVideoProgress: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.loading_video_progress); else field
        } //視頻載入中，請稍候…
    open var pgStrLoadingVideoError: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.loading_video_error); else field
        } //視頻載入失敗
    open var pgStrLoadingVideoErrorTint: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.loading_video_error_tint); else field
        } //請檢查文件是否損壞或暫不支持該格式

    open var pgStrAll: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.pg_str_all); else field
        } //全部
    open var pgStrCancel: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.pg_str_cancel); else field
        } //返回
    open var pgStrAlbum: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.pg_str_album); else field
        } //文件夾
    open var pgStrFiles: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.pg_str_all_files); else field
        } //所有類別
    open var pgStrCount: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.pg_str_picture_count); else field
        } //共 %1$d 個文件
    open var pgStrPreview: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.pg_str_preview); else field
        } //預覽
    open var pgStrSend: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.pg_str_send); else field
        } //完成
    open var pgStrOriginal: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.pg_str_full_images_tip); else field
        } //原圖
    open var pgStrPhotoAndVideoCannotUseTogether: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.pg_str_images_choose_tip_message); else field
        } //不能同時選取圖片和視頻文件
    open var pgStrVideoCanOnlySelecteAOne: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.pg_str_video_choose_tip_message); else field
        } //視頻只能選擇一個
    open var pgStrTheMaxSelectedOverd: String? = null
        get() {
            return if (field == null) AlbumConfig.getString(R.string.pg_str_at_best); else field
        } //最多只能选择 %1$d 张圖片
}