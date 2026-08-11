# Zashboard Android Controller (Zashboard 移动端控制器)

<p align="center">
  <b>基于 Jetpack Compose 构建的 Clash / Sing-Box 现代化 Bento 风格移动端控制面板</b>
</p>

---

## 📖 项目来历与背景 (Project Origin)

**Zashboard Android** 起源于对现代化 Web 代理面板（如 Yacd、Meta-CubeX Dashboard 以及 Zashboard Web）视觉美学与操控体验的致敬与重构。

随着移动端代理内核（如 Clash Meta / Sing-Box）的广泛应用，许多用户在 Android 设备上依然渴望拥有如桌面/Web端般直观、优雅的 **Bento 盒网格 (Bento Grid Layout)** 布局与实时流量监控。

本项目基于 **Android 原生 Kotlin** 与 **Jetpack Compose (Material Design 3)** 完全重新开发，旨在提供一个轻量、极速、美观且功能完备的 Clash/Sing-Box 移动端 REST API 管理控制器。

---

## ✨ 核心功能亮点 (Key Features)

- 🎨 **Bento 盒极简美学 (Bento Grid Design)**：
  - 采用 M3 紫罗兰与柔和浅色容器配色方案。
  - 模块化 Bento 卡片：实时流量下载/上传极速响应图表、活动代理节点展示、分流模式切换、内核内存监控及一键重启内核。
- 🌏 **双语切换 (Bi-lingual Support)**：
  - 顶部导航栏一键切换 **中文 / English**，极简无缝。
- ⚡ **节点与策略组管理 (Proxies & Policy Groups)**：
  - 支持延迟测试 (Delay Ping)、策略组模式（Global / Rule / Direct）实时切换。
  - 节点类型识别（vless, vmess, ss, trojan 等）与高亮响应延迟状态。
- 🔌 **多后端/内核管理 (Multi-Backend REST Controller)**：
  - 灵活配置与管理本地或远程 Clash / Sing-box API 服务（如 `127.0.0.1:9090`）。
- 📊 **连接与日志监控 (Live Connections & Logs)**：
  - 活跃连接查看与一键断开所有连接。
  - 实时内核 Logs 输出与规则（Rules）分流检索。

---

## 🚀 GitHub Actions APK 编译与发布 (Automated Builds & Releases)

本项目已配置完整的 **GitHub Actions 自动化 CI/CD 工作流** (`.github/workflows/build_apk.yml`)。

### 1. 下载构建出的 APK 进行安装测试
无需在本地配置复杂的 Android 开发环境，您可以通过以下两种方式获取编译好的 `.apk` 文件：

#### 方式 A：通过 GitHub Releases 直接下载 (推荐)
1. 在 GitHub 仓库页面点击右侧的 **Releases** 标签。
2. 下载最新发布的 `app-release.apk` 安装包并安装到 Android 设备即可。

#### 方式 B：通过 GitHub Actions Artifacts 下载
1. 在 GitHub 仓库页面点击 **Actions** 选项卡。
2. 选择最近一次构建成功的工作流运行 (Workflow Run)。
3. 在页面底部的 **Artifacts** 区域，下载 `Zashboard-Release-APK` 或 `Zashboard-Debug-APK` 解压后即可获得 `.apk` 文件。

---

### 2. 如何触发构建 Release 安装包
- **自动发布 Release**：只要在 Git 中打上版本标签并推送（例如 `git tag v1.0.0 && git push origin v1.0.0`），GitHub Actions 将自动编译 Release 版本 APK，并在 GitHub Releases 中自动创建发布页面并附带 APK 文件。
- **手动触发构建**：在 GitHub Actions 页面点击 `Build & Release Android APK` -> `Run workflow` 即可手动触发编译与发布。

---

## 🛠️ 本地开发与编译 (Local Build Instructions)

如果你需要在本地进行代码修改与编译：

### 环境要求
- **Android Studio**: Jellyfish 或更高版本
- **JDK**: Java 17
- **Gradle**: 8.x

### 编译步骤
```bash
# 克隆仓库
git clone https://github.com/your-username/zashboard-android.git
cd zashboard-android

# 赋予 gradlew 可执行权限
chmod +x gradlew

# 编译 Debug 版本 APK
./gradlew assembleDebug

# 编译 Release 版本 APK
./gradlew assembleRelease
```

编译完成后，APK 生成路径为：
- Release APK: `app/build/outputs/apk/release/app-release.apk`
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`

### 💡 常见问题 (Troubleshooting)
- **Release APK 签名配置**：
  当环境变量 `STORE_PASSWORD` 与密钥文件未配置时，Release 构建任务会平滑回退使用 AGP 内置默认签名（Default Debug Key），确保 GitHub Actions CI 及本地能直接打出可安装测试的 Release APK。
- **KSP / Room 编译空指针异常 (`Task :app:kspDebugKotlin NullPointerException`)**：
  在 KSP2 模式下，项目已配置 `ksp.useKSP2=true` 与 `ksp { arg("room.generateKotlin", "true") }` 参数。如果遇到 KSP 线程异常，请确保清理 Gradle 缓存后重新构建：`./gradlew clean assembleDebug`。

---

## 📜 许可证 (License)

本项目基于 [MIT License](LICENSE) 开源许可。
