package cc.crab55e.metsChatBackendSupport

import cc.crab55e.metsChatBackendSupport.event.PlayerDeath
import cc.crab55e.metsChatBackendSupport.gateway.BackendSupportClient
import cc.crab55e.metsChatBackendSupport.schedule.HeatBeat
import org.bukkit.plugin.java.JavaPlugin

class MetsChatBackendSupport : JavaPlugin() {
    private var backendSupportClient: BackendSupportClient? = null
    private var serverId: String? = null

    fun getBackendSupportClient(): BackendSupportClient? {
        return backendSupportClient
    }

    fun getServerId(): String? {
        return serverId
    }

    override fun onEnable() {
        saveResource("config.yml", false);
        saveDefaultConfig()

        serverId = config.getString("server_id")

        if (serverId == null) {
            logger.warning("required configuration \"server_id\" is null.")
            server.shutdown()
            return
        }

        val secret = config.getString("proxy-server.secret")
        if (secret == null || secret == "") {
            logger.warning("required configuration \"secret\" is null or empty. ")
            server.shutdown()
            return
        }

        val serverPort = config.getInt("proxy-server.port")
        var serverHost = config.getString("proxy-server.host")
        if (serverHost == null) {
            serverHost = "localhost"
            logger.info("Failed to get server host. replaced to localhost")
        }

        logger.info("Connecting to: $serverHost:$serverPort")
        backendSupportClient = BackendSupportClient(
            this,
            serverHost,
            serverPort,
            serverId!!
        )

        val pluginManager = server.pluginManager
        pluginManager.registerEvents(PlayerDeath(this), this)

        backendSupportClient!!.sendEvent(
            "plugin_enabled",
            mapOf(
                "name" to this.name
            ),
            null
        )
        logger.info("Connected")

        val scheduler = server.scheduler
        scheduler.runTaskTimer(this, HeatBeat(this), 20, 20)
    }

    override fun onDisable() {
        logger.info("Disabling...")
        backendSupportClient?.sendEvent(
            "plugin_disabled",
            mapOf(
                "name" to this.name
            ),
            null
        )
        logger.info("Disabled.")
    }
}
