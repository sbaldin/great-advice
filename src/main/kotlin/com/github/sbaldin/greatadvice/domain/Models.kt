package com.github.sbaldin.greatadvice.domain

data class GreatAdviceResponse(
    val status: String,
    val errors: List<String>,
    val data: List<GreatAdvice>
)


@NoArg
open class GreatAdvice constructor(
    val id: String,
    val text: String,
    val html: String,
    val tags: List<String>,
    val conclusions: List<String>
) {

    override fun toString(): String {
        return "$id,'$text','$html','{${tags.joinToString { "''$it''" }}}','{${conclusions.joinToString { "''$it''" }}}'"
    }
}
