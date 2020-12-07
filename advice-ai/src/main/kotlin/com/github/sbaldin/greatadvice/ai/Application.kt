package com.github.sbaldin.greatadvice.ai

import com.github.sbaldin.greatadvice.ai.csv.AdviceLineIterator
import com.github.sbaldin.greatadvice.ai.csv.StemSentenceProcessor
import com.github.sbaldin.greatadvice.ai.domain.AIGeneratorConfig
import com.github.sbaldin.greatadvice.domain.asResource
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

val log: Logger = LoggerFactory.getLogger(Application::class.java)

fun readAiConf(
    appConfPath: String = "",
    resourcePath: String = "application.yaml"
) = Config().from.yaml.resource(resourcePath)
    .from.yaml.file(appConfPath, optional = true)
    .at("ai").toValue<AIGeneratorConfig>()


class Application {

    fun start() {
        log.info("Reading configurations.")
        println("User dir:" + System.getProperty("user.dir"))
        val executionPath = System.getProperty("user.dir")
        println("Executing at =>" + executionPath.replace("\\", "/"))
        val aiConf = readAiConf().learnDatasetFilePath
        println(aiConf)
        /*"advice.csv".asResource {
            val stemmer = StemSentenceProcessor(AdviceLineIterator(it))
            stemmer.process()
        }*/
        Word2VecRawTextExample.main(emptyArray())
    }
}


fun main(args: Array<String>) {
    Application().start()
}

