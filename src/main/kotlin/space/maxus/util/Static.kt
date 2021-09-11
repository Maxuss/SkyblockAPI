package space.maxus.util

import space.maxus.api.Key
import space.maxus.api.SlayerSummary
import java.math.BigDecimal
import java.util.*

object Static {
    @JvmField
    val allPlayers = listOf<UUID>()
    @JvmField
    val keys = hashMapOf<UUID, Key>()
    @JvmField
    val maxDamage = hashMapOf<UUID, BigDecimal>()
    @JvmField
    val slayerData = hashMapOf<UUID, ConcurrentRoute<SlayerSummary>>()
}