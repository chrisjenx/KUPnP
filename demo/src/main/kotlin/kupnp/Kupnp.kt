package kupnp

import kupnp.controlpoint.DeviceDescription
import kupnp.controlpoint.ServiceDescription
import kupnp.controlpoint.getDeviceService
import okhttp3.HttpUrl
import rx.Observable
import rx.Observable.merge
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

var subs = CompositeSubscription()

fun main(args: Array<String>) {

    log("Starting search")

    val ssdp = SSDPService.msearch()
            .flatMap {
                val location = it.headers[SsdpMessage.HEADER_LOCATION] ?: ""
                val url = HttpUrl.parse(location)?.newBuilder()?.encodedPath("/")?.build() ?: return@flatMap Observable.empty<ServiceDescription>()
                //debug("SearchLocation: $location")
                getDeviceService(url).getDeviceDescription(location).onExceptionResumeNext(Observable.empty<DeviceDescription>())
            }
            .doOnNext { debug("DeviceDescription: $it") }
            .doOnCompleted { log("Completed SSDP Discovery") }
            .subscribeOn(Schedulers.io())
    val ws = WsDiscoveryService.search()
            .doOnNext { debug("WSSearch Result: ${it.packetAddress} ${it.hardware}") }
            .doOnCompleted { log("Completed WS-Discovery") }
            .subscribeOn(Schedulers.io())

    val sub = merge(ssdp, ws).subscribe({}, { it.printStackTrace() })
//    val sub = ws.subscribe({}, { it.printStackTrace() })

    while (!sub.isUnsubscribed) {
        Thread.sleep(500L)
    }

}

