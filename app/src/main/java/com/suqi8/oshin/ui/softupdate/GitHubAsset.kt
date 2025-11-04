package com.suqi8.oshin.ui.softupdate

import com.google.gson.annotations.SerializedName

data class GitHubAsset(
    val id: Long,
    val name: String,
    val size: Long,
    @SerializedName("download_count")
    val downloadCount: Int,
    @SerializedName("browser_download_url")
    val downloadUrl: String
)
