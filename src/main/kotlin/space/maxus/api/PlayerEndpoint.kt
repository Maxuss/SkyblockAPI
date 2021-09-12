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

    companion object {
        @JvmStatic
        fun empty() : PlayerSkills {
            val list = mutableListOf<CertainSkill>()
            for(skill in listOf("combat", "mining", "fishing", "foraging", "farming", "alchemy", "enchanting")) {
                list.add(CertainSkill(0, .0, skill))
            }
            return PlayerSkills(list)
        }
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
                        if (!p.hasPlayedBefore() || p.player == null) {
                            throw ExceptionInInitializerError("Exit")
                        }
                        val pl = p.player!!
                        val ep = PlayerEndpoint(code, message, username)
                        ep.firstJoin = pl.firstPlayed
                        ep.lastJoin = pl.lastLogin
                        ep.online = pl.isOnline
                        ep.maxDamage = (Static.maxDamage[p.uniqueId] ?: .0).toDouble()
                        ep.uuid = pl.uniqueId
                        ep.rank = Static.rankContainers[p.uniqueId]?.receive() ?: RankData("none", "non", false)
                        ep.coins = (Static.coins[p.uniqueId]?.receive() ?: 0).toDouble()
                        ep.armor = Encoder.encodeStacks(pl.inventory.armorContents)
                        ep.inventory = Encoder.encodeStacks(pl.inventory.contents)
                        ep.enderChest = Encoder.encodeStacks(pl.enderChest.contents)
                        try {
                            ep.skills = Static.skills[p.uniqueId]?.receive() ?: PlayerSkills(listOf())
                        } catch (e: IllegalStateException) {
                            ep.skills = PlayerSkills(listOf())
                        }
                        return@async ep
                    } catch(e: ExceptionInInitializerError) {
                        return@async ErrorEndpoint(404, "PLAYER_NOT_PLAYED_BEFORE", "Player has not yet played on server!")
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