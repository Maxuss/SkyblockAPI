package space.maxus.api

data class ErrorEndpoint(override val code: Int, val error: String, override val message: String) : Endpoint {
    var trace: List<String>? = null
    constructor(code: Int, error: String, throwable: Exception) : this(code, error, "An internal server error occurred! ${throwable.message}") {
        trace = throwable.stackTrace.map { trace -> trace.toString() }
    }
    constructor(code: Int, error: String, throwable: Throwable) : this(code, error, "An internal server error occurred! ${throwable.message}") {
        trace = throwable.stackTrace.map { trace -> trace.toString() }
    }
}
