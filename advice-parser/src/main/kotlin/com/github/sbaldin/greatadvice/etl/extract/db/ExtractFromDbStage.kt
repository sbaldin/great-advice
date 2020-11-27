package com.github.sbaldin.greatadvice.etl.extract.db

import com.github.sbaldin.greatadvice.db.GreatAdviceRepo
import com.github.sbaldin.greatadvice.domain.GreatAdvice
import com.github.sbaldin.greatadvice.etl.FirstStage
import com.github.sbaldin.greatadvice.etl.LastStage
import com.github.sbaldin.greatadvice.etl.transform.DownloadAdviceResult
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

private val log: Log = LogFactory.getLog(ExtractFromDbStage::class.java)

class ExtractFromDbStage(val repo: GreatAdviceRepo) : FirstStage<DownloadAdviceResult> {

    val adviceFromDb = repo.selectAll().iterator()

    override fun hasNext(): Boolean {
        return adviceFromDb.hasNext()
    }

    override fun next(): DownloadAdviceResult {
        return adviceFromDb.let {
           val batch = mutableListOf<GreatAdvice>()
            if(it.hasNext()){
                batch.add(it.next())
            }
            DownloadAdviceResult(batch.toSet())
        }
    }

    /* val loadedAdviceIds: HashSet<String>

       init {
           loadedAdviceIds = repo.selectAll().mapTo(HashSet<String>(1000)) { it.id }
       }

       override fun next(value: DownloadAdviceResult) {
           log.info("Saving to db following values: ${value.data.size}")
           val newAdvices  = value.data.filterNot { it.id in loadedAdviceIds }.toSet()
           log.info("Following ids will be inserted: ${newAdvices.map { it.id }}")
           repo.butchInsert(newAdvices)
           loadedAdviceIds.addAll(newAdvices.map { it.id })
       }*/
}