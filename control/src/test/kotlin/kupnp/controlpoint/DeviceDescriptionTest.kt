package kupnp.controlpoint

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Created by chris on 18/10/2016.
 * For project kupnp
 */
class DeviceDescriptionTest {

    val root = DeviceDescription(
            SpecVersion(1, 0),
            Device(
                    deviceType = "urn:schemas-upnp-org:device:InternetGatewayDevice:1",
                    friendlyName = "MikroTik Router",
                    manufacturer = "MikroTik",
                    manufacturerURL = "http://www.mikrotik.com/",
                    modelName = "Router OS",
                    UDN = "uuid:UUID-MIKROTIK-INTERNET-GATEWAY-DEVICE-DXZR-V5M2",
                    serviceList = listOf(
                            Service(
                                    serviceType = "urn:schemas-microsoft-com:service:OSInfo:1",
                                    serviceId = "urn:microsoft-com:serviceId:OSInfo1",
                                    SCPDURL = "/osinfo.xml",
                                    controlURL = "/upnp/control/vyyraqzkin/osinfo",
                                    eventSubURL = "/upnp/event/kuxugirlyu/osinfo"
                            )
                    ),
                    deviceList = listOf(
                            Device(
                                    deviceType = "urn:schemas-upnp-org:device:WANDevice:1",
                                    friendlyName = "WAN Device",
                                    manufacturer = "MikroTik",
                                    manufacturerURL = "http://www.mikrotik.com/",
                                    modelName = "Router OS",
                                    UDN = "uuid:UUID-MIKROTIK-WAN-DEVICE-DXZR-V5M2-11",
                                    serviceList = listOf(
                                            Service(
                                                    serviceType = "urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1",
                                                    serviceId = "urn:upnp-org:serviceId:WANCommonIFC1",
                                                    SCPDURL = "/wancommonifc-11.xml",
                                                    controlURL = "/upnp/control/wnddggytgr/wancommonifc-11",
                                                    eventSubURL = "/upnp/event/sdpswpiwcs/wancommonifc-11"
                                            )
                                    ),
                                    deviceList = listOf(
                                            Device(
                                                    deviceType = "urn:schemas-upnp-org:device:WANConnectionDevice:1",
                                                    friendlyName = "WAN Connection Device",
                                                    manufacturer = "MikroTik",
                                                    manufacturerURL = "http://www.mikrotik.com/",
                                                    modelName = "Router OS",
                                                    UDN = "uuid:UUID-MIKROTIK-WAN-CONNECTION-DEVICE-DXZR-V5M2-11",
                                                    serviceList = listOf(
                                                            Service(
                                                                    serviceType = "urn:schemas-upnp-org:service:WANIPConnection:1",
                                                                    serviceId = "urn:upnp-org:serviceId:WANIPConn1",
                                                                    SCPDURL = "/wanipconn-11.xml",
                                                                    controlURL = "/upnp/control/lomiivrzgp/wanipconn-11",
                                                                    eventSubURL = "/upnp/event/weebjmhjfp/wanipconn-11"
                                                            )
                                                    )
                                            )
                                    )
                            )
                    ),
                    presentationURL = "http://192.168.88.1/"
            )
    )

    @Test
    fun findServiceOfTypeInRoot() {
        val expected = Service(
                serviceType = "urn:schemas-microsoft-com:service:OSInfo:1",
                serviceId = "urn:microsoft-com:serviceId:OSInfo1",
                SCPDURL = "/osinfo.xml",
                controlURL = "/upnp/control/vyyraqzkin/osinfo",
                eventSubURL = "/upnp/event/kuxugirlyu/osinfo"
        )

        val result = root.findServiceOfType("urn:schemas-microsoft-com:service:OSInfo:1")

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun findServiceOfTypeDeviceListTopLevel() {
        val expected = Service(
                serviceType = "urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1",
                serviceId = "urn:upnp-org:serviceId:WANCommonIFC1",
                SCPDURL = "/wancommonifc-11.xml",
                controlURL = "/upnp/control/wnddggytgr/wancommonifc-11",
                eventSubURL = "/upnp/event/sdpswpiwcs/wancommonifc-11"
        )

        val result = root.findServiceOfType("urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1")

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun findServiceOfTypeDeviceListTwoDeep() {
        val expected = Service(
                serviceType = "urn:schemas-upnp-org:service:WANIPConnection:1",
                serviceId = "urn:upnp-org:serviceId:WANIPConn1",
                SCPDURL = "/wanipconn-11.xml",
                controlURL = "/upnp/control/lomiivrzgp/wanipconn-11",
                eventSubURL = "/upnp/event/weebjmhjfp/wanipconn-11"
        )

        val result = root.findServiceOfType("urn:schemas-upnp-org:service:WANIPConnection:1")

        assertThat(result).isEqualTo(expected)
    }

}