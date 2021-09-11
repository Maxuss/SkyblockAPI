package space.maxus.api

import space.maxus.util.Static
import java.util.*
import java.util.concurrent.ThreadLocalRandom

data class Key(val owner: UUID, val value: String) {
    companion object {
        @JvmStatic
        fun generate(owner: UUID) : Key {
            val random: Random = ThreadLocalRandom.current()
            val r = ByteArray(24)
            random.nextBytes(r)
            val s: String = Base64.getUrlEncoder().encodeToString(r).replace("_", "G").replace("=", "F")
            val key = Key(owner, s)
            Static.keys[owner] = key
            return key
        }
    }

    override fun toString(): String {
        return "space.maxus.api.Key [owner=$owner & value=$value]"
    }
}