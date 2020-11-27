package com.github.sbaldin.greatadvice.ai

import com.github.sbaldin.greatadvice.ai.domain.AIGeneratorConfig
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger(Application::class.java)

fun readAiConf(
    appConfPath: String = "",
    resourcePath: String = "application.yaml"
) = Config().from.yaml.resource(resourcePath)
    .from.yaml.file(appConfPath, optional = true)
    .at("datasource").toValue<AIGeneratorConfig>()


class Application {

    fun start(){
        log.info("Reading configurations.")
        println("User dir:" + System.getProperty("user.dir"))
        val executionPath = System.getProperty("user.dir")
        println("Executing at =>" + executionPath.replace("\\", "/"))
        val aiConf = readAiConf()
    }
}


fun main(args: Array<String>) {
    Application().start()
}

