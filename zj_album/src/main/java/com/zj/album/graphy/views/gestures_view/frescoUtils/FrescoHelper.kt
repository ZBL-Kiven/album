package com.zj.album.graphy.views.gestures_view.frescoUtils


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.facebook.binaryresource.FileBinaryResource
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.zj.album.graphy.views.gestures_view.frescoUtils.listener.LoadFrescoListener
import java.io.File
import java.lang.NullPointerException

object FrescoHelper {

    fun initFresco(context: Context, config: ImagePipelineConfig) {
        Fresco.initialize(context, config)
    }

    fun loadBigImage(context: Context, imageView: SubsamplingScaleImageView, imageUri: String, defaultId: Int, isLocal: Boolean, ir: ImageRequest? = null, onLoadStart: () -> Unit, onLoadEnd: () -> Unit, isGif: (url: String) -> Unit) {
        onLoadStart()
        val file = getCache(context, Uri.parse(imageUri))
        fun setImage(source: ImageSource) {
            imageView.post {
                onLoadEnd()
                imageView.setImage(source)
            }
        }

        fun rotateImage(file: File) {
            Thread {
                try {
                    filterGif(file, isGif) {
                        val bmpIn: Bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: throw NullPointerException()
                        val matrix = Matrix()
                        matrix.postRotate((ir?.rotationOptions?.forcedAngle ?: 0) * 1.0f)
                        val w = bmpIn.width
                        val h = bmpIn.height
                        val bmpOut = Bitmap.createBitmap(bmpIn, 0, 0, w, h, matrix, true)
                        setImage(ImageSource.bitmap(bmpOut))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }

        if (!isLocal) {
            if (file != null && file.exists()) {
                rotateImage(file)
            } else {
                val imageRequest = ir ?: ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUri)).setProgressiveRenderingEnabled(false).build()
                getFrescoImgProcessor(context, imageRequest, object : LoadFrescoListener {

                    override fun onSuccess(bitmap: Bitmap) {
                        val f = getCache(context, Uri.parse(ir?.sourceUri.toString()))
                        if (f != null && f.exists()) {
                            rotateImage(f)
                        } else {
                            setImage(ImageSource.resource(defaultId))
                        }
                    }

                    override fun onFail(failureCause: Throwable) {
                        setImage(ImageSource.resource(defaultId))
                    }
                })
            }
        } else {
            onLoadEnd()
            filterGif(file, isGif) {
                imageView.setImage(ImageSource.uri(imageUri.replace("file://", "")))
            }
        }
    }

    private fun filterGif(file: File?, isGif: (path: String) -> Unit, execute: (File?) -> Unit) {
        val path: String = file?.path ?: ""
        if (pathIsGif(path)) {
            isGif(path)
        } else {
            execute(file)
        }
    }

    private fun pathIsGif(localPath: String): Boolean {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bp: Bitmap? = null
        return try {
            bp = BitmapFactory.decodeFile(localPath, options)
            options.outMimeType == "image/gif"
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            if (bp?.isRecycled == false) bp.recycle()
        }
    }
}

/**
 * 图片是否已经存在了
 */
private fun isCached(context: Context, uri: Uri): Boolean {
    val imagePipeline = Fresco.getImagePipeline()
    val dataSource = imagePipeline.isInDiskCache(uri) ?: return false
    val imageRequest = ImageRequest.fromUri(uri)
    val cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest!!, context)
    val resource = ImagePipelineFactory.getInstance()
            .mainFileCache.getResource(cacheKey)
    return resource != null && dataSource.result != null && dataSource.result!!
}

/**
 * 本地缓存文件
 */
private fun getCache(context: Context, uri: Uri): File? {
    if (!isCached(context, uri))
        return null
    val imageRequest = ImageRequest.fromUri(uri)
    val cacheKey = DefaultCacheKeyFactory.getInstance()
            .getEncodedCacheKey(imageRequest!!, context)
    val resource = ImagePipelineFactory.getInstance()
            .mainFileCache.getResource(cacheKey)
    return (resource as FileBinaryResource).file
}

private fun getFrescoImgProcessor(context: Context, ir: ImageRequest, listener: LoadFrescoListener) {
    val imagePipeline = Fresco.getImagePipeline()
    val dataSource = imagePipeline.fetchDecodedImage(ir, context)
    dataSource.subscribe(object : BaseBitmapDataSubscriber() {

        override fun onNewResultImpl(bitmap: Bitmap?) {
            listener.onSuccess(bitmap)
        }

        override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
            listener.onFail(dataSource.failureCause)
            dataSource.close()
        }
    }, CallerThreadExecutor.getInstance())
}
