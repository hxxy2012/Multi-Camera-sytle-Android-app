package com.example.camerastyle.data.model

/**
 * 相机风格参数配置
 *
 * @property brightness 亮度 (0.0-2.0)
 * @property contrast 对比度 (0.0-2.0)
 * @property saturation 饱和度 (0.0-2.0)
 * @property vibrance 自然饱和度 (0.0-2.0)
 * @property warmth 色温 (0.5-1.5)
 * @property blackAndWhite 黑白模式
 * @property redBoost 红色增强（徕卡特色）
 * @property sharpness 锐化 (0.0-2.0)
 * @property grain 颗粒感 (0.0-0.05)
 * @property softness 柔和度 (0.0-2.0)
 * @property smoothness 平滑度 (0.0-2.0)
 * @property punch 冲击力 (0.0-2.0)
 * @property desaturate 去饱和度 (0.0-1.0)
 * @property colorShift 色彩偏移
 * @property midtone 中间调 (0.0-2.0)
 */
data class StyleParams(
    val brightness: Float = 1.0f,
    val contrast: Float = 1.0f,
    val saturation: Float = 1.0f,
    val vibrance: Float = 1.0f,
    val warmth: Float = 1.0f,
    val blackAndWhite: Boolean = false,
    val redBoost: Float = 1.0f,
    val sharpness: Float = 1.0f,
    val grain: Float = 0f,
    val softness: Float = 1.0f,
    val smoothness: Float = 1.0f,
    val punch: Float = 1.0f,
    val desaturate: Float = 0f,
    val colorShift: Boolean = false,
    val midtone: Float = 1.0f
)
