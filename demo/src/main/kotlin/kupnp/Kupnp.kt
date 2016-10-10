package kupnp

import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

var subs = CompositeSubscription()

fun main(args: Array<String>) {

    log("Starting search")
    val sub = SSDPService.Companion.msearch()
            .subscribeOn(Schedulers.io())
            .subscribe({
                log("Search result:\n\r\t$it\n\r")
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

