@file:Suppress("DeferredResultUnused")

package space.maxus

import io.ktor.application.*
import io.ktor.client.utils.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import space.maxus.api.*
import space.maxus.json.JsonConverter
import space.maxus.util.Pages
import space.maxus.util.Security
import java.util.*

@OptIn(InternalAPI::class)
val thread = Dispatchers.clientDispatcher(1, "SbAPI")

suspend fun main(args: Array<String>) {
    val example = Key.generate(UUID.randomUUID())
    println("Example key: ${example.value}")
    coroutineScope {
        async { EngineMain.main(args) }
    }
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
            withContext(Dispatchers.Default) {
                call.respondText(
                    endpoint(
                        ErrorEndpoint(
                            404,
                            "NOT_FOUND",
                            "Endpoint not found: ${call.request.uri}"
                        )
                    ),
                    ContentType.Application.Json,
                    status = HttpStatusCode.NotFound
                )
            }
        }
        exception<Throwable> {
            withContext(Dispatchers.Default) {
                call.respondText(
                    endpoint(
                        ErrorEndpoint(
                            500,
                            "INTERNAL_SERVER_ERROR",
                            "Unknown internal error occurred"
                        )
                    ),
                    ContentType.Application.Json,
                    status = HttpStatusCode.InternalServerError
                )
            }
        }
    }

    routing {
        get("/api") {
            withContext(Dispatchers.Default) {
                call.respondText(
                    endpoint(HeaderEndpoint(200, "SkyblockSketchpad API v1")),
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.OK
                )
            }
        }
    }

    routing {
        get("/") {
            withContext(Dispatchers.Default) {
                call.respondText(
                    Pages.aboutPage,
                    contentType = ContentType.Text.Html
                )
            }
        }
    }

    routing {
        get("/api/users") {
            withContext(Dispatchers.Default) {
                val ep = Security.validateKey(call, UsersEndpoint(200, "All users that ever been on server"))
                call.respondText(
                    endpoint(ep),
                    contentType = ContentType.Application.Json,
                )
            }
        }
    }

    routing {
        get("/api/users/{username}") {
            withContext(Dispatchers.Default) {
                val ep = Security.validateKey(
                    call,
                    PlayerEndpoint(200, "Player information", call.parameters["username"] ?: "undefined")
                )
                call.respondText(
                    endpoint(ep),
                    contentType = ContentType.Application.Json,
                )
            }
        }
    }
}

suspend fun endpoint(ep: Endpoint) : String {
    return JsonConverter.endpointJson(ep)
}