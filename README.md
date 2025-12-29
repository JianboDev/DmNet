# DmNet - 基于Retrofit的轻量级网络封装库

[![](https://jitpack.io/v/jianbo1124/DmNet.svg)](https://jitpack.io/#jianbo1124/DmNet)

> 专为Android设计的轻量级Retrofit网络封装库，简化网络请求处理流程

## 特性亮点

✔️ 零配置快速接入  
✔️ 统一响应格式封装  
✔️ 动态BaseURL配置  
✔️ 自定义请求头支持  
✔️ 多维度日志追踪  
✔️ SSL证书信任处理  
✔️ 自定义拦截器支持  
✔️ GSON自动解析策略  

## 环境要求

- Android API 21+
- Kotlin 1.8+
- Retrofit 3.0.0+
- OkHttp 4.12.0+

## 快速集成

### 1. 添加仓库配置

在项目级 `build.gradle.kts` 文件中添加以下代码：

```kotlin
allprojects {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2. 添加依赖

在模块级 `build.gradle.kts` 文件中添加以下依赖：

```kotlin
dependencies {
    implementation("com.github.jianbo1124:DmNet:1.2.3")
    
    // 必需依赖（DmNet已包含，如需自定义版本可单独添加）
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
}
```

### 3. 初始化配置

在 `Application` 中初始化：

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 基础配置
        NetConfig.init(
            baseUrl = "https://api.yourdomain.com/v1/",
            log = ::customLog
        )
        
        // 或完整配置
        NetConfig.init(
            baseUrl = "https://api.yourdomain.com/v1/",
            timeout = 15L,
            enableLog = true,
            commonHeader = mapOf(
                "User-Agent" to "Android/1.0.0",
                "Authorization" to "Bearer your_token"
            ),
            logHelper = object : LogHelper {
                override fun log(msg: String) {
                    if (BuildConfig.DEBUG) {
                        Log.d("NET_DEBUG", msg)
                    }
                }
            }
        )
    }

    private fun customLog(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("NET_DEBUG", message)
        }
    }
}
```

### 4. 定义API接口

```kotlin
interface ApiService {
    @GET("user/profile")
    suspend fun getUserProfile(): BaseResponse<UserInfo>
    
    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse<Token>
    
    @POST("data/upload")
    @Multipart
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): BaseResponse<String>
}
```

### 5. 发起网络请求

```kotlin
// 创建API服务
val apiService = NetClient.create(ApiService::class.java)

// 在协程中调用
lifecycleScope.launch {
    try {
        val response = apiService.getUserProfile()
        if (response.code == 200) {
            // 请求成功
            val userInfo = response.data
        } else {
            // 请求失败
            Log.e("API", "Error: ${response.message}")
        }
    } catch (e: Exception) {
        // 网络异常
        Log.e("API", "Network error", e)
    }
}
```

## 高级配置

### 自定义拦截器

```kotlin
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = getToken() // 获取token
        
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
            
        return chain.proceed(newRequest)
    }
}

// 添加到配置中
NetConfig.init(
    baseUrl = "https://api.yourdomain.com/v1/",
    interceptor = listOf(AuthInterceptor()),
    log = ::customLog
)
```

### 动态修改配置

```kotlin
// 动态修改BaseURL
NetConfig.baseUrl = "https://new-api.domain.com/v2/"

// 动态添加请求头
NetConfig.commonHeader = mapOf(
    "X-Custom-Header" to "custom_value",
    "Content-Type" to "application/json"
)

// 动态控制日志
NetConfig.enableLog = BuildConfig.DEBUG
```

## 构建和发布

### 本地构建

```bash
# 构建项目
./gradlew build

# 运行测试
./gradlew test

# 生成Javadoc
./gradlew dokkaHtml
```

### 发布到JitPack

项目已配置JitPack自动构建，推送代码到GitHub后：

1. 确保标签版本与 `build.gradle.kts` 中的版本一致
2. 推送标签：`git tag v1.0.0 && git push origin v1.0.0`
3. 在 [JitPack](https://jitpack.io) 上查看构建状态

### 运行命令

```bash
# 克隆项目
git clone https://github.com/jianbo1124/DmNet.git
cd DmNet

# 构建发布版本
./gradlew build -x test

# 发布到本地Maven仓库
./gradlew publishToMavenLocal

# 清理构建缓存
./gradlew clean
```

## 常见问题解决方案

### Q1: 如何处理HTTPS证书问题？

A: DmNet默认信任所有SSL证书，如需自定义证书验证：

```kotlin
// 修改OkHttpFactory中的SSL配置
private fun createSSLSocketFactory(): SSLSocketFactory? {
    return try {
        val trustManagerArray = arrayOf<TrustManager>(object : X509TrustManager {
            // 自定义证书验证逻辑
        })
        val sc = SSLContext.getInstance("TLS")
        sc.init(null, trustManagerArray, SecureRandom())
        sc.socketFactory
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
```

### Q2: 如何设置不同的超时时间？

A: 通过NetConfig配置超时时间（单位：秒）

```kotlin
NetConfig.timeOut = 30L // 设置30秒超时
```

### Q3: 如何自定义日志格式？

A: 实现LogHelper接口并传入NetConfig.init()

```kotlin
class CustomLogHelper : LogHelper {
    override fun log(msg: String) {
        // 自定义日志处理逻辑
        Log.i("CustomNet", msg)
        // 也可以写入文件或发送到日志服务器
    }
}

NetConfig.init(
    baseUrl = "https://api.example.com/",
    logHelper = CustomLogHelper()
)
```

### Q4: 如何处理BaseResponse的统一格式？

A: DmNet提供了BaseResponse类，标准格式为：

```kotlin
data class BaseResponse<out T>(
    val code: Int = 0,      // 响应码，200表示成功
    val message: String = "", // 响应消息
    val data: T = Any() as T  // 响应数据
)
```

### Q5: 如何添加全局错误处理？

A: 创建拦截器统一处理错误：

```kotlin
class ErrorHandlerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        try {
            val response = chain.proceed(request)
            
            // 处理HTTP错误码
            if (!response.isSuccessful) {
                when (response.code) {
                    401 -> handleUnauthorized()
                    403 -> handleForbidden()
                    404 -> handleNotFound()
                    500 -> handleServerError()
                }
            }
            return response
        } catch (e: Exception) {
            handleNetworkError(e)
            throw e
        }
    }
}
```

### Q6: 如何在Debug和Release环境中使用不同配置？

A: 根据BuildConfig动态设置：

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val isDebug = BuildConfig.DEBUG
        
        NetConfig.apply {
            if (isDebug) {
                // 开发环境配置
                init(
                    baseUrl = "https://dev-api.example.com/v1/",
                    timeout = 30L,
                    enableLog = true,
                    commonHeader = mapOf("X-Env" to "debug"),
                    log = ::debugLog
                )
            } else {
                // 生产环境配置
                init(
                    baseUrl = "https://api.example.com/v1/",
                    timeout = 10L,
                    enableLog = false,
                    commonHeader = mapOf("X-Env" to "production"),
                    log = ::releaseLog
                )
            }
        }
    }
}
```

## 版本历史

- **v1.0.0** - 初始版本发布
  - 基础网络请求功能
  - 统一响应格式封装
  - 自定义拦截器支持
  - SSL证书信任处理
  - 多维度日志追踪

## 许可证

本项目采用 [MIT License](LICENSE) 开源协议。

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启一个 Pull Request

## 联系方式

- GitHub: [@jianbo1124](https://github.com/jianbo1124)
