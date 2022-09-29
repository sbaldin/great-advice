package com.github.sbaldin.greatadvice.rest.advice

import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders


@SpringBootTest
@AutoConfigureMockMvc
open class HealthCheckControllerTest {
    @Autowired
    private val mvc: MockMvc? = null

    @Throws(Exception::class)
    @Test
    fun health() {
        mvc!!.perform(MockMvcRequestBuilders.get("/health/"))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("Status: Ok. Service - Alive")))
    }
}