package com.example.camerastyle.domain.usecase

import android.graphics.Bitmap
import com.example.camerastyle.data.model.CameraStyle
import com.example.camerastyle.domain.processor.ImageProcessor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 应用风格到图片的用例
 */
class ApplyStyleUseCase @Inject constructor(
    private val imageProcessor: ImageProcessor
) {
    /**
     * 执行风格应用
     * @param bitmap 原始图片
     * @param style 要应用的风格
     * @return Flow<Result<Bitmap>> 处理结果
     */
    operator fun invoke(bitmap: Bitmap, style: CameraStyle): Flow<Result<Bitmap>> = flow {
        try {
            // 检查图片是否过大
            if (imageProcessor.isImageTooLarge(bitmap)) {
                emit(Result.failure(Exception("图片文件过大（超过50MB），请选择较小的图片")))
                return@flow
            }

            // 如果图片分辨率过高，先缩放
            val processedBitmap = imageProcessor.resizeBitmapIfNeeded(bitmap)

            // 应用风格
            val resultBitmap = imageProcessor.applyStyle(processedBitmap, style.params)

            emit(Result.success(resultBitmap))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
