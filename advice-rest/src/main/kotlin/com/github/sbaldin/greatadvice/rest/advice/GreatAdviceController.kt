package com.github.sbaldin.greatadvice.rest.advice

import com.github.sbaldin.greatadvice.rest.domain.GreatAdviceDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/advice")
class GreatAdviceController(
    @Autowired
    private val service: GreatAdviceService
) {


    @GetMapping("/random")
    fun random(): GreatAdviceDTO? = service.random()

    // Single item
    @GetMapping("/{id}")
    fun findAdviceById(@PathVariable id: Long): GreatAdviceDTO = service.findAdviceById(id) ?: throw AdviceNotFoundException(id)

    @GetMapping("")
    fun replaceEmployee(
        @RequestParam("tags") tags: List<String>?,
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("pagSize", required = false) pagSize: Int?
    ): Iterable<GreatAdviceDTO> = service.findAllByTag(tags ?: emptyList(), page ?: 1, pagSize ?: 20)
}

class AdviceNotFoundException(val id: Long) : RuntimeException("There are no advice with such id=$id!")