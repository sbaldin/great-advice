package com.github.sbaldin.greatadvice.etl.transform

import com.github.sbaldin.greatadvice.domain.GreatAdvice
import com.github.sbaldin.greatadvice.etl.Stage
import com.github.sbaldin.greatadvice.etl.extract.site.GreatAdviceIterator
import com.github.sbaldin.greatadvice.etl.extract.vk.ExtractAdviceUrlResult
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

private val log: Log = LogFactory.getLog(AdviceUrlDownloader::class.java)


class DownloadAdviceStage(val adviceUrlDownloader:AdviceUrlDownloader) :
    Stage<ExtractAdviceUrlResult, DownloadAdviceResult> {
    override fun next(value: ExtractAdviceUrlResult): DownloadAdviceResult {
        return adviceUrlDownloader.download(value.data)
    }
}

class AdviceUrlDownloader(
    val adviceRestService: GreatAdviceIterator
) {
    val validUrlregex = Regex("""^[(http(s)?):\/\/(www\.)?a-zA-Z0-9@:%._\+~#=-]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)$""")
    //Some of post doesn't contain url with advice id, but have only base url of site, such urls should be skipped
    val emptyUrlRegex = Regex("fucking-great-advice.ru$")

    // see url validation regex https://regexr.com/39nr7
    fun isValidUrl(url: String): Boolean = !emptyUrlRegex.matches(url) && validUrlregex.matches(url)

    fun download(urls: List<String>): DownloadAdviceResult {
        val data = urls.filter {
            isValidUrl(it).apply {
                if (!this) {
                    log.info("Filtered invalid or empty Url $it.")
                }
            }
        }.map {
            download(it)
        }.flatten().toSet()

        return DownloadAdviceResult(data)
    }

    private fun download(url: String): List<GreatAdvice> {
        log.info("Downloading from $url started.")
        val greatAdviseId = url.removePrefix("https://fucking-great-advice.ru/").split("/").reversed()
            .run { if (size > 1) get(1) else "" }.let {
            try {
                it.toLong()
            } catch (e: Exception) {
                log.error("Can't parse url $url")
                url.removePrefix("https://fucking-great-advice.ru/").split("/").last().let {
                    log.error("Trying to parse $url again")
                    try {
                        it.toLong()
                    } catch (e: Exception) {
                        0L
                    }
                }
            }
        }
        if(greatAdviseId == 0L ){
            return emptyList()
        }
        log.info("Extracted start id of advice $greatAdviseId for batch download.")
        return adviceRestService.doRequest(greatAdviseId).let { adviseResponse ->
            if (adviseResponse != null && adviseResponse.status == "success") {
                val data = adviseResponse.data
                try{
                    log.info("Advices received: " + data.size)
                    data.forEach {
                        log.info("Advices: " +it.id)
                        log.info("Advices: " + it)
                    }
                }catch (e:Exception){
                    log.error("Something wrong with advice data")
                }
                Thread.sleep(500);
                data
            } else emptyList()
        }
    }
}

data class DownloadAdviceResult(
    val data: Set<GreatAdvice>
)

