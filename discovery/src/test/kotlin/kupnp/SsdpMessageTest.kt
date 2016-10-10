package kupnp

import com.google.common.truth.Truth.assertThat
import okio.ByteString
import org.junit.Test

/**
 * Created by chris on 20/09/2016.
 */
class SsdpMessageTest {

    @Test
    fun fromByteString_typeValid() {
        val bytes = """
        HTTP/1.1 200 OK
        CACHE-CONTROL: max-age=3600
        EXT:
        LOCATION: http://192.168.88.1:2828/gateway.xml
        SERVER: RouterOS/6.36.3UPnP/1.0 MikroTik UPnP/1.0
        ST: upnp:rootdevice
        USN: uuid:UUID-MIKROTIK-INTERNET-GATEWAY-DEVICE-DXZR-V5M2::upnp:rootdevice

        """.trimIndent().byteString()

        val message = SsdpMessage.fromPacket(bytes)

        assertThat(message).isNotNull()
        assertThat(message.type).isEqualTo(SsdpMessage.TYPE.OK)
    }

    @Test
    fun fromByteString_headersValid() {
        val bytes = """
        HTTP/1.1 200 OK
        CACHE-CONTROL: max-age=3600
        EXT:
        LOCATION: http://192.168.88.1:2828/gateway.xml
        SERVER: RouterOS/6.36.3UPnP/1.0 MikroTik UPnP/1.0
        ST: upnp:rootdevice
        USN: uuid:UUID-MIKROTIK-INTERNET-GATEWAY-DEVICE-DXZR-V5M2::upnp:rootdevice

        """.trimIndent().byteString()

        val message = SsdpMessage.fromPacket(bytes)

        assertThat(message).isNotNull()
        assertThat(message.headers).containsEntry(SsdpMessage.HEADER_LOCATION, "http://192.168.88.1:2828/gateway.xml")
        assertThat(message.headers).containsEntry(SsdpMessage.HEADER_SERVER, "RouterOS/6.36.3UPnP/1.0 MikroTik UPnP/1.0")
        assertThat(message.headers).containsEntry(SsdpMessage.HEADER_SEARCH_TEXT, "upnp:rootdevice")
        assertThat(message.headers).containsEntry(SsdpMessage.HEADER_USN, "uuid:UUID-MIKROTIK-INTERNET-GATEWAY-DEVICE-DXZR-V5M2::upnp:rootdevice")
    }

    @Test
    fun fromByteString_isValid_true() {
        val bytes = """
        HTTP/1.1 200 OK
        CACHE-CONTROL: max-age=3600
        EXT:
        LOCATION: http://192.168.88.1:2828/gateway.xml
        SERVER: RouterOS/6.36.3UPnP/1.0 MikroTik UPnP/1.0
        ST: upnp:rootdevice
        USN: uuid:UUID-MIKROTIK-INTERNET-GATEWAY-DEVICE-DXZR-V5M2::upnp:rootdevice

        """.trimIndent().byteString()

        val message = SsdpMessage.fromPacket(bytes)

        assertThat(message).isNotNull()
        assertThat(message.isValid()).isTrue()
    }

    @Test
    fun fromByteString_isValid_false() {
        val bytes = """
        HTTP/1.1 200 OK
        EXT:
        LOCATION: http://192.168.88.1:2828/gateway.xml
        SERVER: RouterOS/6.36.3UPnP/1.0 MikroTik UPnP/1.0
        ST: upnp:rootdevice
        USN: uuid:UUID-MIKROTIK-INTERNET-GATEWAY-DEVICE-DXZR-V5M2::upnp:rootdevice

        """.trimIndent().byteString()

        val message = SsdpMessage.fromPacket(bytes)

        assertThat(message).isNotNull()
        assertThat(message.isValid()).isFalse()
    }

    @Test
    fun equalTrue() {
        val byte = """
        HTTP/1.1 200 OK
        USN: uuid:UUID-MIKROTIK-INTERNET-GATEWAY-DEVICE-DXZR-V5M2::upnp:rootdevice
        """.trimIndent().byteString()

        val one = SsdpMessage.fromPacket(byte)
        val two = SsdpMessage.fromPacket(byte)

        assertThat(one).isEqualTo(two)
        assertThat(one.hashCode()).isEqualTo(two.hashCode())
    }

    @Test
    fun equalFalse() {
        val byte = """
        HTTP/1.1 200 OK
        USN: uuid:UUID-MIKROTIK-INTERNET-GATEWAY-DEVICE-DXZR-V5M2::upnp:rootdevice
        """.trimIndent().byteString()
        val byte2 = """
        HTTP/1.1 200 OK
        USN: uuid:UUID-MIKROTIK-INTERNET-GATEWAY-DEVICE-DXZR-V5M3::upnp:rootdevice
        """.trimIndent().byteString()

        val one = SsdpMessage.fromPacket(byte)
        val two = SsdpMessage.fromPacket(byte2)

        assertThat(one).isNotEqualTo(two)
        assertThat(one.hashCode()).isNotEqualTo(two.hashCode())
    }

    fun String.byteString(): ByteString = ByteString.encodeUtf8(this)
}