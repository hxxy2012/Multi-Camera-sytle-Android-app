package com.example.camerastyle.data.repository

import com.example.camerastyle.data.local.StyleDataSource
import com.example.camerastyle.data.model.CameraBrand
import com.example.camerastyle.data.model.CameraStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 风格仓库 - 提供风格数据的访问接口
 */
@Singleton
class StyleRepository @Inject constructor(
    private val styleDataSource: StyleDataSource
) {

    /**
     * 获取所有风格
     */
    fun getAllStyles(): Flow<List<CameraStyle>> = flow {
        emit(styleDataSource.getAllStyles())
    }

    /**
     * 根据品牌获取风格
     */
    fun getStylesByBrand(brand: CameraBrand): Flow<List<CameraStyle>> = flow {
        emit(styleDataSource.getStylesByBrand(brand))
    }

    /**
     * 根据ID获取风格
     */
    fun getStyleById(id: String): Flow<CameraStyle?> = flow {
        emit(styleDataSource.getStyleById(id))
    }

    /**
     * 获取按品牌分组的风格
     */
    fun getStylesGroupedByBrand(): Flow<Map<String, List<CameraStyle>>> = flow {
        val allStyles = styleDataSource.getAllStyles()
        emit(allStyles.groupBy { it.brand })
    }
}
