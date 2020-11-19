package com.github.sbaldin.greatadvice.domain

data class GreatAdviceResponse(
    val status: String,
    val errors: List<String>,
    val data: List<GreatAdvice>
)


//@NoArg
data class GreatAdvice constructor(
    val id: String,
    val text: String,
    val html: String,
    val tags: List<String>,
    val conclusions: List<GreatAdviceConclusion>
) {

}

data class GreatAdviceConclusion (
    val id: String,
    val text: String,
    val html: String
)
