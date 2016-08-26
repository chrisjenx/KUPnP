package kupnp

import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by chris on 16/04/2016.
 * For project kupnp
 */
class SSDPService {

    companion object {
        fun msearch(): Observable<String> {
            return Ssdp(SsdpMessage.search())
                    .start()
                    .subscribeOn(Schedulers.io())
                    .map {
                        val string = it.utf8()
                        log("Result: $string")
                        return@map string
                    }
        }
    }

}

