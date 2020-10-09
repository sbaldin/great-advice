package com.github.sbaldin.greatadvice.db

import com.github.sbaldin.greatadvice.Application
import com.github.sbaldin.greatadvice.domain.DatabaseConfig
import com.github.sbaldin.greatadvice.domain.GreatAdvice
import com.github.sbaldin.greatadvice.domain.GreatAdviceConclusion
import com.github.sbaldin.greatadvice.readDBConf
import com.google.gson.GsonBuilder
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger(GreatAdviceRepo::class.java)


//TODO IMPROVE take a look to https://github.com/JetBrains/Exposed/wiki/DataBase-and-DataSource HikariDataSource
class GreatAdviceRepo(
    private val conf: DatabaseConfig
) {
    private var isInitialized = false
    private val db by lazy {
        conf.run {
            Database.connect(url = url, driver = driver, user = user, password = password)
        }
    }
    private val gson by lazy {
        GsonBuilder()
            .setLenient()
            .create()
    }

    fun initialize() {
        if(isInitialized){
            log.info("Initialization is already done.")
            return
        }
        if (conf.schema.createOnStartup) {
            val ddlScript = conf.schema.ddlScript.let {
                if (it.isNotEmpty()) it else "schema-postgresql.sql"
            }

            val ddl = this.javaClass.classLoader.getResource(ddlScript)!!.readText()
            transaction(db) {
                log.info("Create On Startup enabled. Will create schema.")
                log.info("Following script will be executed:\n $ddl")
                exec(ddl)
            }
        } else {
            log.info("Initialization on startup is disabled in config. See application.yaml.")
        }
        db
    }

    fun insert(value: GreatAdvice) = transaction(db) {
        addLogger(Slf4jSqlDebugLogger)
        GreatAdviceTable.insert {
            it[id] = value.id.toLong()
            it[text] = value.text
            it[html] = value.html
            it[tags] = value.tags.toTypedArray()
            it[conclusions] = value.conclusions.map { gson.toJson(it) }.toTypedArray()
        }
    }

    fun butchInsert(values: Set<GreatAdvice>, batchSize: Int = 100) =
        values.chunked(batchSize).forEach { chunk ->
            transaction(db) {
                addLogger(Slf4jSqlDebugLogger)
                GreatAdviceTable.batchInsert(chunk) { value ->
                    this[GreatAdviceTable.id] = value.id.toLong()
                    this[GreatAdviceTable.text] = value.text
                    this[GreatAdviceTable.html] = value.html
                    this[GreatAdviceTable.tags] = value.tags.toTypedArray()
                    this[GreatAdviceTable.conclusions] = value.conclusions.map { gson.toJson(it) }.toTypedArray()
                }
            }
        }

    fun selectAll(): List<GreatAdvice> = transaction(db) {
        addLogger(Slf4jSqlDebugLogger)
        GreatAdviceTable.selectAll().map {
            GreatAdvice(
                id = it[GreatAdviceTable.id].toString(),
                text = it[GreatAdviceTable.text],
                html = it[GreatAdviceTable.html],
                tags = it[GreatAdviceTable.tags].toList(),
                conclusions = it[GreatAdviceTable.conclusions].map {
                    gson.fromJson(
                        it,
                        GreatAdviceConclusion::class.java
                    )
                }
            )
        }
    }

    fun selectById(id: String): List<GreatAdvice> = transaction(db) {
        addLogger(Slf4jSqlDebugLogger)
        GreatAdviceTable.select { GreatAdviceTable.id.eq(id.toLong()) }.map {
            GreatAdvice(
                id = it[GreatAdviceTable.id].toString(),
                text = it[GreatAdviceTable.text],
                html = it[GreatAdviceTable.html],
                tags = it[GreatAdviceTable.tags].toList(),
                conclusions = it[GreatAdviceTable.conclusions].map {
                    gson.fromJson(
                        it,
                        GreatAdviceConclusion::class.java
                    )
                }
            )
        }
    }
}


object GreatAdviceTable : Table("great_advice") {
    val id = long("id").autoIncrement("great_advice_id_seq")
    val text = varchar("text", 200)
    val html = varchar("html", 500)
    val tags = array<String>("tags", VarCharColumnType(50))
    val conclusions = array<String>("conclusions", VarCharColumnType(500))

    override val primaryKey = PrimaryKey(id, name = "great_advice_pkey")
}