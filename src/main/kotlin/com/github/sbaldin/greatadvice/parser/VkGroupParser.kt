package com.github.sbaldin.greatadvice.parser

import com.vk.api.sdk.client.TransportClient
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.wall.responses.GetResponse
import com.vk.api.sdk.queries.wall.WallGetFilter
import org.slf4j.LoggerFactory


val logger = LoggerFactory.getLogger(VkGroupParser::class.java)


//see https://vk.com/dev/first_guide?f=3.%20%D0%90%D0%B2%D1%82%D0%BE%D1%80%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D1%8F%20%D0%BF%D0%BE%D0%BB%D1%8C%D0%B7%D0%BE%D0%B2%D0%B0%D1%82%D0%B5%D0%BB%D1%8F
//url to obtain token https://oauth.vk.com/authorize?client_id=7508458&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=friends&response_type=token&v=5.52
class VkGroupParser {

    private val OATH_TOKEN = "966e062de37677c5d72ddcadefc7250ee4f7ef6534a7a31177835d479596ed2d0d1c7083108350d0266a3"
    private val USER_ID = 13499857
    private val APP_ID = 7508458

    fun parse(handleUrl: (String?) -> Boolean) {
        val transportClient: TransportClient = HttpTransportClient.getInstance()
        val vk = VkApiClient(transportClient)

        val actor = UserActor(USER_ID, OATH_TOKEN)

        var itemCount = 100;
        var offset = 0;
        while (itemCount > 0 || offset == 100) {
            val response = getWallContent(vk, actor, itemCount, offset)
            logger.info("Response size ${response.items.size}, offset: $offset")
            offset += itemCount
            response.items.apply { itemCount = size }.forEach { wallPost ->
                wallPost.attachments?.forEach { attachment ->
                    attachment.link?.url.let {
                        val handled = handleUrl(it)
                        if(!handled){
                          logger.info("Post has empty link ${wallPost.id}, ${wallPost.attachments.joinToString()}!")
                        }
                    }
                }
            }
        }


    }

    private fun getWallContent(vk: VkApiClient, actor: UserActor, count: Int, offset: Int) = vk.wall()[actor]
            .ownerId(-23266096)
            .count(count)
            .offset(offset)
            .filter(WallGetFilter.OWNER)
            .execute()
}

