package kupnp

import kupnp.controlpoint.ServiceDescription
import kupnp.controlpoint.getDeviceService
import okhttp3.HttpUrl
import rx.Observable
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

var subs = CompositeSubscription()

fun main(args: Array<String>) {

    log("Starting search")
    val sub = SSDPService.msearch()
            .flatMap {
//                debug("Search result:\n\r\t$it\n\r")
                val location = it.headers[SsdpMessage.HEADER_LOCATION] ?: ""
                val url = HttpUrl.parse(location)?.newBuilder()?.encodedPath("/")?.build() ?: return@flatMap Observable.empty<ServiceDescription>()

                debug("SearchLocation: $url")
                getDeviceService(url).getDeviceDescription(location)
            }
            .subscribeOn(Schedulers.io())
            .subscribe({
                debug("DeviceDescriptio: $it")
            }, {
                it.printStackTrace()
            }, {
                log("Completed SSDP Discovery")
            })
            .apply { subs.add(this) }


    while (!sub.isUnsubscribed) {
        Thread.sleep(500L)
    }

}

