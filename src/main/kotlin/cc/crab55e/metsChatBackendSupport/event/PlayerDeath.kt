package cc.crab55e.metsChatBackendSupport.event

import cc.crab55e.metsChatBackendSupport.MetsChatBackendSupport
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

class PlayerDeath(private val plugin: MetsChatBackendSupport) : Listener {
    private val logger = plugin.logger

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val client = plugin.getBackendSupportClient()
        if (client == null) {
            logger.warning("Failed to get BackendSupport Client.")
            return
        }
        val serverId = plugin.getServerId()
        if (serverId == null) {
            logger.warning("Failed to get serverId")
            return
        }
        val jsonMessageContent = GsonComponentSerializer.gson().serialize(event.deathMessage()!!.asComponent())

        client.sendEvent(
            "player_death_event",
            mapOf(
                "player" to mapOf(
                    "name" to event.player.name,
                    "uuid" to event.player.playerProfile.id.toString()
                )),
            jsonMessageContent
        )
    }
}