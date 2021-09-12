package space.maxus.api

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import space.maxus.util.Encoder
import space.maxus.util.Static
import java.util.*

data class CertainSkill(
    val level: Int,
    val totalExperience: Double,
    val name: String
)

data class PlayerSkills(
    val skills: List<CertainSkill>
) {
    val totalExperience: Double
    get() {
        return skills.sumOf { selector -> selector.totalExperience }
    }
    val skillAverage: Double
    get() {
        val filtered = skills.sumOf { sk -> sk.level }
        return filtered.div(skills.size.toDouble())
    }
}

data class PlayerEndpoint(
    override val code: Int,
    override val message: String,
    val username: String
) : Endpoint {
    var firstJoin: Long? = null
    var lastJoin: Long? = null
    var online: Boolean? = null
    var maxDamage: Double? = null
    var uuid: UUID? = null
    var rank: RankData? = null
    var coins: Double? = null
    var armor: String? = null
    var inventory: String? = null
    var enderChest: String? = null
    var skills: PlayerSkills? = null

    companion object {
        suspend fun init(code: Int, message: String, username: String) : Endpoint {
            return coroutineScope {
                async {
                    try {
                        println("Started getting endpoint! $username")
                        @Suppress("DEPRECATION")
                        val p = Bukkit.getOfflinePlayer(username)
                        println("Got all players!")
                        if (!p.hasPlayedBefore() || p.player == null) {
                            println("Player didn't play before!")
                            throw ExceptionInInitializerError("Exit")
                        }
                        println("Player played!")
                        val pl = p.player!!
                        val ep = PlayerEndpoint(code, message, username)
                        println("After getting endpoint")
                        ep.firstJoin = pl.firstPlayed
                        ep.lastJoin = pl.lastLogin
                        ep.online = pl.isOnline
                        println("Before getting max damage")
                        ep.maxDamage = (Static.maxDamage[p.uniqueId] ?: .0).toDouble()
                        ep.uuid = pl.uniqueId
                        println("Before getting rank")
                        ep.rank = Static.rankContainers[p.uniqueId]?.receive() ?: RankData("none", "non", false)
                        println("Before getting coins")
                        ep.coins = (Static.coins[p.uniqueId]?.receive() ?: 0).toDouble()
                        println("Before getting armor")
                        ep.armor = Encoder.encodeStacks(pl.inventory.armorContents)
                        println("Before getting inventory")
                        ep.inventory = Encoder.encodeStacks(pl.inventory.contents)
                        println("Before getting enderchest")
                        ep.enderChest = Encoder.encodeStacks(pl.enderChest.contents)
                        println("Before getting skills")
                        ep.skills = Static.skills[p.uniqueId]?.receive() ?: PlayerSkills(listOf())
                        return@async ep
                    } catch(e: Exception) {
                        return@async ErrorEndpoint(500, "INTERNAL_SERVER_ERROR", e)
                    } catch(e: Throwable) {
                        return@async ErrorEndpoint(500, "INTERNAL_SERVER_ERROR", e)
                    }
                }
            }.await()
        }
    }

}