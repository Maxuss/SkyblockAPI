package space.maxus.util

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ConcurrentRoute<T> {
    private var data : T? = null

    suspend infix fun transfer(value: T) {
        return coroutineScope {
            async {
                data = value
            }
        }.await()
    }

    suspend fun receive() : T {
        return coroutineScope {
            async {
                return@async data ?: throw IllegalStateException("Tried to receive data before transferring it!")
            }
        }.await()
    }
}