package com.github.sbaldin.greatadvice.domain

data class DatabaseConfig(
   val url : String,
   val driver : String,
   val user : String,
   val password : String,
   val schema: SchemaConfig
)

data class SchemaConfig(
   val createOnStartup: Boolean,
   val ddlScript: String
)
