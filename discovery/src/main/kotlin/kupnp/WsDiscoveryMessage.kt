package kupnp

import okio.ByteString
import java.util.*

/**
 * Created by chris on 12/10/2016.
 */
class WsDiscoveryMessage(private val uuid: UUID = UUID.randomUUID()) {

    internal val types = mutableListOf<String>()

    internal fun buildProbeMessage(): String {
        return PROBE_MESSAGE.replace("{UUID}", uuid.toString()).replace("{PROBE_BODY}", buildProbeBody())
    }

    /**
     * Build the Probe body, defaults to `<d:Probe/>` if no types or
     */
    internal fun buildProbeBody(): String {
        if (types.isEmpty()) return EMPTY_PROBE
        return buildString {
            append(PROBE_START)
            append(TYPE_START)
            types.forEachIndexed { i, s ->
                if (i > 0) append(' ')
                append("dn:").append(s)
            }
            append(TYPE_END)
            append(PROBE_END)
        }
    }

    /**
     * Add Type to search for. e.g. NetworkVideoTransmitter, remember an invalid search type might yeild no search results.
     */
    fun addType(type: String) = types.add(type)

    /**
     * Remove search type.
     */
    fun removeType(type: String) = types.remove(type)

    fun byteString(): ByteString = ByteString.encodeUtf8(buildProbeMessage())

    override fun toString(): String {
        return "WsDiscoveryMessage(probeMessage=${buildProbeMessage()})"
    }

    companion object {
        const val DEFAULT_TIMEOUT = 4
        const val DEFAULT_IP_ADDRESS = MulticastDiscovery.DEFAULT_WS_DISCOVERY_MULTICAST_IP
        const val DEFAULT_PORT = MulticastDiscovery.DEFAULT_WS_DISCOVERY_PORT
        private const val EMPTY_PROBE = "<d:Probe/>"
        private const val PROBE_START = "<d:Probe>"
        private const val PROBE_END = "</d:Probe>"
        private const val TYPE_START = "<d:Types>"
        private const val TYPE_END = "</d:Types>"
        val PROBE_MESSAGE = """
        <s:Envelope xmlns:s="http://www.w3.org/2003/05/s-envelope" xmlns:a="http://schemas.xmlsoap.org/ws/2004/08/addressing" xmlns:d="http://schemas.xmlsoap.org/ws/2005/04/discovery" xmlns:dn="http://www.onvif.org/ver10/network/wsdl">
        <s:Header>
        <a:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</a:Action>
        <a:MessageID>urn:uuid:{UUID}</a:MessageID>
        <a:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</a:To>
        </s:Header>
        <s:Body>
        {PROBE_BODY}
        </s:Body>
        </s:Envelope>
        """.trimIndent()
    }
}