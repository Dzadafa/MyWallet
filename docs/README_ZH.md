# MyWallet 💸

> **Languages:**  
> [🇬🇧 English](../README.md) | [🇮🇩 Bahasa Indonesia](README_ID.md) | [🇨🇳 简体中文](README_ZH.md) | [🇯🇵 日本語](README_JA.md)

**MyWallet** 是一款现代化的个人财务管理应用，完全使用 **Kotlin** 开发，运行在 Android 平台上。  
它基于安全的 **Firebase** 后端，实现数据的实时同步、智能财务洞察，以及实用的主屏幕小部件。

这款应用旨在为你提供清晰的财务健康概览，帮助你追踪支出，并激励你达成储蓄目标。

---

## ✨ 功能特点

### 核心应用功能
* **财务记录：** 记录每一笔收入或支出，包括日期、描述、金额（以印尼卢比计）和自定义类别。  
* **交易历史：** 查看所有记录，按「最近收入」和「最近支出」分类显示。  
* **实时仪表盘：** 显示你的 **当前余额**（总收入减去总支出）。如果余额为负数，将以红色高亮。  
* **时间筛选：** 查看「全部时间」、「本月」或「今年」的数据。  
* **可视化图表：**
    * **支出分布图：** 以甜甜圈图显示「钱花在了哪里」。  
    * **收入与支出对比图：** 以柱状图显示在所选时间段内的收入与支出对比。  
* **智能愿望清单：**
    * 添加想要购买的物品及其价格。  
    * **可负担性检查：** 即时比较物品价格与当前余额。  
    * **储蓄时间预估：** 如果余额不足，应用会根据你的平均月储蓄率给出建议，如“按照当前储蓄速度，大约 5 个月后即可购买。”  
    * **清单状态：** 将已购买的项目标记为完成，移动到列表底部并显示删除线。  
* **云端安全存储：** 所有数据均安全保存于私有的 Firebase Firestore 数据库中，每位用户在首次启动时会自动创建匿名账户。  
* **主题切换：** 可在设置中快速切换为 **浅色模式**、**深色模式** 或 **跟随系统主题**。

---

## 🏠 主屏幕小部件功能
* **统计信息小部件：** 可调整大小的小部件，直接显示当前余额、总收入与总支出。  
* **愿望清单小部件：** 显示下一个未完成的愿望项目及其可负担状态（如“你现在可以买了！”或“预计 3 个月后”）。  
* **快速添加小部件：** 一个“+ 添加交易”按钮，点击后可直接打开快速添加界面，无需进入完整应用。

---

## 🛠 技术栈
* **语言：** [Kotlin](https://kotlinlang.org/)  
* **后端：** [Firebase](https://firebase.google.com/)  
    * **身份验证：** 匿名登录，提供独立且安全的用户账户。  
    * **数据库：** [Cloud Firestore](https://firebase.google.com/products/firestore) — 实时存储与同步。  
* **架构设计：**
    * [MVVM](https://developer.android.com/topic/architecture)
    * [Android 架构组件](https://developer.android.com/topic/libraries/architecture)（ViewModel、LiveData）
* **UI 与导航：**
    * [Android Navigation Component](https://developer.android.com/guide/navigation)
    * [ViewBinding](https://developer.android.com/topic/libraries/view-binding)
    * Material Components（按钮、卡片、输入框）
* **图表库：** [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
* **并发处理：** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
* **小部件系统：** [AppWidgets](https://developer.android.com/guide/topics/appwidgets)

---

## 🚀 构建方法

项目使用 Gradle Wrapper 手动构建。

1. **克隆仓库。**  
2. **配置 Firebase：**
    * 在 [Firebase 控制台](https://console.firebase.google.com/) 中创建新项目。  
    * 添加一个 Android 应用，包名为 `com.dzadafa.mywallet`。  
    * 下载 Firebase 提供的 `google-services.json` 文件，并放入 `MyWallet/app/` 目录。  
    * 在 Firebase 控制台中，进入 **Authentication → Sign-in method**，启用 **Anonymous 登录**。  
    * 打开 **Firestore Database** 并创建新数据库。  
3. **构建应用：**
    * 连接 Android 设备或启动模拟器。  
    * 在项目根目录下运行 `gradlew installDebug`。

---

Would you like me to include **both Simplified (`README_ZH.md`) and Traditional (`README_ZH_TW.md`) Chinese** variants in the multilingual template pack — so you can reach users in Mainland and Taiwan/Hong Kong separately?

