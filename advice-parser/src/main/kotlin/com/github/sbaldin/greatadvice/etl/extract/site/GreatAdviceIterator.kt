package com.github.sbaldin.greatadvice.etl.extract.site

import com.github.sbaldin.greatadvice.domain.GreatAdvice
import com.google.gson.GsonBuilder
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private val log: Log = LogFactory.getLog(GreatAdviceIterator::class.java)

private interface FuckingGreatAdviceService {
    @Headers("Content-Type: application/json")
    @GET("api/v2/random-advices")
    fun getAdvicesStartFromId(@Query("startID") adviceId: Long?): Call<GreatAdviceResponse>
}

data class GreatAdviceResponse(
    val status: String,
    val errors: List<String>,
    val data: List<GreatAdvice>
)

class GreatAdviceIterator(
    private val requestDelay: Long = 500,
    private val failedRequestThreshold: Int = 5,
    startId: Long) : Iterator<GreatAdviceResponse?> {

    private val service: FuckingGreatAdviceService
    private var currentId = startId
    private var failedRequestCount = 0

    init {
        val gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit = Retrofit.Builder()
                .client(getUnsafeOkHttpClient())
                .baseUrl("http://fucking-great-advice.ru")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        service = retrofit.create(FuckingGreatAdviceService::class.java)
    }

    fun doRequest(Id: Long) = processGreatAdviceResponse(service.getAdvicesStartFromId(Id), Id)

    override fun hasNext(): Boolean = currentId < 5000 || failedRequestCount == failedRequestThreshold

    override fun next() = processGreatAdviceResponse(service.getAdvicesStartFromId(currentId++))

    private fun processGreatAdviceResponse(it: Call<GreatAdviceResponse>, id:Long = currentId): GreatAdviceResponse? {
        Thread.sleep(requestDelay)
        log.info("Execute request to ${it.request().url}")

        val res = it.execute()
        log.info("currentId is $id")
        if (res.isSuccessful) {
            return res.body()
        } else if (failedRequestCount <= failedRequestThreshold) {
            failedRequestCount++
            return processGreatAdviceResponse(it)
        } else {
            log.info("Last request id is ${id}")
            return null
        }

    }
}