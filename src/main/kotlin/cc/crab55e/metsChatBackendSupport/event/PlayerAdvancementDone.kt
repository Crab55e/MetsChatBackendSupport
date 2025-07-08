package cc.crab55e.metsChatBackendSupport.event

import cc.crab55e.metsChatBackendSupport.MetsChatBackendSupport
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.event.player.PlayerAdvancementDoneEvent

class PlayerAdvancementDone(private val plugin: MetsChatBackendSupport) : Listener {
    private val logger = plugin.logger

    @EventHandler
    fun onPlayerAdvancementDone(event: PlayerAdvancementDoneEvent) {
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
        val eventMessage = event.message() ?: return
        val jsonMessageContent = GsonComponentSerializer.gson().serialize(eventMessage.asComponent())

        client.sendEvent(
            "player_advancement_done_event",
            mapOf(
                "player" to mapOf(
                    "name" to event.player.name,
                    "uuid" to event.player.playerProfile.id.toString()
                )
            ),
            jsonMessageContent
        )
    }
}