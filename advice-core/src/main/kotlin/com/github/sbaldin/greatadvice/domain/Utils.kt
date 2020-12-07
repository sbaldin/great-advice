package com.github.sbaldin.greatadvice.domain

import java.io.File

fun String.asResource(work: (File) -> Unit) {
    val resourceFile = getResourceAsFile(this)
    work(resourceFile)
}

fun getResourceAsFile(name:String): File {
    return File(Thread.currentThread().contextClassLoader.getResource(name).toURI())
}