package space.maxus.util

import space.maxus.api.Key
import space.maxus.api.PlayerSkills
import space.maxus.api.RankData
import space.maxus.api.SlayerSummary
import java.math.BigDecimal
import java.math.BigInteger
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
    @JvmField
    val rankContainers = hashMapOf<UUID, ConcurrentRoute<RankData>>()
    @JvmField
    val coins = hashMapOf<UUID, ConcurrentRoute<BigInteger>>()
    @JvmField
    val skills = hashMapOf<UUID, ConcurrentRoute<PlayerSkills>>()

    @JvmField
    val itembin = hashMapOf<String, String>()

    @JvmField
    var COMMON_API_KEY: Key = Key.generate(UUID.randomUUID())
}