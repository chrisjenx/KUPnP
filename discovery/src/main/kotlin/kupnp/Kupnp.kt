package kupnp

import rx.subscriptions.CompositeSubscription

var subs = CompositeSubscription()

fun main(args: Array<String>) {

    log("Starting search")
    val sub = SSDPService.msearch()
            .subscribe({
                log("Search result")
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

