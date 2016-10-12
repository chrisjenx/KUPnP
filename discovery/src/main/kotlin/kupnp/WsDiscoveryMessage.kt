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
        const val PROBE_MESSAGE = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:tns=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\"><soap:Header><wsa:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</wsa:Action><wsa:MessageID>urn:uuid:{UUID}</wsa:MessageID><wsa:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</wsa:To></soap:Header><soap:Body><tns:Probe/></soap:Body></soap:Envelope>"
    }
}