package com.example.filip.daggerarticle.article

import com.appunite.paints.dagger.ActivityScope
import com.appunite.paints.dagger.UiScheduler
import com.example.filip.daggerarticle.article.ArticleDao
import io.reactivex.Observable
import io.reactivex.Scheduler
import org.funktionale.option.getOrElse
import javax.inject.Inject
import javax.inject.Named

@ActivityScope
class ArticlePresenter @Inject constructor(articleDao: ArticleDao,
                                           @UiScheduler uiScheduler: Scheduler,
                                           @Named("articleId") articleId: Int,
                                           @Named("simpleButtonClickObservable") clickObservable: Observable<Unit>) {

    val articleObservable: Observable<String> = clickObservable
            .switchMap {
                articleDao.articleObservable(articleId)
                        .observeOn(uiScheduler)
                        .subscribeOn(uiScheduler)
            }
            .map { it.fold({ it.message().getOrElse { "" } }, { it.article.title }) }
}