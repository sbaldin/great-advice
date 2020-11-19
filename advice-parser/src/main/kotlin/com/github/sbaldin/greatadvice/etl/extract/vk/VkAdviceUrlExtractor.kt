package com.github.sbaldin.greatadvice.etl.extract.vk

import com.github.sbaldin.greatadvice.etl.FirstStage
import com.vk.api.sdk.client.TransportClient
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.queries.wall.WallGetFilter
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger(VkAdviceUrlExtractor::class.java)


class ExtractAdviceUrlStage(private val vkAdviceUrlExtractor: VkAdviceUrlExtractor, val batchSize: Int) : FirstStage<ExtractAdviceUrlResult> {
    override fun hasNext(): Boolean = vkAdviceUrlExtractor.hasNextWallPost()
    override fun next(): ExtractAdviceUrlResult = vkAdviceUrlExtractor.extract(batchSize)
}

//see https://vk.com/dev/first_guide?f=3.%20%D0%90%D0%B2%D1%82%D0%BE%D1%80%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D1%8F%20%D0%BF%D0%BE%D0%BB%D1%8C%D0%B7%D0%BE%D0%B2%D0%B0%D1%82%D0%B5%D0%BB%D1%8F
//url to obtain token https://oauth.vk.com/authorize?client_id=7508458&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=friends&response_type=token&v=5.52
class VkAdviceUrlExtractor {

    private var extractedUrls = 0
    // let's assume that vk wall has at lest 100 posts
    private var totalExtractedUrls = INITIAL_OFFSET


    val transportClient: TransportClient = HttpTransportClient.getInstance()
    val vk = VkApiClient(transportClient)
    val actor = UserActor(USER_ID, OATH_TOKEN)


    fun extractAndHandle(handlerFn: (String?) -> Boolean) {
        var bacthSize = 100;
        while (hasNextWallPost()) {
            val response = getWallContent(vk, actor, bacthSize, totalExtractedUrls)
            logger.info("Response size ${response.items.size}, offset: $totalExtractedUrls")
            totalExtractedUrls += bacthSize
            response.items.apply { extractedUrls = size }.forEach { wallPost ->
                wallPost.attachments?.forEach { attachment ->
                    attachment.link?.url.let {
                        val handled = handlerFn(it)
                        if (!handled) {
                            logger.info("Post has empty link ${wallPost.id}, ${wallPost.attachments.joinToString()}!")
                        }
                    }
                }
            }
        }
    }

    fun extract(batchSize: Int = 100): ExtractAdviceUrlResult{
        logger.info("Attempt to extract advice url from vk wall posts.")
        val result =
            if(hasNextWallPost()){
            val response = getWallContent(vk, actor, batchSize, totalExtractedUrls)
            logger.info("VK wall posts have been received. Posts: ${response.items.size}, Offset: $totalExtractedUrls")

            response.items.apply {
                extractedUrls = size
                totalExtractedUrls += size
            }.mapNotNull { wallPost ->
                wallPost.attachments?.mapNotNull { attachment ->
                    attachment.link?.url
                }
            }.flatten()
        } else {
            logger.info("No more vk wall post to extract. \n Statistics totalExtractedUrls: $totalExtractedUrls")
            emptyList()
        }
        logger.info("Extrcted ${result.size} urls.")
        return ExtractAdviceUrlResult(result)
    }

    private fun getWallContent(vk: VkApiClient, actor: UserActor, count: Int, offset: Int) = vk.wall()[actor]
        .ownerId(-23266096)
        .count(count)
        .offset(offset)
        .filter(WallGetFilter.OWNER)
        .execute()

    fun hasNextWallPost() = extractedUrls > 0 || totalExtractedUrls == INITIAL_OFFSET


    companion object {
        // let's assume that wall contain at least 100 posts
        private const val INITIAL_OFFSET: Int = 100
        private const val OATH_TOKEN: String = "89c1a2231bacc4deff76eb801c67ab83cf032a98699e556631bf947134deabfafb617be6d9ec06f0e3fb5"
        private const val USER_ID: Int = 13499857
        private const val APP_ID: Int = 7508458
    }
}

data class ExtractAdviceUrlResult(
    val data:List<String>
)