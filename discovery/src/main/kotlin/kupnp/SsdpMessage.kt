package kupnp

import okio.ByteString
import kotlin.properties.Delegates

/**
 * Created by chris on 25/08/2016.
 * For project kupnp
 */
class SsdpMessage {

    val headers = mutableMapOf<String, String>()
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
                headers.put(keyValue[0], keyValue[1])
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
        private const val SSDP_HOST = "${SsdpControlPoint.SSDP_IP}:${SsdpControlPoint.SSDP_PORT}"
        private const val SSDP_DISCOVER = "\"ssdp:discover\""
        private const val HEADER_SEARCH_TEXT = "ST"
        const val HEADER_MX = "MX"
        const val HEADER_HOST = "HOST"
        const val HEADER_MAN = "MAN"
        private const val NL = "\r\n"

        /**
         * Create M-SEARCH multicast packet with the applied search string.
         *
         * @param searchString This is the ST header, Default search string is "upnp:rootdevice"
         */
        fun search(searchString: String = "upnp:rootdevice") = SsdpMessage(TYPE.M_SEARCH).apply {
            headers.put(HEADER_HOST, SSDP_HOST)
            headers.put(HEADER_MAN, SSDP_DISCOVER)
            headers.put(HEADER_MX, 3.toString())
            headers.put(HEADER_SEARCH_TEXT, searchString)
        }

        fun fromPacket(source: ByteString) = SsdpMessage(source)

        enum class TYPE(val headerLine: String) {
            M_SEARCH("M-SEARCH * HTTP/1.1"),
            OK("HTTP/1.1 200 OK"),
            UNKNOWN("")
        }

    }

}
