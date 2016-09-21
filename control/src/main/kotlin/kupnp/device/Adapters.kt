package kupnp.device

import okhttp3.HttpUrl
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import rx.Observable

/**
 * Created by chris on 21/09/2016.
 */

interface DeviceService {

    @GET
    fun getRootDeviceDescription(@Url path: String): Observable<DeviceRoot>

}


/**
 * Create a device service for the specified device endpoint
 */
fun getDeviceService(baseUrl: HttpUrl): DeviceService {
    return Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(xmlConverter)
            .baseUrl(baseUrl)
            .build()
            .create(DeviceService::class.java)
}