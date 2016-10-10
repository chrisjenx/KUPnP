@file:JvmName("ControlPointService")

package kupnp.controlpoint

import okhttp3.HttpUrl
import retrofit2.Retrofit


/**
 * Create a controlpoint service for the specified controlpoint endpoint.
 * This is what you use to talk to the devices.
 *
 */
fun getDeviceService(baseUrl: HttpUrl, retrofit: Retrofit = getRetrofit(baseUrl)): DeviceService = retrofit.create(DeviceService::class.java)
