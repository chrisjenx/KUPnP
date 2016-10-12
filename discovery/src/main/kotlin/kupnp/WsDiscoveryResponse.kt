package kupnp

import okio.ByteString
import java.net.InetAddress
import java.util.*

/**
 * Created by chris on 12/10/2016.
 */
data class WsDiscoveryResponse
internal constructor(
        val messageId: UUID,
        val addressId: UUID,
        /**
         * Remember sometimes these can be self-assigned if the device is multi-honed, worth using [InetAddress.isSiteLocalAddress] etc.
         */
        val xaddrs: List<String>,
        val scopes: List<String>,
        /**
         * This is defined from the Packet Source, it can be useful if the device is multi-honed and has the wrong address
         */
        val packetAddress: InetAddress? = null
) {

    /**
     * Closest to model of the camera.
     */
    val hardware: String?
        get() = scopes.firstOrNull { it.startsWith("onvif://www.onvif.org/hardware/") }?.substringAfter("onvif://www.onvif.org/hardware/")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as WsDiscoveryResponse

        if (addressId != other.addressId) return false
        if (xaddrs != other.xaddrs) return false
        if (scopes != other.scopes) return false
        if (packetAddress != other.packetAddress) return false

        return true
    }

    override fun hashCode(): Int {
        var result = addressId.hashCode()
        result = 31 * result + xaddrs.hashCode()
        result = 31 * result + scopes.hashCode()
        result = 31 * result + (packetAddress?.hashCode() ?: 0)
        return result
    }


    companion object {
        /**
         * Group 2 gives you the UUID
         */
        internal val REGEX_MESSAGE_ID = "<[a-z]+?:MessageID>(urn:)?uuid:([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})</[a-z]+?:MessageID>".toRegex(RegexOption.IGNORE_CASE)
        /**
         * Matches the address uuid from the device, this is unique to the device. (Should never change)
         *
         * Group 2 gives you the UUID
         */
        internal val REGEX_ADDRESS_ID = "<[a-z]+?:Address>(urn:)?uuid:([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})</[a-z]+?:Address>".toRegex(RegexOption.IGNORE_CASE)

        /**
         * Matches the content of XAddrs which will contain zero or more addresses using white space split
         */
        internal val REGEX_XADDRS = "<\\w*:XAddrs>([^<>]*)</\\w*:XAddrs>".toRegex(RegexOption.IGNORE_CASE)
        /**
         * Matches the content of scopes which will be contain zero or more onvif:// scopes, whitespace split
         */
        internal val REGEX_SCOPES = "<\\w*:Scopes>([^<>]*)</\\w*:Scopes>".toRegex(RegexOption.IGNORE_CASE)


        /**
         * @return Populated Response, null if can't parse response
         */
        fun parseResponse(byteString: ByteString, packetAddress: InetAddress? = null): WsDiscoveryResponse? {
            val resStr = byteString.utf8()

            val message = REGEX_MESSAGE_ID.find(resStr)?.groupValues?.getOrNull(2) ?: return null
            val address = REGEX_ADDRESS_ID.find(resStr)?.groupValues?.getOrNull(2) ?: return null

            return WsDiscoveryResponse(
                    messageId = uuidFromString(message) ?: return null,
                    addressId = uuidFromString(address) ?: return null,
                    xaddrs = getWhiteSpaceSplit(REGEX_XADDRS, resStr) ?: return null,
                    scopes = getWhiteSpaceSplit(REGEX_SCOPES, resStr) ?: return null,
                    packetAddress = packetAddress
            )
        }

        /**
         * Null means failed to get XAddrs completely
         */
        internal fun getWhiteSpaceSplit(regex: Regex, parse: String): List<String>? {
            val xaddr = regex.find(parse)?.groupValues?.getOrNull(1)?.trim() ?: return null
            return xaddr.split("\\s".toRegex())
        }

        internal fun uuidFromString(name: String): UUID? {
            try {
                return UUID.fromString(name)
            } catch (e: IllegalArgumentException) {
                return null
            }
        }


    }

}