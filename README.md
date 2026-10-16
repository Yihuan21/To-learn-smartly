# 安心健康：Android 健康管理 App

基于 Kotlin、Jetpack Compose、Material 3、MVVM、Hilt、Room、Coroutines/Flow、Retrofit 和 Moshi 的 Android 工程起点。采用本地优先设计：健康记录默认只存储在本机，只有用户保存 API Key、明确同意云端分析，并主动点击分析后才请求 LLM。

## 功能

- 健康档案和每日睡眠、饮食、运动、情绪、症状、体重、饮水与烟酒记录。
- Room 持久化、Flow 状态观察、ViewModel/Repository 分层。
- 固定系统提示词 + 稳定摘要 + 单条新增日志的增量分析；JSON 结构解析、稳定摘要与建议结果缓存。
- API Key 使用 Android Keystore 中的 AES-GCM 密钥加密后存入应用私有 SharedPreferences；不写入源码或日志。
- AlarmManager 提醒；没有精确闹钟特殊访问权限时回退到 WorkManager（可能延迟）；重启后恢复提醒。
- Android 13+ 通知权限；用户主动导出健康数据 JSON；一键清除数据库、API Key 和工作任务。
- AI 仅提供一般生活方式信息，不诊断疾病、不调整药物、不替代医疗服务。

## 打开与运行

1. 安装 Android Studio、JDK 17 和 Android SDK Platform 35。
2. 用 Android Studio 打开仓库根目录并同步 Gradle。
3. 运行 app 模块。仓库包含 Gradle 构建脚本，但没有提交 Gradle Wrapper JAR；如需命令行构建，请先在本地配置 Gradle Wrapper。
4. 默认 API 地址为 https://api.openai.com/v1/，模型名为 gpt-4o-mini。若使用其它兼容服务，请修改 AppModule.kt 中的 BASE_URL，并按需要调整 HealthRepository.kt 中的模型名。服务端需支持 Chat Completions 风格 API 和 JSON object response format。
5. 在「设置」中保存 API Key。填写档案后，阅读隐私说明并主动同意云端 AI 分析。保存记录本身不会上传数据。

## 目录概要

    app/src/main/java/com/example/healthmanager/
      data/local/       Room 实体、DAO、Database
      data/remote/      Retrofit DTO、API Key 加密存储
      data/repository/  本地保存、增量分析、导出与清理
      di/               Hilt providers
      domain/           固定系统提示词与分析 JSON 模型
      reminder/         通知、AlarmManager/WorkManager、开机恢复
      ui/               ViewModel、Compose 首页/记录/报告/设置

## 隐私与安全边界

- 只有用户主动请求分析，才会发送当前档案、稳定摘要和本次新增日志；不会批量上传完整历史日志。
- 数据上传会将档案和日志中的敏感健康信息发送到用户配置的服务。请先阅读该服务的数据处理政策，只填写必要信息。
- 固定 system prompt 始终位于消息前缀，有利于支持提示缓存的服务复用前缀；缓存是否命中取决于模型、前缀长度与服务端实现，不作保证。
- 健康数据目前保存在应用私有 Room 数据库中，但数据库文件本身没有集成 SQLCipher。Keystore 加密仅用于 API Key，不代表 Room 数据库已加密。
- 「导出数据」由用户主动触发系统分享面板，不包含 API Key。数据离开本机后，应用不能控制分享目标或导出副本。
- 清除全部数据会删除 Room 表数据、API Key，并取消本应用的 WorkManager 工作项；它不会撤销用户曾经导出的副本。
- Android 12+ 精确闹钟受系统特殊访问权限与电量管理约束；没有权限时采用 WorkManager 近似调度。Android 13+ 需要通知权限。
- 危险信号（胸痛、明显呼吸困难、昏厥、严重过敏、卒中征象、严重意识异常、自伤/自杀风险等）应优先联系当地急救服务，不要等待 AI 响应。
- 此项目是开发示例，不是医疗器械或诊断工具。提交后仍需在本地执行 Gradle Sync、assembleDebug、单元测试和目标设备测试；此环境未运行 Android 构建。
