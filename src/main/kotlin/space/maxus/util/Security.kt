package space.maxus.util

import io.ktor.application.*
import space.maxus.api.Endpoint
import space.maxus.api.ErrorEndpoint
import java.util.*


object Security {
    @JvmStatic
    fun validateKey(call: ApplicationCall, default: Endpoint) : Endpoint {
        if(Static.keys.isEmpty()) return default
        val query = call.request.queryParameters
        if(!query.contains("key")) {
            return ErrorEndpoint(401, "UNAUTHORIZED","Please provide a correct API key!")
        }
        val key = query["key"]
        if(Static.keys.any { k -> k.value.value == key }) return default

        return ErrorEndpoint(403, "FORBIDDEN", "Your API key is invalid!")
    }

    @JvmStatic
    fun generateSID(): String {
        val uuid = UUID.randomUUID()
        return (uuid.mostSignificantBits.toString(36) + uuid.leastSignificantBits.toString(36)).replace("-", "")
    }
}