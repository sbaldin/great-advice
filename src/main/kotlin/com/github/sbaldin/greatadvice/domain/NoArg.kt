package com.github.sbaldin.greatadvice.domain

import kotlin.annotation.Target
import kotlin.annotation.AnnotationTarget
import kotlin.annotation.Retention
import kotlin.annotation.AnnotationRetention

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class NoArg