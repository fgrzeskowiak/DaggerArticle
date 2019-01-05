package com.example.filip.daggerarticle.models

import com.google.gson.annotations.SerializedName

data class ArticleResponse(@SerializedName("files") val files: List<File> = emptyList(),
                           @SerializedName("article") val article: Article)


data class Article(@SerializedName("type") val type: String = "",
                   @SerializedName("title") val title: String = "",
                   @SerializedName("slug") val slug: String = "",
                   @SerializedName("published_at") val publishedAt: String = "",
                   @SerializedName("id") val id: Int = 0,
                   @SerializedName("collection") val collection: Boolean = false,
                   @SerializedName("body") val body: List<ArticleBodyItem> = emptyList(),
                   @SerializedName("background_id") val backgroundId: Int = 0)

data class ArticleBodyItem(@SerializedName("type") val type: String = "",
                           @SerializedName("text") val text: String = "",
                           @SerializedName("image_ids") val imageIds: List<Int>?,
                           @SerializedName("image_id") val imageId: Int?)

data class File(@SerializedName("id") val id: Int = 0,
                @SerializedName("download_url") val downloadUrl: String = "")
