package space.maxus

import io.ktor.server.netty.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import space.maxus.api.HeaderEndpoint
import space.maxus.api.Key
import space.maxus.api.PlayerEndpoint
import space.maxus.json.JsonConverter
import space.maxus.api.UsersEndpoint
import space.maxus.util.Security
import java.util.*

fun main(args: Array<String>) {
    val first = Key.generate(UUID.randomUUID())
    val second = Key.generate(UUID.randomUUID())

    println("Example key: ${first.value}")
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

    routing {
        get("/") {
            val ep = Security.validateKey(call, HeaderEndpoint(200, "SkyblockSketchpad API v1"))
            call.respondText(
                JsonConverter.endpointJson(ep),
                contentType = ContentType.Application.Json
            )
        }
    }

    routing {
        get("/users") {
            val ep = Security.validateKey(call, UsersEndpoint(200, "All users that ever been on server"))
            call.respondText(
                JsonConverter.endpointJson(ep),
                contentType = ContentType.Application.Json
            )
        }
    }

    routing {
        get("/users/{username}") {
            Security.validateKey(call, PlayerEndpoint(200, "Player information", call.parameters["username"] ?: "undefined"))
            call.respondText(
                """{"message": "The API successfully validated your access token."}""",
                contentType = ContentType.Application.Json
            )
        }
    }
}