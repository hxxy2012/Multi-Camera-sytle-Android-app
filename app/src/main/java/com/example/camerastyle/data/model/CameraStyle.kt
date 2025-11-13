package com.example.camerastyle.data.model

/**
 * 相机风格数据模型
 *
 * @property id 唯一标识符
 * @property brand 相机品牌
 * @property brandIcon 品牌图标emoji
 * @property name 风格名称
 * @property description 风格描述
 * @property params 风格参数配置
 */
data class CameraStyle(
    val id: String,
    val brand: String,
    val brandIcon: String,
    val name: String,
    val description: String,
    val params: StyleParams
)

/**
 * 相机品牌枚举
 */
enum class CameraBrand(val displayName: String, val icon: String) {
    LEICA("Leica 徕卡", "🔴"),
    HASSELBLAD("Hasselblad 哈苏", "🎨"),
    ZEISS("Zeiss 蔡司", "❄️"),
    RICOH_GR("Ricoh GR 理光", "🏙️")
}
