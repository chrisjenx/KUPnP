package kupnp

import rx.subscriptions.CompositeSubscription

var subs = CompositeSubscription()

fun main(args: Array<String>) {

    log("Starting search")
    SSDPService.msearch()
            .subscribe {
                log("Search result")
            }
            .apply { subs.add(this) }


    while (!subs.isUnsubscribed) {
        Thread.sleep(500L)
    }
}

