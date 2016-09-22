package kupnp.controlpoint

import okhttp3.HttpUrl
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.http.*
import rx.Observable

/**
 * Created by chris on 21/09/2016.
 */

interface DeviceService {

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
    fun postActionCommand(@Url controlURL: String, @Header("SOAPACTION") soapHeader: String, @Body actionRequest: ActionRequest): Observable<Unit>

}


/**
 * Create a controlpoint service for the specified controlpoint endpoint
 */
fun getDeviceService(baseUrl: HttpUrl): DeviceService {
    return Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(xmlConverter)
            .baseUrl(baseUrl)
            .build()
            .create(DeviceService::class.java)
}