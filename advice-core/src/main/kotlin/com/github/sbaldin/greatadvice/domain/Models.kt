package com.github.sbaldin.greatadvice.domain

import kotlinx.serialization.Serializable


data class GreatAdviceResponse(
    val status: String,
    val errors: List<String>,
    val data: List<GreatAdvice>
)

@Serializable
data class GreatAdvice constructor(
    val id: String,
    val text: String,
    val html: String,
    val tags: List<String>,
    val conclusions: List<GreatAdviceConclusion>
)

@Serializable
data class GreatAdviceConclusion (
    val id: String,
    val text: String,
    val html: String
)
