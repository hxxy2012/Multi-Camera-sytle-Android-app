package com.example.camerastyle.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.camerastyle.data.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 保存图片的用例
 */
class SaveImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    /**
     * 执行保存图片
     * @param bitmap 要保存的图片
     * @param styleName 风格名称
     * @return Flow<Result<Uri>> 保存结果，包含文件URI
     */
    operator fun invoke(bitmap: Bitmap, styleName: String): Flow<Result<Uri>> = flow {
        try {
            val uri = imageRepository.saveImage(bitmap, styleName)

            if (uri != null) {
                emit(Result.success(uri))
            } else {
                emit(Result.failure(Exception("保存图片失败")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * 获取保存路径显示文本
     */
    fun getSavePathDisplay(): String {
        return imageRepository.getSavePathDisplay()
    }
}
