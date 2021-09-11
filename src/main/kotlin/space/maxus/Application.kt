package space.maxus

import io.ktor.server.netty.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import space.maxus.api.*
import space.maxus.json.JsonConverter
import space.maxus.util.Pages
import space.maxus.util.Security
import java.util.*

fun main(args: Array<String>) {
    val example = Key.generate(UUID.randomUUID())

    println("Example key: ${example.value}")
    EngineMain.main(args)
}

fun Application.module() {

    install(CORS) {
        anyHost()
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        header("authorization")
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }

    install(StatusPages) {
        statusFile(HttpStatusCode.NotFound, HttpStatusCode.InternalServerError, filePattern = "error#.html")
        exception<NotFoundException> {
            call.respondText(
                JsonConverter.endpointJson(ErrorEndpoint(404, "NOT_FOUND", "Endpoint not found: ${call.request.uri}")),
                ContentType.Application.Json
            )
        }
        exception<Throwable> {
            call.respondText(
                JsonConverter.endpointJson(ErrorEndpoint(500, "INTERNAL_SERVER_ERROR", "Unknown internal error occurred")),
                ContentType.Application.Json
            )
        }
    }

    routing {
        get("/api") {
            call.respondText(
                JsonConverter.endpointJson(HeaderEndpoint(200, "SkyblockSketchpad API v1")),
                contentType = ContentType.Application.Json
            )
        }
    }

    routing {
        get("/") {
            call.respondText(
                Pages.aboutPage,
                contentType = ContentType.Text.Html
            )
        }
    }

    routing {
        get("/api/users") {
            val ep = Security.validateKey(call, UsersEndpoint(200, "All users that ever been on server"))
            call.respondText(
                JsonConverter.endpointJson(ep),
                contentType = ContentType.Application.Json
            )
        }
    }

    routing {
        get("/api/users/{username}") {
            Security.validateKey(call, PlayerEndpoint(200, "Player information", call.parameters["username"] ?: "undefined"))
            call.respondText(
                """{"message": "The API successfully validated your access token."}""",
                contentType = ContentType.Application.Json
            )
        }
    }

    routing {

    }
}