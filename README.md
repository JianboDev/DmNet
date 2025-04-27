# DmNet - 基于Retrofit的协程网络封装库

[![JitPack Version](https://jitpack.io/v/jianbo1124/DmNet.svg)](https://jitpack.io/#jianbo1124/DmNet)

> 专为Android设计的轻量级Retrofit协程封装库，简化网络请求处理流程

## 特性亮点

✔️ 零配置快速接入  
✔️ 协程异步请求支持  
✔️ 动态BaseURL配置  
✔️ 多维度日志追踪  
✔️ 自定义GSON解析策略  

## 环境要求

- Android API 21+
- Kotlin 1.6+
- Retrofit 2.9+

## 快速集成

### 1. 添加仓库配置

在项目级 `build.gradle` 文件中添加以下代码：

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

### 2. 添加依赖

在模块级 `build.gradle` 文件中添加以下依赖：

```gradle
dependencies {
    implementation("com.github.jianbo1124:DmNet:v1.2.0")
    
    // 必需依赖
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
}
```

### 3. 初始化配置

在 `Application` 中初始化：

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        NetConfig.apply {
            init(
                baseUrl = "https://api.yourdomain.com/v1/",
                userAgent = "Android/1.0.0",
                logger = ::customLog // 使用函数引用
            )
            debugMode(BuildConfig.DEBUG)
        }
    }

    private fun customLog(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("NET_DEBUG", message)
        }
    }
}
```

### 4. 发起网络请求

```kotlin
val service = NetClient.create(ApiService::class.java)
service.login("")
```
