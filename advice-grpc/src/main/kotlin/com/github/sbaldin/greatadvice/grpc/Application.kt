package com.github.sbaldin.greatadvice.grpc

import com.github.sbaldin.greatadvice.db.config.DatabaseConfig
import com.github.sbaldin.greatadvice.db.repo.GreatAdviceRepo
import com.github.sbaldin.greatadvice.grpc.plugins.configureDependencyInjection
import com.github.sbaldin.greatadvice.grpc.plugins.configureRouting
import com.github.sbaldin.greatadvice.grpc.plugins.configureSerialization
import com.github.sbaldin.greatadvice.grpc.service.AdviceService
import io.ktor.server.application.*
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureDependencyInjection(appModule)
}

val appModule = module {
    single { readDBConf() }
    singleOf(::GreatAdviceRepo) {
        // definition options
        createdAtStart()
    }
    singleOf(::AdviceService){
        createdAtStart()
    }
  //  singleOf(::GreatAdviceRepo) { bind() }
}


fun readDBConf(
    appConfPath: String = "",
    resourcePath: String = "datasource-postgres.yaml"
): DatabaseConfig = Config().from.yaml.resource(resourcePath)
    .from.yaml.file(appConfPath, optional = true)
    .at("datasource").toValue()
