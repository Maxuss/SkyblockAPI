package space.maxus.api

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.bukkit.Bukkit
import space.maxus.util.ConcurrentRoute
import space.maxus.util.Static
import java.util.*

data class CertainSlayerSummary(
    val slayerType: String,
    val level: Int,
    val totalExperience: Int,
    val toNext: Int
)

data class SlayerSummary(
    val player: UUID,
    val totalExperience: Int,
    val total: List<CertainSlayerSummary>,
    val unlockedAutoSlayer: Boolean = total.all { cont -> cont.level >= 5 }
)

data class SlayerEndpoint internal constructor(override val code: Int, override val message: String, val player: String, val data: SlayerSummary) : Endpoint {
    companion object {
        suspend fun SlayerEndpoint(code: Int, message: String, player: String) : SlayerEndpoint {
            val p = Bukkit.getOfflinePlayer(player)
            if(!p.hasPlayedBefore()) {
                throw ExceptionInInitializerError("Player not joined before")
            }
            val id = p.uniqueId
            if(!Static.slayerData.containsKey(id)) {
                Static.slayerData[id] = ConcurrentRoute()
            }
            val route = Static.slayerData[id]!!
            return coroutineScope {
                async {
                    val data = route.receive()
                    return@async SlayerEndpoint(code, message, player, data)
                }
            }.await()
        }
    }
}
