package cc.crab55e.metsChatBackendSupport.schedule

import cc.crab55e.metsChatBackendSupport.MetsChatBackendSupport
import me.lucko.spark.api.Spark
import me.lucko.spark.api.statistic.StatisticWindow
import org.bukkit.Bukkit
import org.bukkit.plugin.RegisteredServiceProvider


class HeatBeat(private val plugin: MetsChatBackendSupport) : Runnable {
    private val logger = plugin.logger
    override fun run() {
        val client = plugin.getBackendSupportClient()
        if (client == null) {
            logger.warning("Failed to get BackendSupport Client.")
            return
        }

        val provider: RegisteredServiceProvider<Spark>? = Bukkit.getServicesManager().getRegistration(Spark::class.java)
        if (provider != null) {
            val spark: Spark = provider.provider
            val tps = spark.tps()?.poll(StatisticWindow.TicksPerSecond.SECONDS_5) ?: -1.0
            client.sendEvent(
                "heartbeat",
                mapOf(
                    "enabled_spark" to true,
                    "tps" to tps
                ),
                null
            )
        } else {
            client.sendEvent(
                "heartbeat",
                mapOf(
                    "enabled_spark" to false,
                    "tps" to plugin.server.tps
                ),
                null
            )
        }
    }
}