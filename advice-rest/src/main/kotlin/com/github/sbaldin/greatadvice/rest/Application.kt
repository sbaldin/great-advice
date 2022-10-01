package com.github.sbaldin.greatadvice.rest

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


val log: Logger = LoggerFactory.getLogger(Application::class.java)

@SpringBootApplication(scanBasePackages = ["com.github.sbaldin.greatadvice.rest.advice"])
class Application {
}



fun main(args: Array<String>) {
   runApplication<Application>(*args)
}