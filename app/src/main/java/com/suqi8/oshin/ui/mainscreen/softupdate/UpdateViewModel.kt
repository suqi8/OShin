package com.suqi8.oshin.ui.mainscreen.softupdate

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.BuildConfig
import com.suqi8.oshin.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private object GitHubConfig {
    const val BASE_URL = "https://api.github.com"
    const val MAIN_REPO = "suqi8/OShin"
    const val CI_REPO = "suqi8/OShin-Builds"
    const val MAIN_RELEASES_PER_PAGE = 100
    const val CI_RELEASES_PER_PAGE = 30
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
}

private object DownloadConfig {
    const val BUFFER_SIZE = 8192 // 8KB
    const val DOWNLOAD_DIR = "downloads"
    const val CI_DOWNLOAD_FILENAME = "OShin_CI_Latest.apk"
    const val RELEASE_DOWNLOAD_FILENAME = "OShin_Release_Latest.apk"
}

// ============ ViewModel ============

@HiltViewModel
class UpdateViewModel @Inject constructor(
    @ApplicationContext private val context: Context // 已注入 Context
) : ViewModel() {

    private val TAG = "UpdateViewModel"
    private val gson = Gson()
    private val GITHUB_TOKEN_KEY = "github_pat"
    private val NON_NUMERIC_REGEX = Regex("[^0-9.]")

    // UI 状态
    var releases by mutableStateOf<List<GitHubRelease>>(emptyList())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var isDownloading by mutableStateOf(false)
    var currentToken by mutableStateOf<String?>(null)
    private var hasAutoChecked = false

    // 下载进度状态
    private val _downloadProgress = MutableStateFlow(-1f)
    val downloadProgress = _downloadProgress.asStateFlow()

    private val _updateCheckResult = MutableStateFlow<GitHubRelease?>(null)
    val updateCheckResult = _updateCheckResult.asStateFlow()
    var triggerAutoDownload by mutableStateOf(false)
        private set

    private val _downloadedBytes = MutableStateFlow(0L)
    val downloadedBytes = _downloadedBytes.asStateFlow()

    private val _totalBytes = MutableStateFlow(0L)
    val totalBytes = _totalBytes.asStateFlow()

    // Release 缓存
    private val releaseCache = mutableMapOf<Int, List<GitHubRelease>>()

    // 懒加载 HTTP 客户端
    private val httpClient: OkHttpClient by lazy {
        createHttpClientWithAuth()
    }

    init {
        loadTokenFromPrefs()
    }

    // ============ Token 管理 ============

    private fun loadTokenFromPrefs() {
        val token = context.prefs("settings").getString(GITHUB_TOKEN_KEY, "")
        currentToken = token.ifBlank { null }
    }

    fun checkTokenStatus() {
        loadTokenFromPrefs()
    }

    fun setAutoDownloadFlag() {
        triggerAutoDownload = true
    }

    fun consumeAutoDownloadFlag() {
        triggerAutoDownload = false
    }

    fun getSavedUpdateChannel(): Int {
        return context.prefs("settings").getInt("app_update_channel", 0)
    }

    fun clearUpdateCheckResult() {
        _updateCheckResult.value = null
    }

    fun autoCheckForUpdate(currentVersion: String) {
        if (hasAutoChecked) return
        viewModelScope.launch {
            val savedChannel = getSavedUpdateChannel()

            fetchReleasesInternal(savedChannel)

            val latestRelease = releases.firstOrNull()
            val isDebugEnabled = context.prefs("settings").getBoolean("Debug", false)
            val shouldShowUpdate: Boolean = if (isDebugEnabled && releases.isNotEmpty()) true else {
                hasNewVersion(latestRelease, currentVersion, savedChannel)
            }
            if (shouldShowUpdate) {
                _updateCheckResult.value = latestRelease
            }
            hasAutoChecked = true
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            context.prefs("settings").edit { putString(GITHUB_TOKEN_KEY, token) }
            withContext(Dispatchers.Main) {
                currentToken = token.ifBlank { null }
            }
        }
    }

    fun clearToken() {
        viewModelScope.launch(Dispatchers.IO) {
            context.prefs("settings").edit { remove(GITHUB_TOKEN_KEY) }
            withContext(Dispatchers.Main) {
                currentToken = null
            }
        }
    }

    // ============ HTTP 客户端 ============

    private fun createHttpClientWithAuth(): OkHttpClient {
        val token = getTokenFromPrefs()
        return OkHttpClient.Builder()
            .followRedirects(true)
            .connectTimeout(GitHubConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(GitHubConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(GitHubConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .apply {
                if (!token.isNullOrBlank()) {
                    addInterceptor { chain ->
                        chain.proceed(
                            chain.request().newBuilder()
                                .header("Authorization", "Bearer $token")
                                .build()
                        )
                    }
                }
            }
            .build()
    }

    private fun getTokenFromPrefs(): String? {
        val token = context.prefs("settings").getString(GITHUB_TOKEN_KEY, "")
        return token.ifBlank { null }
    }

    // ============ 版本比较 ============

    private fun extractVersion(tag: String): List<Int> {
        val clean = tag
            .removePrefix("v")
            .substringBefore("(")
            .substringBefore("-")
            .substringBefore(".g")
            .replace(NON_NUMERIC_REGEX, "")
        return clean.split(".").mapNotNull { it.toIntOrNull() }
    }

    private fun isNewerVersion(latestTag: String, currentTag: String): Boolean {
        val latestParts = extractVersion(latestTag)
        val currentParts = extractVersion(currentTag)

        for (i in 0 until maxOf(latestParts.size, currentParts.size)) {
            val latest = latestParts.getOrElse(i) { 0 }
            val current = currentParts.getOrElse(i) { 0 }
            when {
                latest > current -> return true
                latest < current -> return false
            }
        }
        return false
    }

    fun hasNewVersion(
        latestRelease: GitHubRelease?,
        currentVersion: String,
        releaseType: Int
    ): Boolean {
        if (latestRelease == null) return false

        val latestTag = latestRelease.tagName
        return if (releaseType == 0) {
            isNewerVersion(latestTag, currentVersion)
        } else {
            val cleanLatest = latestTag.replace("CI", "", ignoreCase = true)
            val cleanCurrent = currentVersion.replace("CI", "", ignoreCase = true)
            isNewerVersion(cleanLatest, cleanCurrent)
        }
    }

    // ============ Release 获取 ============

    private fun cleanMarkdownText(text: String?): String {
        if (text.isNullOrBlank()) return ""
        return text
            .replace("\r\n", "\n")
            .replace(Regex("(?i)<br\\s*/?>"), "\n")
            .replace(Regex("\n{3,}"), "\n\n")
            .replace(Regex("(?<!\\n)\\n(?!\\n)"), "\n\n")
            .trim()
    }

    fun fetchReleases(releaseType: Int) {
        viewModelScope.launch {
            fetchReleasesInternal(releaseType)
        }
    }

    private suspend fun fetchReleasesInternal(releaseType: Int) {
        withContext(Dispatchers.IO) {
            // 检查缓存
            if (releaseCache.containsKey(releaseType)) {
                releases = releaseCache[releaseType] ?: emptyList()
                return@withContext
            }

            isLoading = true
            error = null
            releases = emptyList()

            try {
                val result = retryWithBackoff(maxRetries = 3, delayMillis = 500) {
                    fetchReleasesFromGitHub(releaseType)
                }

                result?.let { fetchedReleases ->
                    releases = if (releaseType == 0) {
                        fetchedReleases
                            .filter { !it.tagName.contains("CI", ignoreCase = true) }
                            .map { it.copy(body = cleanMarkdownText(it.body)) }
                            .take(30)
                    } else {
                        fetchedReleases.take(20)
                    }
                    releaseCache[releaseType] = releases
                    logDebug("解析到 ${releases.size} 个 releases")
                } ?: run {
                    error = context.getString(R.string.view_model_error_fetch_failed) // 修改
                    logError("未能从GitHub获取数据")
                }
            } catch (e: Exception) {
                error = handleException(e) // 修改
                logError("获取异常: ${error}", e)
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun fetchReleasesFromGitHub(releaseType: Int): List<GitHubRelease>? {
        return withContext(Dispatchers.IO) {
            val repoUrlPart = if (releaseType == 0) GitHubConfig.MAIN_REPO else GitHubConfig.CI_REPO
            val perPage = if (releaseType == 0) GitHubConfig.MAIN_RELEASES_PER_PAGE else GitHubConfig.CI_RELEASES_PER_PAGE
            val url = "${GitHubConfig.BASE_URL}/repos/$repoUrlPart/releases?per_page=$perPage"

            logDebug("开始获取 Releases, URL: $url")

            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()

            try {
                if (response.isSuccessful) {
                    logDebug("获取成功 (代码: ${response.code})")
                    val responseBody = response.body.string()
                    gson.fromJson(responseBody, Array<GitHubRelease>::class.java)?.toList()
                } else {
                    error = when (response.code) {
                        403 -> context.getString(R.string.view_model_error_api_limit) // 修改
                        404 -> context.getString(R.string.view_model_error_not_found) // 修改
                        else -> context.getString(R.string.view_model_error_fetch_failed_code, response.code) // 修改
                    }
                    logError("HTTP错误: ${response.code}, 消息: ${response.message}")
                    null
                }
            } finally {
                response.close()
            }
        }
    }

    // ============ 下载功能 ============

    suspend fun downloadApk(url: String, context: Context): File? {
        return withContext(Dispatchers.IO) {
            logDebug("开始下载APK: $url")
            resetDownloadProgress()
            isDownloading = true
            error = null

            var outputFile: File? = null

            try {
                val result = retryWithBackoff(maxRetries = 2, delayMillis = 1000) {
                    downloadApkInternal(url, context)
                }

                outputFile = result
                if (result != null) {
                    _downloadProgress.value = 1f
                    logDebug("文件下载完成: ${result.absolutePath}")
                }
                result
            } catch (e: Exception) {
                val errorMsg = e.message ?: context.getString(R.string.view_model_error_unknown) // 修改
                error = context.getString(R.string.view_model_error_download_exception, errorMsg) // 修改
                logError("下载异常", e)
                outputFile?.delete()
                null
            } finally {
                isDownloading = false
                if (error != null) {
                    _downloadProgress.value = -1f
                }
            }
        }
    }

    private suspend fun downloadApkInternal(url: String, context: Context): File? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()

            try {
                if (!response.isSuccessful) {
                    error = context.getString(R.string.view_model_error_download_failed_code, response.code) // 修改
                    logError("下载HTTP错误: ${response.code}")
                    return@withContext null
                }

                val body = response.body
                val total = body.contentLength()
                _totalBytes.value = total
                logDebug("文件总大小: $total bytes")

                if (total > 0) _downloadProgress.value = 0f

                val fileName = extractFileName(response, url)
                val file = createDownloadFile(context, fileName)

                downloadToFile(body, file, total)
                file
            } finally {
                response.close()
            }
        }
    }

    private fun extractFileName(response: Response, url: String): String {
        val contentDisposition = response.header("Content-Disposition")
        val fileNameFromHeader = contentDisposition
            ?.substringAfter("filename=", "")
            ?.removeSurrounding("\"")

        val fileName = fileNameFromHeader?.takeIf { it.isNotBlank() }
            ?: url.substringAfterLast("/")

        return if (fileName.isBlank() || !fileName.endsWith(".apk")) {
            if (url.contains("OShin-Builds")) DownloadConfig.CI_DOWNLOAD_FILENAME
            else DownloadConfig.RELEASE_DOWNLOAD_FILENAME
        } else {
            fileName
        }
    }

    private fun createDownloadFile(context: Context, fileName: String): File {
        val downloadDir = File(context.getExternalFilesDir(null), DownloadConfig.DOWNLOAD_DIR)
        downloadDir.mkdirs()
        return File(downloadDir, fileName)
    }

    private suspend fun downloadToFile(body: ResponseBody, file: File, total: Long) {
        withContext(Dispatchers.IO) {
            body.byteStream().use { inputStream ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(DownloadConfig.BUFFER_SIZE)
                    var bytesCopied: Long = 0
                    var bytes: Int

                    while (inputStream.read(buffer).also { bytes = it } >= 0) {
                        output.write(buffer, 0, bytes)
                        bytesCopied += bytes
                        _downloadedBytes.value = bytesCopied

                        if (total > 0) {
                            _downloadProgress.value = bytesCopied.toFloat() / total.toFloat()
                        }
                    }
                }
            }
        }
    }

    // ============ 工具函数 ============

    private fun resetDownloadProgress() {
        _downloadProgress.value = -1f
        _downloadedBytes.value = 0L
        _totalBytes.value = 0L
    }

    private suspend fun <T> retryWithBackoff(
        maxRetries: Int = 3,
        delayMillis: Long = 1000,
        block: suspend () -> T?
    ): T? {
        repeat(maxRetries) { attempt ->
            try {
                val result = block()
                if (result != null) return result
            } catch (e: Exception) {
                logDebug("重试 ${attempt + 1}/$maxRetries 失败: ${e.message}")
                if (attempt < maxRetries - 1) {
                    delay(delayMillis * (attempt + 1))
                }
            }
        }
        return null
    }

    private fun handleException(e: Exception): String {
        val unknownError = context.getString(R.string.view_model_error_unknown) // 修改
        return when (e) {
            is SocketTimeoutException -> context.getString(R.string.view_model_error_timeout) // 修改
            is UnknownHostException -> context.getString(R.string.view_model_error_unknown_host) // 修改
            is IOException -> context.getString(R.string.view_model_error_network, e.message ?: unknownError) // 修改
            else -> context.getString(R.string.view_model_error_general, e.message ?: unknownError) // 修改
        }
    }

    private fun logDebug(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }

    private fun logError(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }

    // ============ 清理功能 ============

    fun clearOldDownloads() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val downloadDir = File(context.getExternalFilesDir(null), DownloadConfig.DOWNLOAD_DIR)
                val sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L
                val currentTime = System.currentTimeMillis()

                downloadDir.listFiles()?.forEach { file ->
                    if (currentTime - file.lastModified() > sevenDaysInMillis) {
                        if (file.delete()) {
                            logDebug("已清理旧文件: ${file.name}")
                        }
                    }
                }
            } catch (e: Exception) {
                logError("清理旧文件失败", e)
            }
        }
    }

    override fun onCleared() {
        releaseCache.clear()
        super.onCleared()
    }
}
