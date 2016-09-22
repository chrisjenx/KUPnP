package kupnp.controlpoint

import com.google.common.truth.Truth.assertThat
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.Test
import java.io.File

/**
 * Created by chris on 21/09/2016.
 */
class ControlPointConverterTest {

    val server = MockWebServer()
    val resourceDir = File("src/test/resources")
    val service by lazy { getDeviceService(server.url("/")) }

    @Test
    fun parseGatewayDeviceDescriptionBody() {
        val gateway = Okio.buffer(Okio.source(File(resourceDir, "gateway.xml").toPath())).readUtf8()
        server.enqueue(MockResponse().setBody(gateway))

        val result = service.getDeviceDescription("/gateway.xml").toBlocking().single()
        val expected = DeviceDescription(
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

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun parseServiceDescriptionSimpleBody() {
        val gateway = Okio.buffer(Okio.source(File(resourceDir, "service_description_simple.xml").toPath())).readUtf8()
        server.enqueue(MockResponse().setBody(gateway))

        val result = service.getServiceDescription("/service_description_simple.xml").toBlocking().single()
        val expected = ServiceDescription(
                SpecVersion(1, 0),
                listOf(
                        Action(
                                name = "GetExternalIPAddress",
                                argumentList = listOf(
                                        Argument(
                                                name = "NewExternalIPAddress",
                                                direction = "out",
                                                relatedStateVariable = "ExternalIPAddress"
                                        )
                                )
                        )
                ),
                listOf(
                        StateVariable(
                                sendEvents = "no",
                                name = "NATEnabled",
                                dataType = "boolean",
                                defaultValue = "1"
                        ),
                        StateVariable(
                                sendEvents = "yes",
                                name = "ExternalIPAddress",
                                dataType = "string"
                        )
                )
        )

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun parseServiceDescription() {
        val gateway = Okio.buffer(Okio.source(File(resourceDir, "service_description.xml").toPath())).readUtf8()
        server.enqueue(MockResponse().setBody(gateway))

        val result = service.getServiceDescription("/service_description.xml").toBlocking().single()

        assertThat(result).isNotNull()
        assertThat(result.actionList).hasSize(11)
        assertThat(result.containsAction("SetConnectionType")).isTrue()
        assertThat(result.containsAction("GetConnectionTypeInfo")).isTrue()
        assertThat(result.containsAction("RequestConnection")).isTrue()
        assertThat(result.containsAction("ForceTermination")).isTrue()
        assertThat(result.containsAction("GetStatusInfo")).isTrue()
        assertThat(result.containsAction("GetNATRSIPStatus")).isTrue()
        assertThat(result.containsAction("GetGenericPortMappingEntry")).isTrue()
        assertThat(result.containsAction("GetSpecificPortMappingEntry")).isTrue()
        assertThat(result.containsAction("AddPortMapping")).isTrue()
        assertThat(result.containsAction("DeletePortMapping")).isTrue()
        assertThat(result.containsAction("GetExternalIPAddress")).isTrue()

        assertThat(result.containsStateVariable("ConnectionType")).isTrue()
        assertThat(result.containsStateVariable("PossibleConnectionTypes")).isTrue()
        assertThat(result.containsStateVariable("ConnectionStatus")).isTrue()
        assertThat(result.containsStateVariable("Uptime")).isTrue()
        assertThat(result.containsStateVariable("RSIPAvailable")).isTrue()
        assertThat(result.containsStateVariable("NATEnabled")).isTrue()
        assertThat(result.containsStateVariable("X_Name")).isTrue()
        assertThat(result.containsStateVariable("LastConnectionError")).isTrue()
        assertThat(result.containsStateVariable("ExternalIPAddress")).isTrue()
        assertThat(result.containsStateVariable("RemoteHost")).isTrue()
        assertThat(result.containsStateVariable("ExternalPort")).isTrue()
        assertThat(result.containsStateVariable("InternalPort")).isTrue()
        assertThat(result.containsStateVariable("PortMappingProtocol")).isTrue()
        assertThat(result.containsStateVariable("InternalClient")).isTrue()
        assertThat(result.containsStateVariable("PortMappingDescription")).isTrue()
        assertThat(result.containsStateVariable("PortMappingEnabled")).isTrue()
        assertThat(result.containsStateVariable("PortMappingNumberOfEntries")).isTrue()

        assertThat(result.serviceStateTable).contains(StateVariable(
                sendEvents = "no",
                name = "Uptime",
                dataType = "ui4",
                defaultValue = "0",
                allowedValueRange = AllowedValueRange(0.0, null, 1.0)
        ))

        assertThat(result.serviceStateTable).contains(StateVariable(
                sendEvents = "yes",
                name = "ConnectionStatus",
                dataType = "string",
                defaultValue = "Unconfigured",
                allowedValueList = listOf(
                        "Unconfigured",
                        "Connected",
                        "Connecting",
                        "Authenticating",
                        "PendingDisconnect",
                        "Disconnecting",
                        "Disconnected"
                )
        ))
    }
}