package com.example.filip.daggerarticle.dagger

import android.content.Context
import com.appunite.paints.dagger.ForApplication
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.moczul.ok2curl.CurlInterceptor
import com.moczul.ok2curl.logger.Loggable
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Singleton


@Module
class NetworkModule {

    @Singleton
    @Provides
    fun loggingInterceptor(): Interceptor = CurlInterceptor(CurlLoggerLoggable())

    class CurlLoggerLoggable : Loggable {

        override fun log(s: String) {
            println("" + s)
            LOGGER.log(Level.FINE, s)
        }

        companion object {
            private val LOGGER = Logger.getLogger("Curl")
        }
    }

    @Singleton
    @Provides
    internal fun createCache(@ForApplication context: Context): Cache {
        val httpCacheDir = File(context.cacheDir, "cache")
        val httpCacheSize = (150 * 1024 * 1024).toLong() // 150 MiB
        return Cache(httpCacheDir, httpCacheSize)
    }

    @Singleton
    @Provides
    internal fun createOkkHttpClient(cache: Cache, loggingInterceptor: Interceptor): OkHttpClient {

        val builder = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor { chain ->
                    chain.proceed(chain.request().newBuilder()
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .build())
                }
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(loggingInterceptor)

        return builder.build()
    }

    @Singleton
    @Provides
    internal fun createGson(): Gson {
        return GsonBuilder()
                .create()
    }

}