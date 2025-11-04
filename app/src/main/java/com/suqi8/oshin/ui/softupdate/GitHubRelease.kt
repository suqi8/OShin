package com.suqi8.oshin.ui.softupdate

import com.google.gson.annotations.SerializedName

data class GitHubRelease(
    val id: Long,
    val name: String,
    @SerializedName("tag_name")
    val tagName: String,
    val body: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("assets")
    val assets: List<GitHubAsset>
)
