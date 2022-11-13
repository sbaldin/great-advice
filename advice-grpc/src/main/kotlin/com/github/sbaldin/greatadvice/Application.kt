package com.github.sbaldin.greatadvice

import io.ktor.server.application.*
import com.github.sbaldin.greatadvice.plugins.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureRouting()
    configureSerialization()
}