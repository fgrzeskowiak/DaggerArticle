package com.example.filip.daggerarticle

import android.app.Application
import android.content.Context
import com.appunite.paints.dagger.ComputationScheduler
import com.appunite.paints.dagger.ForApplication
import com.appunite.paints.dagger.NetworkScheduler
import com.appunite.paints.dagger.UiScheduler
import com.example.filip.daggerarticle.dagger.NetworkModule
import com.example.filip.daggerarticle.dagger.RetrofitModule
import com.example.filip.daggerarticle.dagger.SchedulersModule
import com.example.filip.daggerarticle.services.ArticleService
import dagger.BindsInstance
import dagger.Provides
import io.reactivex.Scheduler
import javax.inject.Singleton

class MainApplication : Application() {

    private val component by lazy {
        DaggerMainApplication_Component.builder()
                .application(this)
                .build()
    }

    fun appComponent(): Component = component

    @Singleton
    @dagger.Component(modules = [
        NetworkModule::class,
        SchedulersModule::class,
        Component.Module::class,
        RetrofitModule::class
    ])
    interface Component {

        @ForApplication
        fun context(): Context

        @UiScheduler
        fun uiScheduler(): Scheduler

        @NetworkScheduler
        fun networkScheduler(): Scheduler

        @ComputationScheduler
        fun computationScheduler(): Scheduler

        fun articleService(): ArticleService

        @dagger.Component.Builder
        interface Builder {

            fun build(): Component

            @BindsInstance
            fun application(application: MainApplication): Builder
        }

        @dagger.Module
        class Module {

            @Provides
            @ForApplication
            fun context(application: MainApplication): Context = application
        }
    }
}