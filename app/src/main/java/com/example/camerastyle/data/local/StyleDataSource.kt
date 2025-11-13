package com.example.camerastyle.data.local

import com.example.camerastyle.data.model.CameraBrand
import com.example.camerastyle.data.model.CameraStyle
import com.example.camerastyle.data.model.StyleParams
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 风格数据源 - 提供所有预设的相机风格
 */
@Singleton
class StyleDataSource @Inject constructor() {

    /**
     * 获取所有可用的相机风格
     */
    fun getAllStyles(): List<CameraStyle> = allStyles

    /**
     * 根据品牌获取风格列表
     */
    fun getStylesByBrand(brand: CameraBrand): List<CameraStyle> {
        return allStyles.filter { it.brand == brand.displayName }
    }

    /**
     * 根据ID获取风格
     */
    fun getStyleById(id: String): CameraStyle? {
        return allStyles.find { it.id == id }
    }

    companion object {
        /**
         * 所有预设风格列表
         */
        private val allStyles = listOf(
            // ============ Leica 徕卡 (4种风格) ============
            CameraStyle(
                id = "leica_standard",
                brand = CameraBrand.LEICA.displayName,
                brandIcon = CameraBrand.LEICA.icon,
                name = "经典德味",
                description = "高对比度，经典红色增强",
                params = StyleParams(
                    brightness = 1.0f,
                    contrast = 1.3f,
                    saturation = 1.2f,
                    vibrance = 1.25f,
                    warmth = 1.05f,
                    redBoost = 1.3f,
                    midtone = 1.05f
                )
            ),
            CameraStyle(
                id = "leica_monochrom",
                brand = CameraBrand.LEICA.displayName,
                brandIcon = CameraBrand.LEICA.icon,
                name = "Monochrom 黑白",
                description = "徕卡经典黑白，细腻层次",
                params = StyleParams(
                    brightness = 1.05f,
                    contrast = 1.4f,
                    saturation = 0f,
                    vibrance = 0f,
                    warmth = 1.0f,
                    blackAndWhite = true,
                    grain = 0.02f
                )
            ),
            CameraStyle(
                id = "leica_soft",
                brand = CameraBrand.LEICA.displayName,
                brandIcon = CameraBrand.LEICA.icon,
                name = "柔和色彩",
                description = "低对比柔和，人像优选",
                params = StyleParams(
                    brightness = 1.05f,
                    contrast = 0.95f,
                    saturation = 1.0f,
                    vibrance = 1.05f,
                    warmth = 1.08f,
                    softness = 1.1f
                )
            ),
            CameraStyle(
                id = "leica_vivid",
                brand = CameraBrand.LEICA.displayName,
                brandIcon = CameraBrand.LEICA.icon,
                name = "鲜艳模式",
                description = "饱和度提升，色彩浓郁",
                params = StyleParams(
                    brightness = 1.02f,
                    contrast = 1.2f,
                    saturation = 1.4f,
                    vibrance = 1.35f,
                    warmth = 1.0f,
                    redBoost = 1.2f
                )
            ),

            // ============ Hasselblad 哈苏 (4种风格) ============
            CameraStyle(
                id = "hasselblad_natural",
                brand = CameraBrand.HASSELBLAD.displayName,
                brandIcon = CameraBrand.HASSELBLAD.icon,
                name = "自然色彩",
                description = "中画幅标准，色彩真实",
                params = StyleParams(
                    brightness = 1.02f,
                    contrast = 1.08f,
                    saturation = 1.05f,
                    vibrance = 1.1f,
                    warmth = 1.0f,
                    smoothness = 1.15f
                )
            ),
            CameraStyle(
                id = "hasselblad_vivid",
                brand = CameraBrand.HASSELBLAD.displayName,
                brandIcon = CameraBrand.HASSELBLAD.icon,
                name = "鲜艳模式",
                description = "提升饱和度，色彩明快",
                params = StyleParams(
                    brightness = 1.03f,
                    contrast = 1.15f,
                    saturation = 1.3f,
                    vibrance = 1.25f,
                    warmth = 1.02f,
                    smoothness = 1.1f
                )
            ),
            CameraStyle(
                id = "hasselblad_portrait",
                brand = CameraBrand.HASSELBLAD.displayName,
                brandIcon = CameraBrand.HASSELBLAD.icon,
                name = "人像模式",
                description = "柔和肤色，细腻过渡",
                params = StyleParams(
                    brightness = 1.05f,
                    contrast = 1.0f,
                    saturation = 0.95f,
                    vibrance = 1.0f,
                    warmth = 1.1f,
                    softness = 1.2f
                )
            ),
            CameraStyle(
                id = "hasselblad_landscape",
                brand = CameraBrand.HASSELBLAD.displayName,
                brandIcon = CameraBrand.HASSELBLAD.icon,
                name = "风光模式",
                description = "冷色调，适合风景",
                params = StyleParams(
                    brightness = 1.0f,
                    contrast = 1.15f,
                    saturation = 1.15f,
                    vibrance = 1.2f,
                    warmth = 0.95f,
                    smoothness = 1.1f
                )
            ),

            // ============ Zeiss 蔡司 (4种风格) ============
            CameraStyle(
                id = "zeiss_cold",
                brand = CameraBrand.ZEISS.displayName,
                brandIcon = CameraBrand.ZEISS.icon,
                name = "经典冷调",
                description = "冷色调，高锐度对比",
                params = StyleParams(
                    brightness = 1.0f,
                    contrast = 1.35f,
                    saturation = 1.15f,
                    vibrance = 1.2f,
                    warmth = 0.88f,
                    sharpness = 1.4f
                )
            ),
            CameraStyle(
                id = "zeiss_neutral",
                brand = CameraBrand.ZEISS.displayName,
                brandIcon = CameraBrand.ZEISS.icon,
                name = "中性色彩",
                description = "平衡准确，专业标准",
                params = StyleParams(
                    brightness = 1.0f,
                    contrast = 1.15f,
                    saturation = 1.05f,
                    vibrance = 1.1f,
                    warmth = 1.0f,
                    sharpness = 1.25f
                )
            ),
            CameraStyle(
                id = "zeiss_bw",
                brand = CameraBrand.ZEISS.displayName,
                brandIcon = CameraBrand.ZEISS.icon,
                name = "高对比黑白",
                description = "极致对比，纯粹黑白",
                params = StyleParams(
                    brightness = 1.0f,
                    contrast = 1.5f,
                    saturation = 0f,
                    vibrance = 0f,
                    warmth = 1.0f,
                    blackAndWhite = true,
                    sharpness = 1.3f
                )
            ),
            CameraStyle(
                id = "zeiss_vivid",
                brand = CameraBrand.ZEISS.displayName,
                brandIcon = CameraBrand.ZEISS.icon,
                name = "鲜明风格",
                description = "高饱和高对比",
                params = StyleParams(
                    brightness = 1.02f,
                    contrast = 1.3f,
                    saturation = 1.35f,
                    vibrance = 1.3f,
                    warmth = 0.95f,
                    sharpness = 1.3f
                )
            ),

            // ============ Ricoh GR 理光 (4种风格) ============
            CameraStyle(
                id = "ricoh_positive",
                brand = CameraBrand.RICOH_GR.displayName,
                brandIcon = CameraBrand.RICOH_GR.icon,
                name = "Positive Film 正片",
                description = "正片效果，高对比街拍",
                params = StyleParams(
                    brightness = 1.05f,
                    contrast = 1.4f,
                    saturation = 1.3f,
                    vibrance = 1.35f,
                    warmth = 1.0f,
                    punch = 1.3f
                )
            ),
            CameraStyle(
                id = "ricoh_hc_bw",
                brand = CameraBrand.RICOH_GR.displayName,
                brandIcon = CameraBrand.RICOH_GR.icon,
                name = "HC Black & White",
                description = "高对比黑白，街头质感",
                params = StyleParams(
                    brightness = 1.03f,
                    contrast = 1.45f,
                    saturation = 0f,
                    vibrance = 0f,
                    warmth = 1.0f,
                    blackAndWhite = true,
                    grain = 0.015f
                )
            ),
            CameraStyle(
                id = "ricoh_bleach",
                brand = CameraBrand.RICOH_GR.displayName,
                brandIcon = CameraBrand.RICOH_GR.icon,
                name = "Bleach Bypass 漂白",
                description = "漂白效果，低饱和高对比",
                params = StyleParams(
                    brightness = 1.0f,
                    contrast = 1.35f,
                    saturation = 0.6f,
                    vibrance = 0.7f,
                    warmth = 0.98f,
                    desaturate = 0.4f
                )
            ),
            CameraStyle(
                id = "ricoh_cross",
                brand = CameraBrand.RICOH_GR.displayName,
                brandIcon = CameraBrand.RICOH_GR.icon,
                name = "Cross Process 交叉冲印",
                description = "交叉冲印，色彩偏移",
                params = StyleParams(
                    brightness = 1.05f,
                    contrast = 1.25f,
                    saturation = 1.2f,
                    vibrance = 1.25f,
                    warmth = 1.1f,
                    colorShift = true
                )
            )
        )
    }
}
