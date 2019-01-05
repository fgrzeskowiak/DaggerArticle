package com.example.filip.daggerarticle.dagger

import com.example.filip.daggerarticle.services.ArticleService
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Singleton

@Module
class RetrofitModule {

    object UnitConverterFactory : Converter.Factory() {

        override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
            return if (type == Unit::class.java) UnitConverter else null
        }

        private object UnitConverter : Converter<ResponseBody, Unit> {
            override fun convert(value: ResponseBody) = value.close()
        }
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit =
            Retrofit.Builder()
                    .baseUrl("https://api-paints-stage.appunite.net")
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(UnitConverterFactory)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

    @Provides
    @Singleton
    fun articleService(retrofit: Retrofit): ArticleService = retrofit.create(ArticleService::class.java)
}