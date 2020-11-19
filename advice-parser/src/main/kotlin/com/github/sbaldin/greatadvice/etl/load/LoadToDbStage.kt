package com.github.sbaldin.greatadvice.etl.load

import com.github.sbaldin.greatadvice.db.GreatAdviceRepo
import com.github.sbaldin.greatadvice.etl.LastStage
import com.github.sbaldin.greatadvice.etl.transform.DownloadAdviceResult
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

private val log: Log = LogFactory.getLog(LoadToDbStage::class.java)

class LoadToDbStage(val repo: GreatAdviceRepo) : LastStage<DownloadAdviceResult> {

    val loadedAdviceIds: HashSet<String>

    init {
        loadedAdviceIds = repo.selectAll().mapTo(HashSet<String>(1000)) { it.id }
    }

    override fun next(value: DownloadAdviceResult) {
        log.info("Saving to db following values: ${value.data.size}")
        val newAdvices  = value.data.filterNot { it.id in loadedAdviceIds }.toSet()
        log.info("Following ids will be inserted: ${newAdvices.map { it.id }}")
        repo.butchInsert(newAdvices)
        loadedAdviceIds.addAll(newAdvices.map { it.id })
    }
}