package com.github.sbaldin.greatadvice.rest.advice

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.annotation.PostConstruct

@RestController
class HealthCheckController {

    lateinit var initTimestamp: ZonedDateTime

    @PostConstruct
    fun init(){
        initTimestamp = ZonedDateTime.now()
    }


    @GetMapping("/",)
    fun index(): String {
        val period = Duration.between(initTimestamp, ZonedDateTime.now()).seconds
        return "Status: Ok. Service - Alive. Launched $period sec."
    }
}
