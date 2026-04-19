---

# Android TV 游戏《快乐小鸡》开发文档

> 版本：1.0  
> 目标平台：Android TV（系统版本 9.0，API Level 28）  
> 分发方式：不通过 Google Play 发布（APK 直接安装或第三方应用商店）  
> 云服务：无（所有数据本地存储，无账号登录、无排行榜同步）

---

## 一、项目概述

### 1.1 产品简介

《快乐小鸡》是一款专为 Android TV 大屏设计的亲子休闲游戏。玩家使用遥控器或游戏手柄，帮助母鸡下蛋、孵化小鸡，并体验填色、拼图、形状认知、农场照顾等寓教于乐的玩法。游戏适合 3–8 岁儿童及其家长，支持单人游玩。

### 1.2 核心目标

- 100% 遥控器/手柄可操作，无触摸依赖
- 大屏友好（字体、按钮、焦点高亮清晰可见）
- 纯本地游戏，无需网络（除可选音效下载外）
- 性能稳定，在 1.5GB 内存电视上流畅运行

### 1.3 技术约束

| 项目 | 要求 |
|------|------|
| 最低 Android 版本 | 6.0 (API 23) |
| 目标 Android 版本 | 9.0 (API 28) |
| 编译 SDK 版本 | 33 或 34 |
| 开发语言 | Kotlin 或 Java |
| UI 框架 | Leanback 支持库 + 自定义 View / Jetpack Compose for TV（可选） |
| 游戏引擎（可选） | Unity 2019 LTS / Godot 3.x |

---

## 二、功能需求（不含云服务）

### 2.1 核心玩法

| 功能 | 描述 | 本地存储要求 |
|------|------|--------------|
| 下蛋孵化 | 按确认键下蛋 → 倒计时（如 10 秒）→ 孵化出小鸡。小鸡成长后可再次下蛋 | 无需存储 |
| 自由模式 | 无时间限制，无限下蛋/孵化 | 无 |
| 限时挑战 | 60 秒内统计下蛋数量，结束后显示本次成绩，并保存“历史最佳成绩”（仅本地） | 保存最佳成绩（整数） |
| 本地成就 | 完成特定条件（如“首次孵化”“孵化 10 只小鸡”“收集 50 个鸡蛋”）时弹窗庆祝，成就数据保存在本地 | 保存成就解锁状态（布尔值） |

### 2.2 扩展玩法

| 功能 | 描述 | 本地存储 |
|------|------|----------|
| 小鸡填色 | 提供线稿，方向键选色，确认键涂色。可保存作品截图至本地相册（需存储权限） | 保存图片文件 |
| 拼图游戏 | 2×2、3×3 拼图，完成时播放动画。不保存进度 | 无 |
| 形状认知 | 显示形状（圆/方/三角），选择匹配的鸡蛋或小鸡。无需存储 | 无 |
| 农场照顾 | 喂食、清理鸡舍。饥饿度/清洁度本地状态保存 | 保存小鸡状态（整数） |
| 农场探索 | 点击隐藏物体触发动画，发现记录本地保存 | 保存发现记录（布尔值） |
| 数数启蒙 | 显示若干鸡蛋/小鸡，选择正确数字。无需存储 | 无 |

### 2.3 角色与个性化

| 功能 | 描述 | 存储 |
|------|------|------|
| 角色选择 | 选择佩奇或乔治（语音/动作略有差异） | 保存当前角色 ID |
| 服装/装饰解锁 | 使用游戏内赚取的“鸡蛋币”解锁母鸡/小鸡的配色。金币数量本地保存 | 保存金币数量、解锁状态 |

### 2.4 教育功能

- 数字认知：数数正确时语音播报数字
- 颜色认知：选色时语音提示颜色名称
- 形状认知：选中正确形状时语音提示形状名称
- 因果关系：喂食→长大，清理→干净，给予明确视觉/音效反馈

---

## 三、技术架构与模块设计

### 3.1 整体架构（推荐 MVP 或 MVVM）

```
View (Activity/Fragment) ← ViewModel ← Repository ← LocalDataSource (Room/SharedPreferences)
```

- **View**：处理焦点、按键事件、UI 更新
- **ViewModel**：存储 UI 相关数据，处理游戏逻辑
- **Repository**：统一数据接口
- **LocalDataSource**：使用 `SharedPreferences` 存储简单键值对（成就、金币、最佳成绩），使用 `Room` 存储复杂数据（可选）

### 3.2 模块划分

| 模块 | 职责 |
|------|------|
| `game_core` | 下蛋孵化逻辑、计时器、挑战模式 |
| `game_coloring` | 填色画板、颜色选择、保存图片 |
| `game_puzzle` | 拼图生成、碎片交换、完成检测 |
| `game_shape` | 形状展示、选项选择、反馈 |
| `game_farm` | 饥饿度/清洁度定时更新、喂食/清理操作 |
| `game_counting` | 随机生成物体数量、选项交互 |
| `profile` | 角色选择、服装解锁、金币管理 |
| `settings` | 音量调节、语言选择（中/英）、重置游戏数据 |

### 3.3 本地存储方案

- **SharedPreferences**（推荐）：
  - 最佳成绩：`best_score`
  - 成就：`achievement_1`, `achievement_2` ...
  - 金币数：`coins`
  - 当前角色：`selected_character`
  - 已解锁服装：`unlocked_skins`（可用 Set 序列化）
  - 农场状态：`chick_hunger`, `chick_cleanliness`
  - 探索发现：`discovered_items`

- **文件存储**：
  - 填色作品截图：保存至 `/sdcard/Pictures/HappyChicks/`（需申请 `WRITE_EXTERNAL_STORAGE` 权限，Android 9.0 可直接申请）

- **Room 数据库**（可选，如果数据复杂可用）：
  - 例如记录每次挑战的详细历史（时间、得分）

> **注意**：不集成任何云同步、云备份功能。

---

## 四、Android TV 专项设计

### 4.1 焦点与导航

- 所有可交互控件必须可聚焦（`android:focusable="true"`）
- 使用 `android:nextFocusUp/Down/Left/Right` 显式定义焦点移动路径
- 焦点高亮样式：边框放大 + 阴影 + 轻微发光（避免仅靠颜色区分）
- 首次进入游戏，焦点落在主菜单第一个按钮上

### 4.2 遥控器/手柄按键映射

| 按键 | 功能 |
|------|------|
| 方向键上/下/左/右 | 移动焦点 |
| 确认键（DPAD_CENTER / ENTER） | 确认选择、下蛋、涂色、拼图放置 |
| 返回键（BACK） | 返回上一级菜单、退出当前小游戏 |
| 菜单键（MENU） | （可选）呼出设置快捷面板 |

> 不依赖长按、双指等复杂手势。

### 4.3 过扫描处理

- 所有 UI 元素距离屏幕边缘至少 48dp 或屏幕宽/高的 5%
- 在布局根视图设置 `android:padding="5%"` 或动态计算安全区域
- 使用 `ViewCompat.setOnApplyWindowInsetsListener` 获取实际安全区域

### 4.4 画面适配

- 支持 1080p（1920×1080）和 4K（3840×2160）分辨率
- 图片资源提供 `xhdpi`（1920×1080 对应）和 `xxhdpi`（4K）两套，或使用矢量图（Vector Drawable）
- 字体：正文最小 24sp，标题最小 32sp

### 4.5 AndroidManifest 配置（无 Google Play 相关）

```xml
<application
    android:banner="@drawable/tv_banner"
    android:isGame="true"
    android:label="@string/app_name"
    android:theme="@style/Theme.Leanback">
    
    <activity
        android:name=".MainActivity"
        android:screenOrientation="landscape"
        android:configChanges="keyboard|keyboardHidden|navigation">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
            <category android:name="android.intent.category.LEANBACK_LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

- 无需声明任何 Google Play 服务相关权限或 meta-data
- 如果应用需要存储图片，添加：
  ```xml
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
                   android:maxSdkVersion="28" />
  ```
  针对 Android 9.0 可正常申请。

---

## 五、性能与兼容性要求

### 5.1 性能指标

| 指标 | 要求 |
|------|------|
| 启动时间 | 冷启动 ≤ 3 秒 |
| 帧率 | 稳定 60fps（核心玩法）、30fps（填色/拼图） |
| 内存占用 | 常驻内存 ≤ 150MB，峰值 ≤ 200MB |
| APK 体积 | ≤ 150MB（若使用 Unity 可适当放宽至 200MB） |

### 5.2 测试设备（Android 9.0）

- NVIDIA Shield TV (2017/2019)
- 小米电视 4 / 4A
- 任意 Android TV 9.0 模拟器（API 28）

### 5.3 稳定性

- 连续运行 2 小时无崩溃
- 低内存（1GB RAM）设备不闪退
- 遥控器/手柄断连后重连，焦点恢复正常

---

## 六、UI/UX 设计规范

### 6.1 主菜单结构

```
主屏幕（横向导航）
├── 快乐小鸡
│   ├── 自由模式
│   └── 限时挑战
├── 趣味乐园
│   ├── 小鸡填色
│   ├── 农场拼图
│   ├── 形状认知
│   └── 数数小屋
├── 我的农场
│   ├── 农场照顾
│   ├── 农场探索
│   └── 装扮小屋（服装解锁）
└── 设置
    ├── 音量（音乐/音效）
    ├── 语言（中文/英文）
    └── 重置游戏（清除所有本地数据）
```

### 6.2 视觉风格

- 明亮、卡通、低饱和度
- 背景随焦点变化（例如聚焦“快乐小鸡”时显示母鸡场景）
- 按钮尺寸至少 120×80 dp，间距 ≥ 20dp
- 使用 Leanback 的 `BrowseFragment` 或自定义 `RecyclerView` 实现横向网格

### 6.3 音效与语音

- 背景音乐可开关，默认开启
- 动作音效：下蛋、孵化、填色、拼图成功、喂食等
- 语音提示：成就解锁、数数、形状/颜色名称（中文普通话）
- 音量独立调节（音乐 / 音效）

---

## 七、开发与构建指南

### 7.1 开发环境

- Android Studio 4.0+
- Gradle 7.0+
- 依赖库（建议版本）：
  ```groovy
  implementation 'androidx.leanback:leanback:1.0.0'
  implementation 'androidx.appcompat:appcompat:1.2.0'
  implementation 'com.google.android.material:material:1.4.0'
  // 如需 Room
  implementation 'androidx.room:room-runtime:2.4.0'
  // 图片保存
  implementation 'androidx.core:core:1.7.0'
  ```

### 7.2 构建 APK

- 生成 signed APK（用于分发）
- 不依赖 Google Play 服务，因此不需要配置 `google-services.json`
- 启用 ProGuard 混淆（可选）以缩小体积

### 7.3 安装与分发

- 提供 APK 文件，可通过 U 盘、局域网共享或第三方应用商店安装
- 安装命令：`adb install -r HappyChicks.apk`

---

## 八、测试检查清单

- [ ] 所有功能仅使用遥控器/手柄可完成，无触摸提示
- [ ] 焦点移动流畅，高亮效果明显
- [ ] 过扫描：所有文字和按钮在电视上完整显示
- [ ] 下蛋孵化动画流畅，倒计时准确
- [ ] 限时挑战结束后正确显示本次得分，并更新最佳成绩
- [ ] 成就达成时弹窗并保存状态（重启后依然已解锁）
- [ ] 填色：可选择颜色、涂色、保存截图成功（需授权存储权限）
- [ ] 拼图：2×2 和 3×3 可正常完成，碎片可移动
- [ ] 形状认知：随机题目，正确选择有语音反馈
- [ ] 农场照顾：饥饿度/清洁度随时间变化，喂食/清理后数值更新
- [ ] 农场探索：隐藏物体被点击后播放动画，再次进入不再出现（保存状态）
- [ ] 角色切换：更换后语音和动作改变
- [ ] 金币增减：购买服装后金币扣除，服装解锁
- [ ] 设置：音量调节、语言切换、重置游戏（清除所有 SharedPreferences 和文件）
- [ ] 长时间运行无内存泄漏
- [ ] 断网（飞行模式）下所有功能正常

---

## 九、版权与法律声明（非发布用途）

- 本游戏为内部使用或第三方分发，不经过 Google Play。
- 游戏名称、角色形象、音效需使用**原创内容**，避免侵犯《小猪佩奇》等第三方版权。
- 建议所有美术、音乐资源均为自主设计或使用 CC0 许可素材。
- 本文档仅用于开发指导，不构成任何商业发布授权。

---

## 十、附录：数据存储 Key 参考（SharedPreferences）

| Key | 类型 | 默认值 | 说明 |
|-----|------|--------|------|
| `best_score` | Int | 0 | 限时挑战最佳成绩 |
| `total_coins` | Int | 0 | 金币总数 |
| `selected_character` | Int | 0 | 0=佩奇, 1=乔治 |
| `unlocked_skins` | StringSet | {} | 已解锁服装 ID 集合 |
| `achievement_1` | Boolean | false | 首次孵化 |
| `achievement_2` | Boolean | false | 孵化 10 只小鸡 |
| `achievement_3` | Boolean | false | 收集 50 个鸡蛋 |
| `chick_hunger` | Int | 100 | 饥饿度（0-100） |
| `chick_cleanliness` | Int | 100 | 清洁度（0-100） |
| `discovered_items` | StringSet | {} | 已发现隐藏物体 ID |

---

**文档结束**

如果开发过程中需要更详细的代码示例（如焦点管理、自定义 Leanback 行、拼图算法、填色画板实现等），请随时告知，我可以继续提供。
