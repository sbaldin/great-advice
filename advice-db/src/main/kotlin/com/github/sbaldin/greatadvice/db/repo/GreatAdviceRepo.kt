package com.github.sbaldin.greatadvice.db.repo

import com.github.sbaldin.greatadvice.db.table.GreatAdviceTable
import com.github.sbaldin.greatadvice.db.config.DatabaseConfig
import com.github.sbaldin.greatadvice.domain.GreatAdvice
import com.github.sbaldin.greatadvice.domain.GreatAdviceConclusion
import com.google.gson.GsonBuilder
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PSQLException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader

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
        if (isInitialized) {
            log.info("Initialization is already done.")
            return
        }
        if (conf.schema.createOnStartup) {
            val ddlScriptPath = conf.schema.ddlScript.let {
                if (it.isNotEmpty()) it else "schema-postgresql.sql"
            }

            val ddl = this.javaClass.classLoader.getResource(ddlScriptPath)!!.readText()
            transaction(db) {
                log.info("Create On Startup enabled. Will create schema.")
                log.info("Following script will be executed:\n $ddlScriptPath")
                exec(ddl)
            }

            if (conf.schema.populateSchema) {
                val dataScriptPath = conf.schema.dataScript.let {
                    if (it.isNotEmpty()) it else "postgres_fucking_great_advice_great_advice.sql"
                }

                val data = this.javaClass.classLoader.getResource(dataScriptPath)!!.run {
                    FileReader(File(this.toURI())).readLines()
                }
                transaction(db) {
                    log.info("Populate schema is enabled. Data will be written to tables.")
                    log.info("Following script will be executed:\n $dataScriptPath")
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

