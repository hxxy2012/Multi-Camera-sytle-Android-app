package com.example.camerastyle.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camerastyle.data.model.CameraStyle
import com.example.camerastyle.domain.usecase.ApplyStyleUseCase
import com.example.camerastyle.domain.usecase.GetStylesUseCase
import com.example.camerastyle.domain.usecase.SaveImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI状态
 */
data class MainUiState(
    val originalBitmap: Bitmap? = null,
    val processedBitmap: Bitmap? = null,
    val styles: Map<String, List<CameraStyle>> = emptyMap(),
    val selectedStyle: CameraStyle? = null,
    val isProcessing: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showCompareMode: Boolean = false
)

/**
 * 主ViewModel
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getStylesUseCase: GetStylesUseCase,
    private val applyStyleUseCase: ApplyStyleUseCase,
    private val saveImageUseCase: SaveImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadStyles()
    }

    /**
     * 加载所有风格
     */
    private fun loadStyles() {
        viewModelScope.launch {
            getStylesUseCase.getGroupedByBrand()
                .catch { e ->
                    Timber.e(e, "Failed to load styles")
                    _uiState.update { it.copy(errorMessage = "加载风格失败: ${e.message}") }
                }
                .collect { styles ->
                    _uiState.update { it.copy(styles = styles) }
                }
        }
    }

    /**
     * 设置原图
     */
    fun setOriginalImage(bitmap: Bitmap) {
        // 释放旧的bitmap
        val oldOriginal = _uiState.value.originalBitmap
        val oldProcessed = _uiState.value.processedBitmap

        _uiState.update {
            it.copy(
                originalBitmap = bitmap,
                processedBitmap = null,
                selectedStyle = null,
                errorMessage = null
            )
        }

        // 在更新状态后回收旧的bitmap
        oldOriginal?.recycle()
        oldProcessed?.recycle()
    }

    /**
     * 选择风格
     */
    fun selectStyle(style: CameraStyle) {
        val originalBitmap = _uiState.value.originalBitmap

        if (originalBitmap == null) {
            _uiState.update { it.copy(errorMessage = "请先选择或拍摄照片") }
            return
        }

        _uiState.update {
            it.copy(
                selectedStyle = style,
                isProcessing = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            applyStyleUseCase(originalBitmap, style)
                .catch { e ->
                    Timber.e(e, "Failed to apply style")
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            errorMessage = "应用风格失败: ${e.message}"
                        )
                    }
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { processedBitmap ->
                            // 释放旧的处理后的bitmap
                            val oldProcessed = _uiState.value.processedBitmap

                            _uiState.update {
                                it.copy(
                                    processedBitmap = processedBitmap,
                                    isProcessing = false,
                                    errorMessage = null
                                )
                            }

                            // 回收旧bitmap
                            oldProcessed?.recycle()
                        },
                        onFailure = { e ->
                            Timber.e(e, "Failed to apply style")
                            _uiState.update {
                                it.copy(
                                    isProcessing = false,
                                    errorMessage = e.message ?: "应用风格失败"
                                )
                            }
                        }
                    )
                }
        }
    }

    /**
     * 保存图片
     */
    fun saveImage() {
        val processedBitmap = _uiState.value.processedBitmap
        val styleName = _uiState.value.selectedStyle?.name

        if (processedBitmap == null) {
            _uiState.update { it.copy(errorMessage = "没有可保存的图片") }
            return
        }

        if (styleName == null) {
            _uiState.update { it.copy(errorMessage = "未选择风格") }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            saveImageUseCase(processedBitmap, styleName)
                .catch { e ->
                    Timber.e(e, "Failed to save image")
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "保存失败: ${e.message}"
                        )
                    }
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { uri ->
                            val savePath = saveImageUseCase.getSavePathDisplay()
                            _uiState.update {
                                it.copy(
                                    isSaving = false,
                                    successMessage = "图片已保存到 $savePath",
                                    errorMessage = null
                                )
                            }
                        },
                        onFailure = { e ->
                            Timber.e(e, "Failed to save image")
                            _uiState.update {
                                it.copy(
                                    isSaving = false,
                                    errorMessage = e.message ?: "保存失败"
                                )
                            }
                        }
                    )
                }
        }
    }

    /**
     * 切换对比模式
     */
    fun toggleCompareMode() {
        _uiState.update { it.copy(showCompareMode = !it.showCompareMode) }
    }

    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * 清除成功消息
     */
    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    /**
     * 重置所有状态
     */
    fun reset() {
        // 释放所有bitmap
        val oldOriginal = _uiState.value.originalBitmap
        val oldProcessed = _uiState.value.processedBitmap

        _uiState.update {
            MainUiState(styles = it.styles)
        }

        // 回收bitmap
        oldOriginal?.recycle()
        oldProcessed?.recycle()
    }

    /**
     * 清理资源
     */
    override fun onCleared() {
        super.onCleared()
        // ViewModel被销毁时释放所有bitmap
        _uiState.value.originalBitmap?.recycle()
        _uiState.value.processedBitmap?.recycle()
        Timber.d("MainViewModel cleared, bitmaps recycled")
    }
}
