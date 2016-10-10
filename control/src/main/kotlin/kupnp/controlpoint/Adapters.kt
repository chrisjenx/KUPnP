package kupnp.controlpoint

import okhttp3.HttpUrl
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.http.*
import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Created by chris on 21/09/2016.
 */

interface DeviceService {

    /**
     * Gets the description of the passed in DeviceDescription url
     */
    @GET
    fun getDeviceDescription(@Url path: String): Observable<DeviceDescription>

    @GET
    fun getServiceDescription(@Url path: String): Observable<ServiceDescription>

    /**
     * Ask a set/get from a control point. These are discovered using `getDeviceDescription` and `getServiceDescription`
     *
     * @param controlURL this is the URL taken from the `Service.controlURL` field.
     * @param soapHeader  "urn:schemas-upnp-org:service:serviceType:v#actionName".
     *  - serviceType:v = `Service.serviceType`
     *  - actionName = `Action.name`
     */
    @POST
    fun postActionCommand(@Url controlURL: String, @Header("SOAPACTION") soapHeader: String, @Body actionRequest: ActionRequest): Observable<ActionResponse>

}

/**
 * Create a controlpoint service for the specified controlpoint endpoint
 */
fun getRetrofit(baseUrl: HttpUrl, scheduler: Scheduler = Schedulers.io()): Retrofit {
    return Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(scheduler))
            .addConverterFactory(xmlConverter)
            .baseUrl(baseUrl)
            .build()
}
