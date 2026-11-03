# 影视追踪 MovieTracker

《Android 开发技术》课程设计项目 —— **影视追踪 App + 桌面小组件**。

- 技术栈：Java + Android 原生（无跨平台框架）
- 包名：`com.chenzhe.movietracker`
- minSdk 26 / targetSdk 37 / compileSdk 37
- 构建：AGP 9.5.0 + Gradle 9.8.2（JDK 17 字节码）

## 当前进度

| 步骤 | 内容 | 状态 |
| --- | --- | --- |
| 1 | 工程骨架 + 依赖锁定 + 首页占位（可编译运行） | ✅ |
| 2 | 业务功能（列表 / 搜索 / 详情 / 收藏） | ⏳ 待接入 |
| 3 | 桌面小组件（AppWidgetProvider） | ⏳ 待接入 |

## 目录结构

```
MovieTracker/
├── app/
│   └── src/main/
│       ├── java/com/chenzhe/movietracker/MainActivity.java   # 首页占位
│       ├── res/layout/activity_main.xml
│       └── AndroidManifest.xml
├── gradle/libs.versions.toml      # 版本目录：所有依赖版本在此锁定
├── build.gradle                   # 顶层构建脚本
├── settings.gradle
└── gradle.properties
```

## 依赖清单（版本见 `gradle/libs.versions.toml`）

- **UI**：AndroidX Core、AppCompat、Material Components、ConstraintLayout
- **Jetpack**：ViewModel、LiveData、Room（runtime + 注解处理器）、WorkManager
- **网络**：Retrofit、OkHttp（BOM 统一版本）、Gson converter、OkHttp 日志拦截器
- **图片**：Glide（含注解处理器）
- **通知**：通过 AndroidX Core 的 `NotificationCompat` 使用

## 构建与运行

```bash
# 命令行构建（Windows）
gradlew.bat :app:assembleDebug

# 产物
app/build/outputs/apk/debug/app-debug.apk
```

或用 Android Studio 打开本目录，直接 Run `app` 配置。

> `local.properties` 中的 `sdk.dir` 是本机 SDK 路径，已被 `.gitignore` 忽略，请勿提交。
