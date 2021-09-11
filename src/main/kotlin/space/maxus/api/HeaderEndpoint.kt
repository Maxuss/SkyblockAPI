package space.maxus.api

data class AvailableEndpoint(val path: String, val description: String)

class HeaderEndpoint(override val code: Int, override val message: String) : Endpoint {
    val availableEndpoins = listOf(
        AvailableEndpoint("/", "Info endpoint. Contains info on API."),
        AvailableEndpoint("/api", "Main API endpoint. Contains status of whole API."),
        AvailableEndpoint("/api/users", "Shows all registerd user names on the server."),
        AvailableEndpoint("/api/users/:username", "Displays information about certain player."),
        AvailableEndpoint("/api/slayers/:username", "Displays slayer data of certain player")
    )
}