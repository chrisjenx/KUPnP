package kupnp

import okio.ByteString
import kotlin.properties.Delegates

/**
 * Created by chris on 25/08/2016.
 * For project kupnp
 */
class SsdpMessage {

    val headers = mutableMapOf<String, String>()
    val mx: Int
        get() = headers.getOrElse(HEADER_MX, { DEFAULT_MX.toString() }).toInt()
    var type: TYPE by Delegates.notNull<TYPE>()
        private set

    internal constructor(type: TYPE) {
        this.type = type
    }

    internal constructor(bytes: ByteString) {
        val lines = bytes.utf8().split(NL)

        when (lines.first().trim()) {
            TYPE.M_SEARCH.headerLine -> type
            TYPE.OK.headerLine -> type
            else -> TYPE.UNKNOWN
        }
        lines.subList(1, lines.size).forEach { value ->
            val keyValue = value.split(':', limit = 2)
            if (value.length >= 2) {
                headers.put(keyValue[0].toUpperCase(), keyValue[1])
            }
        }
    }

    fun byteString(): ByteString {
        return buildString {
            append(type.headerLine).append(NL)
            headers.forEach {
                append(it.key).append(": ").append(it.value).append(NL)
            }
            append(NL)
        }.let { ByteString.encodeUtf8(it) }
    }

    companion object {
        private const val DEFAULT_MX = 3
        private const val SSDP_HOST = "${SsdpControlPoint.SSDP_IP}:${SsdpControlPoint.SSDP_PORT}"
        private const val SSDP_DISCOVER = "\"ssdp:discover\""
        private const val NL = "\r\n"
        /**
         *  Field value contains Search Target. Single URI. The response sent by the device depends on the field value of the
         *  ST header field that was sent in the request. In some cases, the device MUST send multiple response messages as follows. If
         *  the received ST field value was:
         */
        const val HEADER_SEARCH_TEXT = "ST"
        /**
         *  Field value contains maximum wait time in seconds. MUST be greater than or equal to 1 and SHOULD be less than
         *  5 inclusive. Device responses SHOULD be delayed a random duration between 0 and this many seconds to balance load for
         *  the control point when it processes responses. This value MAY be increased if a large number of devices are expected to
         *  respond. The MX field value SHOULD NOT be increased to accommodate network characteristics such as latency or
         *  propagation delay (for more details, see the explanation below). Specified by UPnP vendor. Integer.
         */
        const val HEADER_MX = "MX"
        const val HEADER_HOST = "HOST"
        const val HEADER_MAN = "MAN"
        /**
         *  Field value contains a URL to the UPnP description of the root device. Normally the host portion contains a
         *  literal IP address rather than a domain name in unmanaged networks. Specified by UPnP vendor.
         *  Single absolute URL (see RFC 3986).
         */
        const val HEADER_LOCATION = "LOCATION"
        /**
         *  Specified by UPnP vendor. String. Field value MUST begin with the following “product tokens” (defined by
         *  HTTP/1.1). The first product token identifes the operating system in the form OS name/OS version, the second token
         *  represents the UPnP version and MUST be UPnP/1.1, and the third token identifes the product using the form
         *  product name/product version. For example, “SERVER: unix/5.1 UPnP/1.1 MyProduct/1.0”. Control points MUST be
         *  prepared to accept a higher minor version number of the UPnP version than the control point itself implements. For
         *  example, control points implementing UDA version 1.0 will be able to interoperate with devices implementing
         *  UDA version 1.1.
         */
        const val HEADER_SERVER = "SERVER"

        /**
         * Create M-SEARCH multicast packet with the applied search string.
         *
         * @param searchString This is the ST header, Default search string is "upnp:rootdevice"
         * @param mx this is the maximum wait time a UPnP device will wait before sending a respond 1..5 seconds.
         */
        fun search(searchString: String = "upnp:rootdevice", mx: Int = DEFAULT_MX) = SsdpMessage(TYPE.M_SEARCH).apply {
            headers.put(HEADER_HOST, SSDP_HOST)
            headers.put(HEADER_MAN, SSDP_DISCOVER)
            headers.put(HEADER_MX, mx.toString())
            headers.put(HEADER_SEARCH_TEXT, searchString)
        }

        /**
         * Turns a packet into a SSDP Message
         */
        fun fromPacket(source: ByteString) = SsdpMessage(source)

        enum class TYPE(val headerLine: String) {
            M_SEARCH("M-SEARCH * HTTP/1.1"),
            OK("HTTP/1.1 200 OK"),
            UNKNOWN("")
        }

    }

}
