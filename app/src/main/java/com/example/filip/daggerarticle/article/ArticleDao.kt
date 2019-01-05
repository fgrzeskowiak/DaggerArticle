package com.example.filip.daggerarticle.article

import com.appunite.paints.dagger.NetworkScheduler
import com.example.filip.daggerarticle.services.ArticleService
import com.example.filip.daggerarticle.utils.DefaultError
import com.example.filip.daggerarticle.models.ArticleResponse
import io.reactivex.Observable
import io.reactivex.Scheduler
import org.funktionale.either.Either
import toEither
import javax.inject.Inject

class ArticleDao @Inject constructor(articleService: ArticleService,
                                     @NetworkScheduler networkScheduler: Scheduler) {

    val articleObservable: (Int) -> Observable<Either<DefaultError, ArticleResponse>> = { articleId ->
        articleService.getArticle(articleId)
                .subscribeOn(networkScheduler)
                .toObservable()
                .toEither()
                .replay(1)
                .refCount()
    }
}