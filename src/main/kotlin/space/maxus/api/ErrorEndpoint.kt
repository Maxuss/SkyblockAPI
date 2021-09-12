package space.maxus.api

data class ErrorEndpoint(override val code: Int, val error: String, override val message: String) : Endpoint {
    var trace: List<StackTraceElement>? = null
    constructor(code: Int, error: String, throwable: Exception) : this(code, error, "An internal server error occurred! ${throwable.message}") {
        trace = throwable.stackTrace.toList()
    }
}
