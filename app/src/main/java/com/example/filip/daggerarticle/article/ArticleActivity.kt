package com.example.filip.daggerarticle.article

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.appunite.paints.dagger.ActivityScope
import com.example.filip.daggerarticle.MainApplication
import com.example.filip.daggerarticle.R
import com.jakewharton.rxbinding2.view.clicks
import dagger.BindsInstance
import dagger.Provides
import empty
import io.reactivex.disposables.SerialDisposable
import kotlinx.android.synthetic.main.activity_article.*
import javax.inject.Named

class ArticleActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ARTICLE_ID = "extra_article_id"
        fun newIntent(context: Context, articleId: Int) = Intent(context, ArticleActivity::class.java)
                .putExtra(EXTRA_ARTICLE_ID, articleId)
    }

    private val disposable = SerialDisposable()
    private val component by lazy {
        DaggerArticleActivity_Component.builder()
                .mainComponent((application as MainApplication).appComponent())
                .activity(this)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        disposable.set(
                component.presenter.articleObservable
                        .subscribe {
                            article_title.text = it
                        }
        )

    }

    override fun onDestroy() {
        disposable.empty()
        super.onDestroy()
    }

    @ActivityScope
    @dagger.Component(dependencies = [MainApplication.Component::class], modules = [Component.Module::class])
    interface Component {

        val presenter: ArticlePresenter

        @dagger.Component.Builder
        interface Builder {
            fun build(): Component
            fun mainComponent(mainApplication: MainApplication.Component): Builder

            @BindsInstance
            fun activity(activity: AppCompatActivity): Builder
        }

        @dagger.Module
        class Module {

            @Provides
            @Named("articleId")
            fun articleId(activity: AppCompatActivity) = activity.intent.extras.getInt(EXTRA_ARTICLE_ID, 0)

            @Provides
            @Named("simpleButtonClickObservable")
            fun simpleButtonClickObservable(activity: AppCompatActivity) = activity.simple_button.clicks().share()
        }
    }
}
