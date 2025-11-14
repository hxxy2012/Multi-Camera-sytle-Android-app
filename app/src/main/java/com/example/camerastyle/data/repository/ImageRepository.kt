package com.example.camerastyle.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 图片仓库 - 处理图片的保存和加载
 */
@Singleton
class ImageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val QUALITY = 95  // JPEG质量
        const val DIRECTORY = "CameraStyleConverter"
    }

    /**
     * 保存处理后的图片到相册
     * @param bitmap 要保存的图片
     * @param styleName 风格名称
     * @return 保存的文件URI，失败返回null
     */
    suspend fun saveImage(bitmap: Bitmap, styleName: String): Uri? = withContext(Dispatchers.IO) {
        try {
            val fileName = generateFileName(styleName)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10及以上使用MediaStore API
                saveImageViaMediaStore(bitmap, fileName)
            } else {
                // Android 9及以下使用传统文件系统
                saveImageViaFileSystem(bitmap, fileName)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to save image")
            null
        }
    }

    /**
     * 使用MediaStore API保存图片（Android 10+）
     */
    private fun saveImageViaMediaStore(bitmap: Bitmap, fileName: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$DIRECTORY")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return uri?.let { imageUri ->
            try {
                resolver.openOutputStream(imageUri)?.use { outputStream ->
                    val success = bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
                    if (success) {
                        Timber.d("Image saved successfully via MediaStore: $imageUri")
                        imageUri
                    } else {
                        Timber.e("Failed to compress bitmap")
                        // 删除失败的条目
                        resolver.delete(imageUri, null, null)
                        null
                    }
                } ?: run {
                    Timber.e("Failed to open output stream")
                    // 删除失败的条目
                    resolver.delete(imageUri, null, null)
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Error writing image to MediaStore")
                // 删除失败的条目
                resolver.delete(imageUri, null, null)
                null
            }
        }
    }

    /**
     * 使用传统文件系统保存图片（Android 9及以下）
     */
    private fun saveImageViaFileSystem(bitmap: Bitmap, fileName: String): Uri? {
        return try {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val appDir = File(picturesDir, DIRECTORY)

            if (!appDir.exists()) {
                val created = appDir.mkdirs()
                if (!created && !appDir.exists()) {
                    Timber.e("Failed to create directory: ${appDir.absolutePath}")
                    return null
                }
            }

            val file = File(appDir, fileName)
            FileOutputStream(file).use { outputStream ->
                val success = bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
                if (!success) {
                    Timber.e("Failed to compress bitmap to file")
                    file.delete() // 删除失败的文件
                    return null
                }
            }

            // 通知系统媒体库更新
            val uri = Uri.fromFile(file)
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DATA, file.absolutePath)
            }
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            Timber.d("Image saved successfully via file system: $uri")
            uri
        } catch (e: Exception) {
            Timber.e(e, "Error saving image via file system")
            null
        }
    }

    /**
     * 生成文件名
     * 移除特殊字符以确保文件系统兼容性
     */
    private fun generateFileName(styleName: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        // 移除文件名中的特殊字符，只保留字母、数字、中文和下划线
        val sanitizedStyleName = styleName.replace(Regex("[^\\w\\u4e00-\\u9fa5]"), "_")
        return "IMG_${sanitizedStyleName}_${timestamp}.jpg"
    }

    /**
     * 获取保存路径的显示文本
     */
    fun getSavePathDisplay(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "Pictures/$DIRECTORY"
        } else {
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/$DIRECTORY"
        }
    }
}
