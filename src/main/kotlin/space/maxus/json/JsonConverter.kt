package space.maxus.json

import com.google.gson.*
import org.jetbrains.annotations.Nullable
import space.maxus.api.ErrorEndpoint

object JsonConverter {
    val gson : Gson = GsonBuilder().setPrettyPrinting().create()

    @JvmStatic
    fun endpointJson(@Nullable ep: Any) : String {
        return try {
            gson.toJson(ep)
        } catch(e: Exception) {
            gson.toJson(ErrorEndpoint(500, "INTERNAL_SERVER_ERROR", e.message ?: "undefined"))
        }
    }
}