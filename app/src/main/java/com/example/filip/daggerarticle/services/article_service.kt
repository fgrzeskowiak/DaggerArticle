package com.example.filip.daggerarticle.services

import com.example.filip.daggerarticle.models.ArticleResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ArticleService {
    @GET("api/articles/{id}")
    fun getArticle(@Path("id") id: Int): Single<ArticleResponse>
}