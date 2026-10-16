# HealthMate 安卓健康管理

Kotlin + Jetpack Compose + MVVM + Hilt + Room + Coroutines/Flow + Retrofit 的本地优先健康生活方式记录 App。

## 功能
- 本地健康档案、每日日志、稳定摘要、AI 分析记录和提醒。
- 首页编辑档案与配置 API Key；记录页可选仅本地保存或经二次确认后请求 AI；报告页查看分析历史。
- Android Keystore AES/GCM 加密保存本机 API Key；导出 JSON；一键删除健康数据、提醒和密钥。
- LLM 请求采用固定 System Prompt + 稳定档案摘要 + 本次新增记录的增量结构，避免每次发送完整历史。
- AlarmManager 按本地时间安排提醒；权限不可用时降级为非精确提醒。

## 打开与构建
使用 JDK 17 和 Android Studio 打开仓库根目录。Gradle 插件版本固定为 AGP 8.8.2 / Kotlin 2.1.10；应用 minSdk 26、targetSdk 34、compileSdk 35。仓库未包含 Gradle Wrapper JAR，可在本地安装 Gradle 后运行 gradle wrapper --gradle-version 8.10.2，再运行 ./gradlew :app:assembleDebug。

## 隐私与安全
数据默认保存在本机。请求 AI 分析时，应用会明确说明会向配置的第三方 LLM 服务发送稳定档案摘要和本次新增记录，并等待用户确认。不要记录请求体、健康数据或密钥到日志。客户端加密不能阻止设备被控制、App 被反编译或密钥滥用；面向真实用户分发时，应使用自有后端代理管理模型凭据、限额与审计，而不是把长期 API Key 分发到客户端。

本项目仅用于一般健康生活方式管理，不诊断疾病、不提供处方或替代就医。胸痛、明显呼吸困难、昏厥、严重过敏或迫切自伤风险等紧急情况应立即联系当地急救服务。

## 注意事项
- 默认接口指向 OpenAI 兼容 API，模型名在 HealthRepository 中集中设置；对其他服务需调整 Base URL、模型名与请求结构。
- 精确闹钟受 Android 版本、特殊访问权限和省电限制影响。本版本未实现重启后恢复提醒。
- 数据库 schema 当前为版本 1，正式升级前应导出 schema 并编写 migration。
- 提示缓存受模型供应商和输入前缀影响，不能保证一定命中。
