package space.maxus.api

import org.bukkit.Bukkit
import org.bukkit.Statistic
import space.maxus.util.Static
import java.util.*

data class PlayerEndpoint(
    override val code: Int,
    override val message: String,
    val player: String
) : Endpoint {
    val name: String
    val firstJoin: Long
    val lastJoin: Long
    val online: Boolean
    val deaths: Long
    val maxDamage: Double
    val uuid: UUID

    init {
        val p = Bukkit.getOfflinePlayer(player)
        if(!p.hasPlayedBefore()) {
            firstJoin = -1
            lastJoin = -1
            online = false
            name = p.name ?: "undefined"
            deaths = 0
            maxDamage = .0
            uuid = p.uniqueId
        } else {
            uuid = p.uniqueId
            name = p.name ?: "undefined"
            firstJoin = p.firstPlayed
            lastJoin = p.lastSeen
            online = p.isOnline
            deaths = p.getStatistic(Statistic.DEATHS).toLong()
            maxDamage = if (Static.maxDamage.containsKey(p.uniqueId)) Static.maxDamage[p.uniqueId]?.toDouble() ?: .0 else .0
        }
    }
}