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

        val expected = WsDiscoveryMessage.PROBE_MESSAGE.replace("{UUID}", uuid.toString())
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun getByteString() {
        val uuid = UUID.randomUUID()
        val result = WsDiscoveryMessage(uuid).byteString()

        val expected = ByteString.encodeUtf8(WsDiscoveryMessage.PROBE_MESSAGE.replace("{UUID}", uuid.toString()))
        assertThat(result).isEqualTo(expected)
    }

}