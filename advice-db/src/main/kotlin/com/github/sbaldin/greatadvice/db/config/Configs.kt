package com.github.sbaldin.greatadvice.db.config

data class DatabaseConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String,
    val schema: SchemaConfig
)

data class SchemaConfig(
    val createOnStartup: Boolean,
    val populateSchema: Boolean,
    val ddlScript: String,
    val dataScript: String
)
