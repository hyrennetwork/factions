package net.hyren.factions.user.data

import net.hyren.core.shared.*
import net.hyren.core.shared.users.data.User
import net.hyren.factions.FactionsProvider
import net.hyren.factions.user.role.Role
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.id.EntityID
import org.joda.time.DateTime
import java.math.RoundingMode
import java.util.*
import kotlin.math.roundToInt

/**
 * @author Gutyerrez
 */
data class FactionUser(
    val user: User,

    // Current faction role if don't have a faction this can be null

    var role: Role? = null,

    // Current faction if don't have a faction this can be null

    var factionId: EntityID<UUID>? = null,

    // Power

    var power: Double = 0.0,
    var maxPower: Double = 0.0,

    // Kills by default = 0

    var enemyKills: Int = 0,
    var neutralKills: Int = 0,
    var civilianKills: Int = 0,

    // Deaths by default = 0

    var enemyDeaths: Int = 0,
    var neutralDeaths: Int = 0,
    var civilianDeaths: Int = 0,

    // Settings

    var isMapAutoUpdating: Boolean = false,
    var isSeeingChunks: Boolean = false,

    // Timestamps

    override var createdAt: DateTime = DateTime.now(
        CoreConstants.DATE_TIME_ZONE
    ),

    override var updatedAt: DateTime? = null
) : User(
    user.id,
    user.name,
    user.email,
    user.discordId,
    user.twoFactorAuthenticationEnabled,
    user.twoFactorAuthenticationCode,
    user.twitterAccessToken,
    user.twitterTokenSecret,
    user.lastAddress,
    user.lastLobbyName,
    user.lastLoginAt,
    user.createdAt,
    user.updatedAt
) {


    fun getPowerRounded() = power.roundToInt()

    fun getMaxPowerRounded() = maxPower.roundToInt()

    fun getFaction() = if (factionId != null) {
        FactionsProvider.Cache.Local.FACTION.provide().fetchById(factionId!!)
    } else {
        null
    }

    fun getFactionName() = getFaction()?.name

    fun getFactionTag() = getFaction()?.tag

    fun getReceivedInvites() = FactionsProvider.Cache.Local.FACTION_INVITES.provide().fetchByFactionUserId(id) ?: emptyArray()

    fun getPlayer(): Player? = Bukkit.getPlayer(getUniqueId())

    fun getChunk() = if (getPlayer() == null) {
        throw NullPointerException()
    } else {
        getPlayer()!!.location.chunk
    }

    fun getTotalKills() = enemyKills + civilianDeaths + neutralKills

    fun getTotalDeaths() = enemyDeaths + civilianDeaths + neutralDeaths

    fun getKDR() = (getTotalKills() / if (getTotalDeaths() == 0) {
        1
    } else {
        getTotalDeaths()
    }).toBigDecimal().setScale(2, RoundingMode.UP).toPlainString().replace('.', ',')

    fun hasFaction() = factionId != null

    fun hasInvites() = getReceivedInvites().isNotEmpty()

    override fun isOnline(): Boolean = super.isOnline() && getConnectedBukkitApplication()?.server == CoreProvider.application.server

    override fun hashCode() = id.value.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (javaClass != other?.javaClass) return false

        other as FactionUser

        if (user.id != other.user.id) return false

        return true
    }

}
