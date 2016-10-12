package kupnp

import rx.Observable

/**
 * Created by chris on 16/04/2016.
 * For project kupnp
 */
class SSDPService {

    companion object {
        /**
         * Create an msearch control point. Will return uniquely found devices on the network then finish after the
         * timeout value.
         *
         * @param message defaults to search for everything, can pass in you're own search message. Not-Null.
         */
        fun msearch(message: SsdpMessage = SsdpMessage.search()): Observable<SsdpMessage> {
            val request = MulticastDiscovery.MulticastDiscoveryRequest(
                    data = message.byteString(),
                    multicastAddress = SsdpMessage.DEFAULT_IP_ADDRESS,
                    port = SsdpMessage.DEFAULT_PORT,
                    timeout = SsdpMessage.DEFAULT_MX
            )
            return MulticastDiscovery(request)
                    .create()
                    .map { SsdpMessage.fromPacket(it) }
                    .distinct()
        }
    }

}

