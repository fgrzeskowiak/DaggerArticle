package com.example.filip.daggerarticle

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.filip.daggerarticle.article.ArticleActivity
import com.jakewharton.rxbinding2.view.clicks
import empty
import io.reactivex.disposables.SerialDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val disposable = SerialDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        disposable.set(

                button.clicks()
                        .subscribe { startActivity(ArticleActivity.newIntent(this, 123)) }
        )
    }

    override fun onDestroy() {
        disposable.empty()
        super.onDestroy()
    }
}
