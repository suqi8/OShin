package com.suqi8.oshin.ui.mainscreen.softupdate

import com.google.gson.annotations.SerializedName

data class GitHubAsset(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String?,
    @SerializedName("size")
    val size: Long,
    @SerializedName("download_count")
    val downloadCount: Int,
    @SerializedName("browser_download_url")
    val downloadUrl: String
)
