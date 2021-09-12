package space.maxus.api

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.bukkit.Bukkit
import space.maxus.util.Static
import java.util.*

data class RankData(val display: String, val short: String, val admin: Boolean)

data class UserProfile(
    val name: String,
    val uuid: UUID,
    val firstJoin: Long,
    val rank: RankData)

data class UsersEndpoint(override val code: Int, override val message: String, val users : List<UserProfile>? = null) : Endpoint {
    companion object {
        suspend fun UsersEndpoint(code: Int, message: String) : UsersEndpoint {
            return coroutineScope {
                async {
                    val offlines = Bukkit.getOfflinePlayers()
                    val list = mutableListOf<UserProfile>()
                    for (p in offlines) {
                        list.add(
                            UserProfile(
                                p.name ?: "undefined",
                                p.uniqueId,
                                p.firstPlayed,
                                Static.rankContainers[p.uniqueId]?.receive() ?: RankData("none", "non", false)
                            )
                        )
                    }
                    return@async UsersEndpoint(code, message, list)
                }
            }.await()
        }
    }
}
