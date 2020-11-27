package com.github.sbaldin.greatadvice.etl

import com.github.sbaldin.greatadvice.db.GreatAdviceRepo
import com.github.sbaldin.greatadvice.etl.extract.db.ExtractFromDbStage
import com.github.sbaldin.greatadvice.etl.extract.vk.ExtractAdviceUrlStage
import com.github.sbaldin.greatadvice.etl.extract.vk.VkAdviceUrlExtractor
import com.github.sbaldin.greatadvice.etl.load.LoadToDbStage
import com.github.sbaldin.greatadvice.etl.load.LoadToFile
import com.github.sbaldin.greatadvice.etl.transform.AdviceUrlDownloader
import com.github.sbaldin.greatadvice.etl.transform.DownloadAdviceStage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.FileSystems

val log: Logger = LoggerFactory.getLogger(EtlRunner::class.java)

class EtlRunner(
    adviceUrlExtractor: VkAdviceUrlExtractor,
    adviceUrlDownloader: AdviceUrlDownloader,
    repository: GreatAdviceRepo
) {

    private val extractStage = ExtractAdviceUrlStage(adviceUrlExtractor, 10)
    private val transformStage = DownloadAdviceStage(adviceUrlDownloader)
    private val loadStage = LoadToDbStage(repository)

    fun run() {
        log.info("Etl has started.")
        extractStage
            .bindNextStage(transformStage)
            .bindNextStage(loadStage)
            .run()
        log.info("Etl has finished.")
    }
}
class CsvExtractor(
    repository: GreatAdviceRepo
) {

    private val extractStage = ExtractFromDbStage(repository)
    private val loadStage = LoadToFile(FileSystems.getDefault().getPath("./advice.csv"))

    fun run() {
        log.info("Csv export has started.")
        extractStage
            .bindNextStage(loadStage)
            .run()
        log.info("Csv export has finished.")
    }
}