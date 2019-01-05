package com.example.filip.daggerarticle.utils

import org.funktionale.option.Option

interface DefaultError {
    fun message(): Option<String> = Option.None

    fun code(): Option<Int> = Option.None
}