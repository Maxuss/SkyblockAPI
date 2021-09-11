package space.maxus.api

import java.time.Instant
import java.util.*

data class UserProfile(val name: String, val uuid: UUID, val firstJoin: Long, val rank: String)

data class UsersEndpoint(override val code: Int, override val message: String) : Endpoint {
    val users = listOf(
        UserProfile("VoidMoment", UUID.randomUUID(), Instant.now().toEpochMilli(), "void"),
        UserProfile("Mr_Sketchpad", UUID.randomUUID(), Instant.now().toEpochMilli()-500, "sketch"),
        UserProfile("WispishWurm", UUID.randomUUID(), Instant.now().toEpochMilli()-6000, "admin"),
    )
}
