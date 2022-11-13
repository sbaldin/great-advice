package com.github.sbaldin.greatadvice.db.table

import com.github.sbaldin.greatadvice.db.array
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.VarCharColumnType

object GreatAdviceTable : Table("great_advice") {
    val id = long("id").autoIncrement("great_advice_id_seq")
    val text = varchar("text", 200)
    val html = varchar("html", 500)
    val tags = array<String>("tags", VarCharColumnType(50))
    val conclusions = array<String>("conclusions", VarCharColumnType(500))

    override val primaryKey = PrimaryKey(id, name = "great_advice_pkey")
}