package space.maxus.util

import space.maxus.api.Key
import java.math.BigDecimal
import java.util.*

object Static {
    @JvmField
    val allPlayers = listOf<UUID>()
    @JvmField
    val keys = hashMapOf<UUID, Key>()
    @JvmField
    val maxDamage = hashMapOf<UUID, BigDecimal>()
}