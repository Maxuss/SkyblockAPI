package space.maxus.api

data class AvailableEndpoint(val path: String, val description: String)

class HeaderEndpoint(override val code: Int, override val message: String) : Endpoint {
    val availableEndpoins = listOf(
        AvailableEndpoint("/", "Main API endpoint. Contains status of whole API."),
        AvailableEndpoint("/users", "Shows all registerd user names on the server."),
        AvailableEndpoint("/users/:username", "Displays information about certain player."),
        AvailableEndpoint("/slayers/:username", "Displays slayer data of certain player")
    )
}