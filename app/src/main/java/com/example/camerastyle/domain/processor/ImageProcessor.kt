package com.example.camerastyle.domain.processor

import android.graphics.Bitmap
import android.graphics.Color
import com.example.camerastyle.data.model.StyleParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * 图像处理器 - 实现各种滤镜效果
 */
@Singleton
class ImageProcessor @Inject constructor() {

    /**
     * 应用风格到图片
     * @param bitmap 原始图片
     * @param params 风格参数
     * @return 处理后的图片
     */
    suspend fun applyStyle(bitmap: Bitmap, params: StyleParams): Bitmap = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()

        val width = bitmap.width
        val height = bitmap.height
        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // 获取所有像素
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // 处理每个像素
        for (i in pixels.indices) {
            pixels[i] = processPixel(pixels[i], params, i)
        }

        // 设置处理后的像素
        resultBitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        val endTime = System.currentTimeMillis()
        Timber.d("Image processing completed in ${endTime - startTime}ms")

        resultBitmap
    }

    /**
     * 处理单个像素
     */
    private fun processPixel(pixel: Int, params: StyleParams, index: Int): Int {
        var r = Color.red(pixel).toFloat()
        var g = Color.green(pixel).toFloat()
        var b = Color.blue(pixel).toFloat()
        val a = Color.alpha(pixel)

        // 1. 亮度调整
        if (params.brightness != 1.0f) {
            r *= params.brightness
            g *= params.brightness
            b *= params.brightness
        }

        // 2. 对比度调整
        if (params.contrast != 1.0f) {
            r = ((r / 255.0f - 0.5f) * params.contrast + 0.5f) * 255.0f
            g = ((g / 255.0f - 0.5f) * params.contrast + 0.5f) * 255.0f
            b = ((b / 255.0f - 0.5f) * params.contrast + 0.5f) * 255.0f
        }

        // 3. 中间调调整
        if (params.midtone != 1.0f) {
            val luminance = 0.299f * r + 0.587f * g + 0.114f * b
            if (luminance in 64.0f..192.0f) { // 中间调范围
                val adjustment = params.midtone
                r *= adjustment
                g *= adjustment
                b *= adjustment
            }
        }

        // 4. 色温调整
        if (params.warmth != 1.0f) {
            r *= params.warmth
            b /= params.warmth
        }

        // 5. 饱和度调整
        if (params.saturation != 1.0f || params.vibrance != 1.0f) {
            val gray = 0.299f * r + 0.587f * g + 0.114f * b

            // 基础饱和度
            if (params.saturation != 1.0f) {
                r = gray + (r - gray) * params.saturation
                g = gray + (g - gray) * params.saturation
                b = gray + (b - gray) * params.saturation
            }

            // 自然饱和度（vibrance）- 对低饱和度像素影响更大
            if (params.vibrance != 1.0f) {
                val currentSaturation = calculateSaturation(r, g, b)
                val vibranceAdjustment = (1.0f - currentSaturation) * (params.vibrance - 1.0f) + 1.0f
                r = gray + (r - gray) * vibranceAdjustment
                g = gray + (g - gray) * vibranceAdjustment
                b = gray + (b - gray) * vibranceAdjustment
            }
        }

        // 6. 去饱和度（用于Bleach Bypass效果）
        if (params.desaturate > 0f) {
            val gray = 0.299f * r + 0.587f * g + 0.114f * b
            r = gray + (r - gray) * (1 - params.desaturate)
            g = gray + (g - gray) * (1 - params.desaturate)
            b = gray + (b - gray) * (1 - params.desaturate)
        }

        // 7. 黑白转换
        if (params.blackAndWhite) {
            val gray = 0.299f * r + 0.587f * g + 0.114f * b
            r = gray
            g = gray
            b = gray
        }

        // 8. 徕卡红色增强
        if (params.redBoost > 1.0f && r > g && r > b) {
            r *= params.redBoost
        }

        // 9. 色彩偏移（Cross Process效果）
        if (params.colorShift) {
            val temp = r
            r = min(255f, r * 1.1f + b * 0.1f)
            g = min(255f, g * 0.95f)
            b = min(255f, b * 1.15f + temp * 0.05f)
        }

        // 10. 冲击力（punch）增强
        if (params.punch > 1.0f) {
            val luminance = 0.299f * r + 0.587f * g + 0.114f * b
            val punchFactor = params.punch
            r = luminance + (r - luminance) * punchFactor
            g = luminance + (g - luminance) * punchFactor
            b = luminance + (b - luminance) * punchFactor
        }

        // 11. 柔和度
        if (params.softness > 1.0f) {
            val softFactor = 1.0f / params.softness
            r = r * 0.7f + 128f * 0.3f * (1 - softFactor)
            g = g * 0.7f + 128f * 0.3f * (1 - softFactor)
            b = b * 0.7f + 128f * 0.3f * (1 - softFactor)
        }

        // 12. 颗粒感
        if (params.grain > 0f) {
            val noise = (Random(index.toLong()).nextFloat() - 0.5f) * params.grain * 255.0f
            r += noise
            g += noise
            b += noise
        }

        // 13. 锐化（简化版）
        if (params.sharpness > 1.0f) {
            val sharpAmount = (params.sharpness - 1.0f) * 0.3f
            r += (r - 128f) * sharpAmount
            g += (g - 128f) * sharpAmount
            b += (b - 128f) * sharpAmount
        }

        // 限制RGB值在0-255范围内
        r = clamp(r, 0f, 255f)
        g = clamp(g, 0f, 255f)
        b = clamp(b, 0f, 255f)

        return Color.argb(a, r.toInt(), g.toInt(), b.toInt())
    }

    /**
     * 计算像素的饱和度
     */
    private fun calculateSaturation(r: Float, g: Float, b: Float): Float {
        val max = max(r, max(g, b))
        val min = min(r, min(g, b))
        return if (max > 0) (max - min) / max else 0f
    }

    /**
     * 限制数值在指定范围内
     */
    private fun clamp(value: Float, min: Float, max: Float): Float {
        return when {
            value < min -> min
            value > max -> max
            else -> value
        }
    }

    /**
     * 检查图片是否过大
     */
    fun isImageTooLarge(bitmap: Bitmap): Boolean {
        val sizeInBytes = bitmap.byteCount
        val sizeInMB = sizeInBytes / (1024 * 1024)
        return sizeInMB > 50
    }

    /**
     * 缩小图片以避免OOM
     */
    fun resizeBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int = 4096): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val scale = if (width > height) {
            maxDimension.toFloat() / width
        } else {
            maxDimension.toFloat() / height
        }

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        Timber.d("Resizing bitmap from ${width}x${height} to ${newWidth}x${newHeight}")
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
