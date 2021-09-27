@file:Suppress("DeferredResultUnused")

package space.maxus

import com.google.gson.reflect.TypeToken
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import space.maxus.api.*
import space.maxus.json.JsonConverter
import space.maxus.util.Pages
import space.maxus.util.Security
import space.maxus.util.Static
import java.util.*
import kotlin.concurrent.schedule

suspend fun main(args: Array<String>) {
    println("Common key: ${Static.COMMON_API_KEY.value}")
    coroutineScope {
        async { EngineMain.main(args) }
    }
}

fun Application.module() {
    install(ContentNegotiation)
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
                val ep = Security.validateKey(call, UsersEndpoint.UsersEndpoint(200, "All users that have ever been on server"))
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
                val pep = try {
                    PlayerEndpoint.init(200, "Player Information", call.parameters["username"] ?: "undefined")
                } catch(e: ExceptionInInitializerError) {
                    ErrorEndpoint(404, "PLAYER_NOT_JOINED_BEFORE", "This player has not yet joined your server!")
                } catch(e: Exception) {
                    ErrorEndpoint(500, "INTERNAL_SERVER_ERROR", e)
                } catch(e: Throwable) {
                    ErrorEndpoint(500, "INTERNAL_SERVER_ERROR", e)
                }
                val ep = Security.validateKey(
                    call,
                    pep
                )
                call.respondText(
                    endpoint(ep),
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.OK
                )
            }
        }
    }

    routing {
        get("/api/slayers/{username}") {
            withContext(Dispatchers.Default) {
                val pep = try {
                    SlayerEndpoint.SlayerEndpoint(200, "Player's Slayer Data", call.parameters["username"] ?: "undefined")
                } catch(e: ExceptionInInitializerError) {
                    ErrorEndpoint(404, "PLAYER_NOT_DONE_SLAYERS", "This player has not yet done any slayers!")
                } catch(e: Exception) {
                    ErrorEndpoint(500, "INTERNAL_SERVER_ERROR", "An internal server error occurred! ${e.message}")
                }
                val ep = Security.validateKey(
                    call,
                    pep
                )
                call.respondText(
                    endpoint(ep),
                    contentType = ContentType.Application.Json,
                )
            }
        }
    }

    routing {
        get("/api/itemdata/{uuid}") {
            withContext(Dispatchers.Default) {
                val itemep = try {
                    ItemDataEndpoint(call.parameters["uuid"] ?: throw ExceptionInInitializerError("UUID not specified!"), Static.itembin[call.parameters["uuid"]] ?: throw ExceptionInInitializerError("This item is not stored!"), 200, "Item stored under provided UUID was found.")
                } catch(e: ExceptionInInitializerError) {
                    ErrorEndpoint(404, "ITEM_NOT_STORED", e.message?:"Error occurred!")
                } catch(e: Exception) {
                    ErrorEndpoint(500, "INTERNAL_SERVER_ERROR", "An internal server error occurred! ${e.message}")
                }
                val ep = Security.validateKey(
                    call,
                    itemep
                )
                call.respondText(endpoint(ep), contentType = ContentType.Application.Json)
            }
        }

        post("/api/itemdata") {

            withContext(Dispatchers.Default) {
                try {
                    val item = JsonConverter.gson.fromJson<ExpectedItemData>(call.receiveText(), object: TypeToken<ExpectedItemData>() { }.type)
                    val uuid = Security.generateSID()
                    Static.itembin[uuid] = item.data
                    call.respondText(
                        endpoint(
                            ItemDataEndpoint(
                                uuid,
                                item.data,
                                200,
                                "Successfully stored item data inside server API!"
                            )
                        ),
                        contentType = ContentType.Application.Json
                    )
                    Timer().schedule(10 * 60 * 1000L) {
                        Static.itembin.remove(uuid)
                    }
                } catch(e: Exception) {
                    call.respondText(
                        endpoint(
                            ErrorEndpoint(500, "Internal server error occurred", e)
                        ),
                        contentType = ContentType.Application.Json,
                    )
                }
            }
        }
    }
}

suspend fun endpoint(ep: Endpoint) : String {
    return coroutineScope {
        async {
            return@async JsonConverter.endpointJson(ep)
        }
    }.await()
}