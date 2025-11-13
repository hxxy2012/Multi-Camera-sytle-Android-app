package com.example.camerastyle.ui.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.camerastyle.ui.component.*
import com.example.camerastyle.ui.theme.GradientEnd
import com.example.camerastyle.ui.theme.GradientStart
import com.example.camerastyle.ui.viewmodel.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

/**
 * 主界面
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 权限状态
    val permissionsState = rememberMultiplePermissionsState(
        permissions = buildList {
            add(android.Manifest.permission.CAMERA)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                add(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    )

    // 图片选择器
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val bitmap = loadBitmapFromUri(context.contentResolver.openInputStream(it))
                    if (bitmap != null) {
                        viewModel.setOriginalImage(bitmap)
                    } else {
                        snackbarHostState.showSnackbar(
                            message = "加载图片失败，请选择其他图片",
                            duration = SnackbarDuration.Short
                        )
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar(
                        message = "加载图片出错: ${e.message}",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    // 显示错误消息
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    // 显示成功消息
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "专业相机风格",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Leica · Hasselblad · Zeiss · Ricoh GR",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 图片选择区域
                item {
                    ImageSelectionSection(
                        onPickFromGallery = {
                            if (permissionsState.allPermissionsGranted) {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } else {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        }
                    )
                }

                // 图片预览区域
                item {
                    if (uiState.showCompareMode && uiState.originalBitmap != null) {
                        CompareView(
                            originalBitmap = uiState.originalBitmap,
                            processedBitmap = uiState.processedBitmap
                        )
                    } else {
                        ImagePreview(
                            bitmap = uiState.processedBitmap ?: uiState.originalBitmap
                        )
                    }
                }

                // 风格选择区域
                if (uiState.originalBitmap != null) {
                    item {
                        Text(
                            text = "选择风格",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    uiState.styles.forEach { (brand, styles) ->
                        item {
                            BrandHeader(
                                brandName = brand,
                                brandIcon = styles.firstOrNull()?.brandIcon ?: ""
                            )
                        }

                        items(styles) { style ->
                            StyleCard(
                                style = style,
                                isSelected = style.id == uiState.selectedStyle?.id,
                                onClick = { viewModel.selectStyle(style) }
                            )
                        }
                    }
                }

                // 底部操作栏
                if (uiState.processedBitmap != null) {
                    item {
                        ActionButtons(
                            onCompare = { viewModel.toggleCompareMode() },
                            onSave = { viewModel.saveImage() },
                            showCompareMode = uiState.showCompareMode
                        )
                    }
                }

                // 底部间距
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // 加载遮罩
            LoadingOverlay(
                isLoading = uiState.isProcessing,
                message = "正在应用风格..."
            )

            LoadingOverlay(
                isLoading = uiState.isSaving,
                message = "正在保存图片..."
            )
        }
    }
}

/**
 * 图片选择区域
 */
@Composable
fun ImageSelectionSection(
    onPickFromGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "选择照片",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onPickFromGallery,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("从相册选择")
            }
        }
    }
}

/**
 * 操作按钮区域
 */
@Composable
fun ActionButtons(
    onCompare: () -> Unit,
    onSave: () -> Unit,
    showCompareMode: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 对比按钮
            OutlinedButton(
                onClick = onCompare,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (showCompareMode) "隐藏对比" else "显示对比")
            }

            // 保存按钮
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("保存图片")
            }
        }
    }
}

/**
 * 从URI加载Bitmap
 * @param inputStream 输入流
 * @return 解码后的Bitmap，失败返回null
 */
private suspend fun loadBitmapFromUri(inputStream: InputStream?): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            inputStream?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        } catch (e: OutOfMemoryError) {
            timber.log.Timber.e(e, "OOM while loading bitmap")
            null
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Error loading bitmap")
            null
        }
    }
}
