package com.github.sbaldin.greatadvice.db.repo

import com.github.sbaldin.greatadvice.db.any
import com.github.sbaldin.greatadvice.db.table.GreatAdviceTable
import com.github.sbaldin.greatadvice.db.config.DatabaseConfig
import com.github.sbaldin.greatadvice.db.contains
import com.github.sbaldin.greatadvice.domain.GreatAdvice
import com.github.sbaldin.greatadvice.domain.GreatAdviceConclusion
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PSQLException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream

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

    fun initialize() {
        if (isInitialized) {
            log.info("Initialization is already done.")
            return
        }
        if (conf.schema.createOnStartup) {
            val ddlScriptPath = conf.schema.ddlScript.let {
                it.ifEmpty { "schema-postgresql.sql" }
            }

            val ddl = this.javaClass.classLoader.getResource(ddlScriptPath)!!.readText()
            transaction(db) {
                log.info("Create On Startup enabled. Will create schema.")
                log.info("Following script will be executed: $ddlScriptPath")
                exec(ddl)
            }

            if (conf.schema.populateSchema) {
                val dataScriptPath = conf.schema.dataScript.let {
                    it.ifEmpty { "postgres_fucking_great_advice_great_advice.sql" }
                }

                val data = this.javaClass.classLoader.getResourceAsStream(dataScriptPath)!!.run {
                    String(BufferedInputStream(this).readAllBytes())
                }.split("\n")
                transaction(db) {
                    log.info("Populate schema is enabled. Data will be written to tables.")
                    log.info("Following script will be executed: $dataScriptPath")
                    data.forEach { insertLine ->
                        try {
                            exec(insertLine)
                        } catch (ex: PSQLException) {
                            log.error("Trying to execute: \n $insertLine \n But error has been occurred!", ex)
                        }
                    }
                }
            }
        } else {
            log.info("Initialization on startup is disabled in config. See application.yaml.")
        }
        db
    }

    fun insertAdvice(value: GreatAdvice) = transaction(db) {
        addLogger(Slf4jSqlDebugLogger)
        GreatAdviceTable.insert {
            it[id] = value.id.toLong()
            it[text] = value.text
            it[html] = value.html
            it[tags] = value.tags.toTypedArray()
            it[conclusions] = value.conclusions.map { gson.toJson(it) }.toTypedArray()
        }
    }

    fun insertAdvice(text: String, html:String, tags: Array<String>) = transaction(db) {
        addLogger(Slf4jSqlDebugLogger)
        GreatAdviceTable.insert {
            it[this.text] = text
            it[this.html] = html
            it[this.tags] = tags
            it[conclusions] = arrayOf("{}")
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
        GreatAdviceTable.selectAll().map(::greatAdviceColumnMapper)
    }

    fun selectById(id: String): GreatAdvice? = transaction(db) {
        addLogger(Slf4jSqlDebugLogger)
        GreatAdviceTable.select { GreatAdviceTable.id.eq(id.toLong()) }.map(::greatAdviceColumnMapper).firstOrNull()
    }

    fun selectRandom(): GreatAdvice = transaction(db) {
        addLogger(Slf4jSqlDebugLogger)
        val count = GreatAdviceTable.slice(GreatAdviceTable.id).selectAll().count()
        val query = "SELECT * FROM fucking_great_advice.great_advice OFFSET floor(random() * $count) LIMIT 1"
        GreatAdviceTable.nativeSelect(query).map(::greatAdviceColumnMapper).first()
    }

    fun select(limit: Int) = transaction(db) {
        addLogger(Slf4jSqlDebugLogger)
        GreatAdviceTable.selectAll().limit(limit).map(::greatAdviceColumnMapper)
    }

    fun selectByTags(tags: Array<out String>): List<GreatAdvice> = transaction(db)  {
        addLogger(Slf4jSqlDebugLogger)
        GreatAdviceTable.select {
            GreatAdviceTable.tags.contains(tags)
        }.limit(20).map(::greatAdviceColumnMapper)
    }

}

// see https://github.com/JetBrains/Exposed/issues/118
private fun FieldSet.nativeSelect(query: String): List<ResultRow> {
    val fieldsIndex = realFields.toSet().mapIndexed { index, expression -> expression to index }.toMap()
    val resultRows = mutableListOf<ResultRow>()
    TransactionManager.current().exec(query) { resultSet ->
        while (resultSet.next()) {
            resultRows.add(ResultRow.create(resultSet, fieldsIndex))
        }
    }
    return resultRows
}

private fun greatAdviceColumnMapper(it: ResultRow) = GreatAdvice(
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

internal val log: Logger = LoggerFactory.getLogger(GreatAdviceRepo::class.java)
private val gson:Gson by lazy { GsonBuilder().setLenient().create() }
