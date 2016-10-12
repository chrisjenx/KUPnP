package kupnp

import kupnp.MulticastDiscovery.MulticastDiscoveryRequest
import kupnp.MulticastDiscovery.MulticastDiscoveryResponse
import rx.Observable

/**
 * Created by chris on 12/10/2016.
 */
object WsDiscoveryService {

    fun search(wsDiscoveryMessage: WsDiscoveryMessage = WsDiscoveryMessage()): Observable<MulticastDiscoveryResponse> {
        val request = MulticastDiscoveryRequest(
                data = wsDiscoveryMessage.byteString(),
                multicastAddress = WsDiscoveryMessage.DEFAULT_IP_ADDRESS,
                port = WsDiscoveryMessage.DEFAULT_PORT,
                timeout = WsDiscoveryMessage.DEFAULT_TIMEOUT,
                responseSize = 4096
        )
        return MulticastDiscovery(request)
                .create()
                .distinct()
    }

}