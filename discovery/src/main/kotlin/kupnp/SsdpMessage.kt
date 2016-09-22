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
        val lines = bytes.utf8().split("\n")

        type = when (lines.first().trim()) {
            TYPE.M_SEARCH.headerLine -> TYPE.M_SEARCH
            TYPE.OK.headerLine -> TYPE.OK
            else -> TYPE.UNKNOWN
        }
        lines.subList(1, lines.size).forEach { value ->
            val keyValue = value.split(':', limit = 2)
            if (value.length >= 2) {
                headers.put(keyValue[0].toUpperCase(), keyValue[1].trim())
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

    /**
     * Based on the UPnP 1.1 spec, this will check for required headers being set for the current Message Type.
     */
    fun isValid(): Boolean {
        var valid = true
        type.requiredHeaders.forEach {
            if (!headers.containsKey(it)) {
                valid = false
                return@forEach
            }
        }
        return valid
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as SsdpMessage

        // We only Compare Type and USN right now, so doesn't really work for discovery mechanisms
        if (type != other.type) return false
        if (headers[HEADER_USN] != other.headers[HEADER_USN]) return false

        return true
    }

    override fun hashCode(): Int {
        var code = 1
        code = 31 * code + type.hashCode()
        code = 31 * code + headers.getOrElse(HEADER_USN, { "" }).hashCode()
        return code
    }

    companion object {
        private const val DEFAULT_MX = 3
        private const val SSDP_HOST = "${SsdpControlPoint.SSDP_IP}:${SsdpControlPoint.SSDP_PORT}"
        private const val SSDP_DISCOVER = "\"ssdp:discover\""
        private const val NL = "\r\n"
        /**
         *  Field value contains Search Target. Single URI. The response sent by the controlpoint depends on the field value of the
         *  ST header field that was sent in the request. In some cases, the controlpoint MUST send multiple response messages as follows. If
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
        /**
         * REQUIRED by HTTP Extension Framework. Unlike the NTS and ST field values, the field value of the MAN header field is
         * enclosed in double quotes; it defines the scope (namespace) of the extension. MUST be "ssdp:discover".
         */
        const val HEADER_MAN = "MAN"
        /**
         *  Field value MUST have the max-age directive (“max-age=”) followed by an integer that specifies the number of
         *  seconds the advertisement is valid. After this duration, control points SHOULD assume the controlpoint (or service) is no longer
         *  available; as long as a control point has received at least one advertisement that is still valid from a root controlpoint, any of its
         *  embedded devices or any of its services, then the control point can assume that all are available. The number of seconds
         *  SHOULD be greater than or equal to 1800 seconds (30 minutes), although exceptions are defined in the text above. Specified
         *  by UPnP vendor. Other directives MUST NOT be sent and MUST be ignored when received.
         */
        const val HEADER_CACHE_CONTROL = "CACHE-CONTROL"
        /**
         * For backwards compatibility with UPnP 1.0. (Header field name only; no field value.)
         */
        const val HEADER_EXT = "EXT"
        /**
         *  Field value contains a URL to the UPnP description of the root controlpoint. Normally the host portion contains a
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
         *  Field value contains Unique Service Name. (See list of required field values for the USN header field in NOTIFY
         *  with ssdp:alive above.) Single URI.
         */
        const val HEADER_USN = "USN"

        /**
         * Create M-SEARCH multicast packet with the applied search string.
         *
         * @param searchString This is the ST header, Default search string is "upnp:rootdevice"
         * @param mx this is the maximum wait time a UPnP controlpoint will wait before sending a respond 1..5 seconds.
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

    }

    enum class TYPE(val headerLine: String, val requiredHeaders: Array<String> = emptyArray<String>()) {
        M_SEARCH("M-SEARCH * HTTP/1.1", arrayOf(HEADER_HOST, HEADER_MAN, HEADER_MX, HEADER_SEARCH_TEXT)),
        OK("HTTP/1.1 200 OK", arrayOf(HEADER_CACHE_CONTROL, HEADER_EXT, HEADER_LOCATION, HEADER_SERVER, HEADER_SEARCH_TEXT, HEADER_USN)),
        UNKNOWN("")
    }

}
