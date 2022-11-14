package com.github.sbaldin.greatadvice.grpc.service

import com.github.sbaldin.greatadvice.db.repo.GreatAdviceRepo
import com.github.sbaldin.greatadvice.domain.GreatAdvice

class AdviceService (private val repo:GreatAdviceRepo) {

    fun getById(id: String): GreatAdvice? = repo.selectById(id)

    fun random(): GreatAdvice = repo.selectRandom()

    fun get20():List<GreatAdvice> = repo.select(20)

    fun getByTags(vararg tags:String):List<GreatAdvice> = repo.selectByTags(tags)

    fun create(text: String, html: String, tags: List<String>)  {
         repo.insertAdvice(text, html, tags.toTypedArray())
    }

}

