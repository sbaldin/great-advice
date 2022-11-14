package com.github.sbaldin.greatadvice.grpc.plugins

import com.github.sbaldin.greatadvice.routes.adviceRouting
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    routing {
        adviceRouting()
    }
}
