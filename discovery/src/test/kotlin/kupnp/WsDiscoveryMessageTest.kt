package kupnp

import com.google.common.truth.Truth.assertThat
import okio.ByteString
import org.junit.Test
import java.util.*

/**
 * Created by chris on 12/10/2016.
 */
class WsDiscoveryMessageTest {

    @Test
    fun buildProbeMessage() {
        val uuid = UUID.randomUUID()
        val result = WsDiscoveryMessage(uuid).buildProbeMessage()

        val expected = WsDiscoveryMessage.PROBE_MESSAGE.replace("{UUID}", uuid.toString()).replace("{PROBE_BODY}", "<d:Probe/>")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun buildProbeMessageAndBody() {
        val uuid = UUID.randomUUID()
        val result = WsDiscoveryMessage(uuid).apply { addType("NetworkVideoTransmitter") }.buildProbeMessage()

        val expected = """
        <s:Envelope xmlns:s="http://www.w3.org/2003/05/s-envelope" xmlns:a="http://schemas.xmlsoap.org/ws/2004/08/addressing" xmlns:d="http://schemas.xmlsoap.org/ws/2005/04/discovery" xmlns:dn="http://www.onvif.org/ver10/network/wsdl">
        <s:Header>
        <a:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</a:Action>
        <a:MessageID>urn:uuid:$uuid</a:MessageID>
        <a:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</a:To>
        </s:Header>
        <s:Body>
        <d:Probe><d:Types>dn:NetworkVideoTransmitter</d:Types></d:Probe>
        </s:Body>
        </s:Envelope>
        """.trimIndent()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun testBuildProbeBody_empty() {
        val uuid = UUID.randomUUID()
        val result = WsDiscoveryMessage(uuid).buildProbeBody()

        val expected = "<d:Probe/>"

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun testBuildProbeBody_addedType() {
        val uuid = UUID.randomUUID()
        val result = WsDiscoveryMessage(uuid).apply { addType("NetworkVideoTransmitter") }.buildProbeBody()

        val expected = "<d:Probe><d:Types>dn:NetworkVideoTransmitter</d:Types></d:Probe>"

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun getByteString() {
        val uuid = UUID.randomUUID()
        val result = WsDiscoveryMessage(uuid).byteString()

        val expected = ByteString.encodeUtf8(WsDiscoveryMessage.PROBE_MESSAGE.replace("{UUID}", uuid.toString()).replace("{PROBE_BODY}", "<d:Probe/>"))
        assertThat(result).isEqualTo(expected)
    }

}