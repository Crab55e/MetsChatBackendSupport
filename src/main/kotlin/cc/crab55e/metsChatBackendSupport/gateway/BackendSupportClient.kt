package cc.crab55e.metsChatBackendSupport.gateway

import cc.crab55e.metsChatBackendSupport.MetsChatBackendSupport
import com.google.gson.Gson
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class BackendSupportClient(
    private val plugin: MetsChatBackendSupport,
    private val host: String,
    private val port: Int,
    private val serverId: String
) {
    private val gson = Gson()

    fun sendEvent(eventName: String, data: Map<String, Any>, jsonComponent: String?) {
        val socket = Socket(host, port)
        socket.use {
            val secret = plugin.config.getString("proxy-server.secret") ?: return

            val writer = OutputStreamWriter(it.getOutputStream(), StandardCharsets.UTF_8).buffered()
            val isoTimestamp = DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneOffset.UTC)
                .format(Instant.ofEpochMilli(System.currentTimeMillis()))
            val jsonComponentSafe: String
            if (jsonComponent == null) {
                jsonComponentSafe = ""
            } else {
                jsonComponentSafe = jsonComponent
            }

            val message = mutableMapOf<String, Any>(
                "event" to eventName,
                "server_id" to serverId,
                "timestamp" to isoTimestamp,
                "data" to data,
                "json_component" to jsonComponentSafe
            )
            val messageString = gson.toJson(message)
            val signature = generateHMAC(messageString, secret)
            val payload = mutableMapOf<String, Any>(
                "message" to message,
                "signature" to signature
            )

            val json = gson.toJson(payload)

            writer.write(json + "\n")
            writer.flush()
        }
    }
    fun generateHMAC(message: String, secret: String): String {
        val algorithm = "HmacSHA256"
        val keySpec = SecretKeySpec(secret.toByteArray(), algorithm)
        val mac = Mac.getInstance(algorithm)
        mac.init(keySpec)
        val hmacBytes = mac.doFinal(message.toByteArray())
        return Base64.getEncoder().encodeToString(hmacBytes)
    }
}
