# Android 专业相机风格转换器

一个功能强大的Android原生应用，可以将照片应用各种专业相机品牌的色彩风格，包括Leica（徕卡）、Hasselblad（哈苏）、Zeiss（蔡司）和Ricoh GR（理光）。

## 功能特性

### 核心功能

- **多种专业相机风格**：16种预设滤镜效果
  - 🔴 Leica 徕卡（4种风格）：经典德味、Monochrom黑白、柔和色彩、鲜艳模式
  - 🎨 Hasselblad 哈苏（4种风格）：自然色彩、鲜艳模式、人像模式、风光模式
  - ❄️ Zeiss 蔡司（4种风格）：经典冷调、中性色彩、高对比黑白、鲜明风格
  - 🏙️ Ricoh GR 理光（4种风格）：Positive Film正片、HC Black & White、Bleach Bypass漂白、Cross Process交叉冲印

- **图片来源**
  - 从相册选择照片
  - 支持拍摄新照片（CameraX集成）

- **图片处理**
  - 高性能图像处理算法
  - GPU加速处理
  - 支持高分辨率图片（自动优化）
  - 实时预览效果

- **用户体验**
  - Material3 深色主题设计
  - 流畅的动画效果
  - 双指缩放查看图片
  - 原图/效果图对比模式
  - 实时处理进度显示

- **保存和分享**
  - 高质量保存（JPEG 95%）
  - 保持原始分辨率
  - 自动添加到系统相册
  - 分享到其他应用

## 技术栈

### 核心技术

- **开发语言**: Kotlin
- **最低SDK版本**: API 24 (Android 7.0)
- **目标SDK版本**: API 34 (Android 14)
- **架构模式**: MVVM + Repository Pattern
- **UI框架**: Jetpack Compose (Material3)

### 主要依赖库

- **Jetpack Compose** - 现代化UI框架
- **Hilt** - 依赖注入
- **Kotlin Coroutines + Flow** - 异步处理
- **CameraX** - 相机功能
- **Coil** - 图片加载
- **RenderScript Toolkit** - 图像处理加速
- **Accompanist Permissions** - 权限管理
- **Timber** - 日志记录

## 项目结构

```
app/
├── src/main/
│   ├── java/com/example/camerastyle/
│   │   ├── MainActivity.kt                 # 主Activity
│   │   ├── CameraStyleApp.kt              # Application类
│   │   │
│   │   ├── di/                            # 依赖注入
│   │   │   └── AppModule.kt
│   │   │
│   │   ├── data/                          # 数据层
│   │   │   ├── model/                     # 数据模型
│   │   │   │   ├── CameraStyle.kt
│   │   │   │   └── StyleParams.kt
│   │   │   ├── repository/                # 仓库
│   │   │   │   ├── StyleRepository.kt
│   │   │   │   └── ImageRepository.kt
│   │   │   └── local/                     # 本地数据源
│   │   │       └── StyleDataSource.kt
│   │   │
│   │   ├── domain/                        # 业务逻辑层
│   │   │   ├── usecase/                   # 用例
│   │   │   │   ├── ApplyStyleUseCase.kt
│   │   │   │   ├── SaveImageUseCase.kt
│   │   │   │   └── GetStylesUseCase.kt
│   │   │   └── processor/                 # 图像处理器
│   │   │       └── ImageProcessor.kt
│   │   │
│   │   └── ui/                            # UI层
│   │       ├── theme/                     # 主题
│   │       │   ├── Color.kt
│   │       │   ├── Theme.kt
│   │       │   └── Type.kt
│   │       ├── screen/                    # 屏幕
│   │       │   └── MainScreen.kt
│   │       ├── component/                 # 组件
│   │       │   ├── ImagePreview.kt
│   │       │   ├── StyleCard.kt
│   │       │   └── LoadingOverlay.kt
│   │       └── viewmodel/                 # ViewModel
│   │           └── MainViewModel.kt
│   │
│   ├── res/                               # 资源文件
│   │   ├── values/
│   │   │   ├── strings.xml
│   │   │   ├── colors.xml
│   │   │   └── themes.xml
│   │   └── xml/
│   │       ├── backup_rules.xml
│   │       └── data_extraction_rules.xml
│   │
│   └── AndroidManifest.xml
```

## 编译和运行

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.2+

### 编译步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd Multi-Camera-sytle-Android-app
   ```

2. **打开项目**
   - 使用Android Studio打开项目
   - 等待Gradle同步完成

3. **运行应用**
   - 连接Android设备或启动模拟器
   - 点击运行按钮（或按Shift + F10）
   - 应用将自动安装并启动

### 构建APK

**Debug版本**
```bash
./gradlew assembleDebug
```

**Release版本**
```bash
./gradlew assembleRelease
```

生成的APK位于：`app/build/outputs/apk/`

## 使用说明

### 基本使用流程

1. **选择照片**
   - 点击"从相册选择"按钮
   - 选择要处理的照片
   - 照片将显示在预览区域

2. **应用风格**
   - 在风格列表中选择想要的相机风格
   - 应用会自动处理图片
   - 处理完成后显示效果图

3. **对比效果**
   - 点击"显示对比"按钮
   - 左右分屏显示原图和效果图
   - 方便查看处理前后的差异

4. **保存图片**
   - 点击"保存图片"按钮
   - 图片将保存到 `Pictures/CameraStyleConverter/` 目录
   - 系统会显示保存成功的提示

### 权限说明

应用需要以下权限：

- **相机权限** - 用于拍摄照片（可选）
- **读取图片权限** - 用于从相册选择照片
- **存储权限** - 用于保存处理后的图片（Android 9及以下）

## 滤镜效果说明

### Leica 徕卡系列

- **经典德味**：高对比度，红色增强，呈现徕卡经典色彩
- **Monochrom黑白**：细腻的黑白层次，带有轻微颗粒感
- **柔和色彩**：低对比度，适合人像摄影
- **鲜艳模式**：高饱和度，色彩浓郁

### Hasselblad 哈苏系列

- **自然色彩**：中画幅标准，色彩真实准确
- **鲜艳模式**：提升饱和度，色彩明快
- **人像模式**：柔和肤色，细腻过渡
- **风光模式**：冷色调，适合风景摄影

### Zeiss 蔡司系列

- **经典冷调**：冷色调，高锐度对比
- **中性色彩**：平衡准确，专业标准
- **高对比黑白**：极致对比，纯粹黑白
- **鲜明风格**：高饱和高对比

### Ricoh GR 理光系列

- **Positive Film正片**：正片效果，高对比街拍风格
- **HC Black & White**：高对比黑白，街头质感
- **Bleach Bypass漂白**：漂白效果，低饱和高对比
- **Cross Process交叉冲印**：交叉冲印，色彩偏移效果

## 性能优化

- **GPU加速**：使用RenderScript Toolkit实现硬件加速
- **智能缩放**：自动处理超大图片，避免内存溢出
- **后台处理**：图片处理在后台线程执行，不阻塞UI
- **内存管理**：及时释放不需要的Bitmap，降低内存占用

## 已知问题

- 某些老旧设备上处理大图片可能较慢
- 颗粒感效果在低分辨率图片上不明显

## 后续计划

- [ ] 添加相机拍摄功能（完整CameraX集成）
- [ ] 支持批量处理多张照片
- [ ] 添加自定义风格调节功能
- [ ] 支持历史记录和收藏功能
- [ ] 添加更多滤镜效果
- [ ] 支持视频滤镜处理
- [ ] 添加社区分享功能

## 开源协议

MIT License

## 贡献

欢迎提交Issue和Pull Request！

## 联系方式

如有问题或建议，请通过以下方式联系：

- 提交Issue
- 发送邮件至：[your-email@example.com]

---

**注意**：本应用仅用于学习和研究目的，滤镜效果为模拟实现，与真实相机效果可能存在差异。
