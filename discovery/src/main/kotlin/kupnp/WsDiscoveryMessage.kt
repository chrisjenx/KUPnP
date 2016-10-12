package kupnp

import okio.ByteString
import java.util.*

/**
 * Created by chris on 12/10/2016.
 */
class WsDiscoveryMessage(private val uuid: UUID = UUID.randomUUID()) {

    internal fun buildProbeMessage(): String {
        return PROBE_MESSAGE.replace("{UUID}", uuid.toString())
    }

    fun byteString(): ByteString = ByteString.encodeUtf8(buildProbeMessage())

    override fun toString(): String {
        return "WsDiscoveryMessage(probeMessage=${buildProbeMessage()})"
    }


    companion object {
        const val DEFAULT_TIMEOUT = 4
        const val DEFAULT_IP_ADDRESS = MulticastDiscovery.DEFAULT_WS_DISCOVERY_MULTICAST_IP
        const val DEFAULT_PORT = MulticastDiscovery.DEFAULT_WS_DISCOVERY_PORT
        const val PROBE_MESSAGE = "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/s-envelope\" xmlns:a=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:d=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\"><s:Header><a:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</a:Action><a:MessageID>urn:uuid:{UUID}</a:MessageID><a:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</a:To></s:Header><s:Body><d:Probe/></s:Body></s:Envelope>"
    }
}