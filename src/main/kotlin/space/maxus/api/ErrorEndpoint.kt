package space.maxus.api

data class ErrorEndpoint(
    override val code: Int,
    val error: String,
    override val message: String) : Endpoint
