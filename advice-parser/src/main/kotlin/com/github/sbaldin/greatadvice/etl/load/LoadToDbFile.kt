package com.github.sbaldin.greatadvice.etl.load

import com.github.sbaldin.greatadvice.db.GreatAdviceRepo
import com.github.sbaldin.greatadvice.etl.LastStage
import com.github.sbaldin.greatadvice.etl.transform.DownloadAdviceResult
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import java.io.File
import java.nio.file.Path

private val log: Log = LogFactory.getLog(LoadToFile::class.java)

class LoadToFile(val path : Path) : LastStage<DownloadAdviceResult> {

    val savedReports = mutableSetOf<String>()
    val file: File = File(path.toUri())

    override fun next(value: DownloadAdviceResult) {
        createNewFileIfNotExists()
        log.info("Saving to file following values: ${value.data.size}")
        val strBuffer = StringBuffer()
        value.data.forEach {
            strBuffer.appendln("""${it.id}, "${it.text.replace(",","")}", [${it.tags.joinToString()}] """ )
        }
        log.info("Following ids will be inserted: ${value.data.map { it.id }}")
        file.appendText(strBuffer.toString())
    }

    private fun createNewFileIfNotExists() {
        if (!file.exists()) {
            file.createNewFile()
        }
    }
}