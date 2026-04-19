# 快乐小鸡 / Happy Chicks

Android TV 大屏亲子休闲游戏，适合 3–8 岁儿童。100% 遥控器/手柄可操作，纯本地数据，不依赖 Google Play。

## 项目规格

| 项目 | 值 |
|------|-----|
| 最低 Android | 6.0 (API 23) |
| 目标 Android | 9.0 (API 28) |
| 编译 SDK | 34 |
| 开发语言 | Kotlin |
| UI 框架 | AndroidX Leanback + 自定义 View |
| 架构 | MVVM（View/Activity ← ViewModel ← Repository ← SharedPreferences） |

## 功能

- **快乐小鸡**：自由模式下蛋孵化 / 60 秒限时挑战
- **趣味乐园**：小鸡填色（保存截图）、拼图（2×2 / 3×3）、形状认知、数数启蒙
- **我的农场**：农场照顾（饥饿度/清洁度）、农场探索、装扮小屋（服装解锁）
- **设置**：独立音乐/音效音量、中英文切换、重置游戏数据
- **成就系统**：首次孵化、孵化 10 只小鸡、收集 50 个鸡蛋
- **语音播报**：形状/颜色/数字朗读（TTS）

## 构建

需要 Android Studio 2023.1+ / JDK 17 / Android SDK 34。

```bash
# Debug 安装（Android TV 9.0 模拟器或实体设备）
./gradlew installDebug

# Release APK（自行配置 keystore 签名）
./gradlew assembleRelease
```

## 分发

- 输出 `app/build/outputs/apk/release/app-release.apk`
- 安装：`adb install -r app-release.apk`，或 U 盘、局域网共享、第三方应用商店
- **不依赖 Google Play 服务**，APK 可直接分发

## 本地数据存储

所有数据保存在 `SharedPreferences`（`happychicks_prefs`）：

| Key | 类型 | 默认 |
|-----|------|------|
| `best_score` | Int | 0 |
| `total_coins` | Int | 0 |
| `selected_character` | Int | 0 |
| `unlocked_skins` | StringSet | {} |
| `achievement_1..3` | Boolean | false |
| `chick_hunger` / `chick_cleanliness` | Int | 100 |
| `discovered_items` | StringSet | {} |
| `music_volume` / `sfx_volume` | Int | 70 / 80 |
| `language` | String | "zh" |

填色作品保存至 `Pictures/HappyChicks/*.png`。

## 测试

```bash
./gradlew test              # 单元测试
./gradlew connectedAndroidTest  # Espresso 冒烟测试
```

## 版权声明

- 美术/音效均为占位素材或 CC0，角色名"小橙/小蓝"为原创
- 避免任何第三方版权形象，本项目为内部分发用途

## 目录结构

```
app/src/main/java/com/happychicks/
├── MainActivity.kt          # Leanback 主菜单
├── HappyChicksApp.kt        # Application + 全局单例
├── core/                    # 焦点/按键/TTS/音效/过扫描 工具
├── data/                    # GameRepository + AchievementManager
├── game_core/               # 下蛋孵化 + 限时挑战
├── game_coloring/           # 填色画板
├── game_puzzle/             # 拼图
├── game_shape/              # 形状认知
├── game_counting/           # 数数启蒙
├── game_farm/               # 农场照顾 + 探索
├── profile/                 # 角色选择 + 装扮小屋
└── settings/                # 设置界面
```

## 占位资源说明

当前所有图形为 Vector Drawable 占位（`ic_chick`、`ic_egg`、`ic_hen`、`shape_*`、`tv_banner`）。音效目录 `res/raw/` 为空，`AudioManager` 默认静默。替换为真实资源时：

1. 替换 `res/drawable/*.xml` 为 PNG / 更精细的 Vector
2. 在 `res/raw/` 添加 `.ogg` / `.wav`，并在各 Activity 中调用 `audio.registerSfx("tag", R.raw.xxx)`
3. 提供 `mipmap-xhdpi` / `mipmap-xxhdpi` 应用图标 PNG
