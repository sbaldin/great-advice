package com.github.sbaldin.greatadvice.grpc.plugins

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.core.module.Module

fun Application.configureDependencyInjection(appModule: Module) {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}