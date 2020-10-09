package com.github.sbaldin.greatadvice.etl

import com.github.sbaldin.greatadvice.db.GreatAdviceRepo
import com.github.sbaldin.greatadvice.etl.extract.vk.ExtractAdviceUrlStage
import com.github.sbaldin.greatadvice.etl.extract.vk.VkAdviceUrlExtractor
import com.github.sbaldin.greatadvice.etl.load.LoadToDbStage
import com.github.sbaldin.greatadvice.etl.transform.AdviceUrlDownloader
import com.github.sbaldin.greatadvice.etl.transform.DownloadAdviceStage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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