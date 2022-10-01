package com.github.sbaldin.greatadvice.rest.advice

import com.github.sbaldin.greatadvice.rest.domain.GreatAdviceDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tags")
class GreatAdviceTagController(
    @Autowired
    private val service: GreatAdviceService
) {

    @GetMapping("")
    fun tags(): List<String> = service.tags()

}