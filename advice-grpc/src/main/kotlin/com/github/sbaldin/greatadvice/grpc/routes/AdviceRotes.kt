package com.github.sbaldin.greatadvice.routes

import com.github.sbaldin.greatadvice.grpc.service.AdviceService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.adviceRouting() {

    val service by inject<AdviceService>()

    route("/api/v1/advices") {
        get() {
            val tags = call.request.queryParameters["tags"]
            val res = if (!tags.isNullOrBlank()) {
               service.getByTags(*tags.split(",").toTypedArray())
            } else{
                 service.get20()
            }
            if (res.isNotEmpty()) {
                call.respond(res)
            } else {
                call.respondText("No advice found", status = HttpStatusCode.OK)
            }
        }
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val advice = service.getById(id) ?: return@get call.respondText(
                "No great advice with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(advice)
        }
        get("random") {
            call.respond(service.random())
        }
        post {
            val cmd = call.receive<GreatAdviceCreateCmd>()
            service.create(cmd.text, cmd.html, cmd.tags)
            call.respondText("Great advice has been created.", status = HttpStatusCode.Created)

        }
        delete("{id?}") {
            call.respondText("Operation is not supported.", status = HttpStatusCode.MethodNotAllowed)
        }
    }
}

@Serializable
data class GreatAdviceCreateCmd constructor(
    val text: String,
    val html: String,
    val tags: List<String>,
)

