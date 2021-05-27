package net.hyren.factions.listeners.connection

import net.hyren.factions.FactionsProvider
import net.hyren.factions.alpha.misc.player.list.data.PlayerList
import net.hyren.factions.misc.player.list.updatePlayerList
import org.bukkit.event.*
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @author Gutyerrez
 */
class PlayerJoinListener : Listener {

    @EventHandler
    fun on(
        event: PlayerJoinEvent
    ) {
        val player = event.player

        PlayerList.hideCommonPlayers(player)

        FactionsProvider.Cache.Local.FACTION_USER.provide().fetchByUserId(player.uniqueId)?.let {
            it.updatePlayerList()
        }
    }

}