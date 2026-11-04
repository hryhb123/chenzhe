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
| 2 | 接入 TMDb：网络层 + 影视列表页 + 详情页（MVVM） | ✅ |
| 3 | 收藏 / 搜索 / 桌面小组件 | ⏳ 待接入 |

## TMDb API Key 申请与配置

1. 打开 <https://www.themoviedb.org> 注册账号（邮箱需可收信）。
2. 登录后点右上角头像 → **Settings（设置）**。
3. 左侧选择 **API**，点击 **Request an API Key**。
4. 类型选 **Developer**（个人 / 学习用途），填写：
   - 姓名（Name）、邮箱；
   - Application Name 填 `MovieTracker`；
   - Application URL 可填 GitHub 仓库地址；
   - Application Summary 简单说明「课程设计学习项目，仅本地调试使用」。
5. 提交后一般几分钟内通过，在 **API Key (auth)** 处得到一串 32 位十六进制 Key（v3 认证方式）。
6. 把 Key 写进工程根目录的 `local.properties`：

```properties
tmdb.api.key=你的32位Key
# 可选：国内直连 api.themoviedb.org 不通时，换成可用代理/镜像（必须以 /3/ 结尾）
tmdb.base.url=https://api.themoviedb.org/3/
```

7. 重新构建（`gradlew.bat :app:assembleDebug`）即可自动切换到真实接口。

**安全说明**

- `local.properties` 已在 `.gitignore` 中，Key 不会进入版本库。
- Key 经 Gradle 写入 `BuildConfig.TMDB_API_KEY`，会被编译进 APK，理论上可被反编译取出；课设 / 学习场景可以接受，正式产品应改为由自己的后端代持 Key。
- 未配置 Key 时 App 自动使用内置示例数据（10 部影片），列表 / 详情 / 状态切换全部可用，方便在无网络或无 Key 的电脑上演示。
- 国内网络访问 `api.themoviedb.org`、`image.tmdb.org` 常常超时，这是网络环境问题，不是代码问题；可用代理后通过 `tmdb.base.url` 指向镜像。

## 架构（MVVM 分层）

```
UI 层（Activity + Adapter，只渲染状态）
   │  observe LiveData<UiState<T>>
ViewModel 层（MovieListViewModel / MovieDetailViewModel）
   │  取消请求、postValue 回主线程
Repository 层（MovieRepository，唯一数据出口）
   ├── 有 Key → Retrofit + OkHttp → TMDb 真实接口
   └── 无 Key → 内置示例数据（MockMovies，可切换空 / 错误场景）
```

- 页面状态统一为 `UiState`：加载中 / 成功 / 空 / 错误，任何分支都有对应视图，不白屏、不崩溃。
- 网络请求由 OkHttp 线程池异步执行，结果通过 `LiveData.postValue` 回到主线程刷新 UI。
- ViewModel 持有请求句柄，`onCleared()` 时取消未完成请求，避免回调泄漏；Activity 重建后复用 ViewModel 中的状态，不会重复请求。

## 目录结构

```
MovieTracker/
├── app/src/main/
│   ├── java/com/chenzhe/movietracker/
│   │   ├── MainActivity.java                    # 首页：影视列表
│   │   ├── data/
│   │   │   ├── model/                           # Movie / MovieDetail / Genre / MovieListResponse
│   │   │   ├── remote/                          # TMDbApi、TMDbClient、ApiConfig
│   │   │   ├── mock/                            # 示例数据与场景开关
│   │   │   └── repository/MovieRepository.java  # 数据仓库
│   │   ├── ui/
│   │   │   ├── common/UiState.java              # 统一页面状态
│   │   │   ├── list/                            # 列表 ViewModel + Adapter
│   │   │   └── detail/                          # 详情 ViewModel + Activity
│   │   └── util/PosterUrls.java                 # 海报地址拼装
│   ├── res/layout/                              # activity_main、item_movie、activity_movie_detail
│   └── AndroidManifest.xml
├── gradle/libs.versions.toml      # 版本目录：所有依赖版本在此锁定
├── build.gradle                   # 顶层构建脚本
├── settings.gradle
└── gradle.properties
```

## 依赖清单（版本见 `gradle/libs.versions.toml`）

- **UI**：AndroidX Core、AppCompat、Material Components、ConstraintLayout、RecyclerView
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
