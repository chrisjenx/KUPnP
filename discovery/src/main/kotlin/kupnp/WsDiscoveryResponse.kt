package kupnp

/**
 * Created by chris on 12/10/2016.
 */
class WsDiscoveryResponse {


    companion object {
        /**
         * Matches the message uuid from the device, this is unique to the device.
         */
        internal val REGEX_MESSAGE_ID = "<[a-z]+?:MessageID>(urn:)?uuid:([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})</[a-z]+?:MessageID>".toRegex(RegexOption.IGNORE_CASE)
    }


}