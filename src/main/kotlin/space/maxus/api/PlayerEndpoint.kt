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
        suspend fun init(code: Int, message: String, player: String) : PlayerEndpoint {
            return coroutineScope {
                async {
                    @Suppress("DEPRECATION") val p = Bukkit.getOfflinePlayer(player)
                    if(!p.hasPlayedBefore() || p !is Player) {
                        throw ExceptionInInitializerError("Exit")
                    }
                    val ep = PlayerEndpoint(code, message, player)
                    ep.firstJoin = p.firstPlayed
                    ep.lastJoin = p.lastLogin
                    ep.online = p.isOnline
                    ep.maxDamage = (Static.maxDamage[p.uniqueId] ?: .0).toDouble()
                    ep.uuid = p.uniqueId
                    ep.rank = Static.rankContainers[p.uniqueId]?.receive() ?: RankData("none", "non", false)
                    ep.coins = (Static.coins[p.uniqueId]?.receive() ?: 0).toDouble()
                    ep.armor = Encoder.encodeStacks(p.inventory.armorContents)
                    ep.inventory = Encoder.encodeStacks(p.inventory.contents)
                    ep.enderChest = Encoder.encodeStacks(p.enderChest.contents)
                    ep.skills = Static.skills[p.uniqueId]?.receive() ?: PlayerSkills(listOf())
                    return@async ep
                }
            }.await()
        }
    }

}