package com.example.camerastyle.domain.usecase

import com.example.camerastyle.data.model.CameraStyle
import com.example.camerastyle.data.repository.StyleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取风格列表的用例
 */
class GetStylesUseCase @Inject constructor(
    private val styleRepository: StyleRepository
) {
    /**
     * 执行获取所有风格
     */
    operator fun invoke(): Flow<List<CameraStyle>> {
        return styleRepository.getAllStyles()
    }

    /**
     * 获取按品牌分组的风格
     */
    fun getGroupedByBrand(): Flow<Map<String, List<CameraStyle>>> {
        return styleRepository.getStylesGroupedByBrand()
    }
}
