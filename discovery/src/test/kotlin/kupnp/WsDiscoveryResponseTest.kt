package kupnp

import com.google.common.truth.Truth.assertThat
import okio.ByteString
import org.junit.Test
import java.util.*

/**
 * Created by chris on 12/10/2016.
 */
class WsDiscoveryResponseTest {
    val foscamRes = """
    <?xml version="1.0" encoding="utf-8" ?>
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope" xmlns:SOAP-ENC="http://www.w3.org/2003/05/soap-encoding" xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing" xmlns:d="http://schemas.xmlsoap.org/ws/2005/04/discovery" xmlns:dn="http://www.onvif.org/ver10/network/wsdl">
        <SOAP-ENV:Header>
            <wsa:MessageID>urn:uuid:79b1ee85-221d-c86e-e6b1-af7c67f3419b</wsa:MessageID>
            <wsa:RelatesTo>urn:uuid:2e6617c4-4a15-4737-b980-ed6e9830a7a8</wsa:RelatesTo>
            <wsa:To SOAP-ENV:mustUnderstand="true">http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</wsa:To>
            <wsa:Action SOAP-ENV:mustUnderstand="true">http://schemas.xmlsoap.org/ws/2005/04/discovery/ProbeMatches</wsa:Action>
        </SOAP-ENV:Header>
        <SOAP-ENV:Body>
            <d:ProbeMatches>
                <d:ProbeMatch>
                    <wsa:EndpointReference>
                        <wsa:Address>urn:uuid:88d87a05-d7f7-7e45-b1e9-C4D6553FDFAB</wsa:Address>
                    </wsa:EndpointReference>
                    <d:Types>dn:NetworkVideoTransmitter</d:Types>
                    <d:Scopes>onvif://www.onvif.org/type/manufacturer/FOSCAM onvif://www.onvif.org/type/video_encoder onvif://www.onvif.org/type/network_video_transmitter onvif://www.onvif.org/name/C1 onvif://www.onvif.org/hardware/C1 onvif://www.onvif.org/Profile/Streaming onvif://www.onvif.org/location/country/china onvif://www.onvif.org/location/city/Shenzhen </d:Scopes>
                    <d:XAddrs>http://10.0.0.227:888/onvif/device_service</d:XAddrs>
                    <d:MetadataVersion>0</d:MetadataVersion>
                </d:ProbeMatch>
            </d:ProbeMatches>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>
    """
    val axisRes = """
    <?xml version="1.0" encoding="UTF-8"?>
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope" xmlns:SOAP-ENC="http://www.w3.org/2003/05/soap-encoding" xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing" xmlns:d="http://schemas.xmlsoap.org/ws/2005/04/discovery" xmlns:dn="http://www.onvif.org/ver10/network/wsdl"><SOAP-ENV:Header><wsa:MessageID>uuid:b0bca4a7-5eee-48e6-ad9f-f4293e3198b8</wsa:MessageID><wsa:RelatesTo>urn:uuid:2e6617c4-4a15-4737-b980-ed6e9830a7a8</wsa:RelatesTo><wsa:To SOAP-ENV:mustUnderstand="true">http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</wsa:To><wsa:Action SOAP-ENV:mustUnderstand="true">http://schemas.xmlsoap.org/ws/2005/04/discovery/ProbeMatches</wsa:Action><d:AppSequence SOAP-ENV:mustUnderstand="true" MessageNumber="6" InstanceId="1431532724"></d:AppSequence></SOAP-ENV:Header><SOAP-ENV:Body><d:ProbeMatches><d:ProbeMatch><wsa:EndpointReference><wsa:Address>urn:uuid:08a6f3cd-312c-4810-837e-fb2859be05bb</wsa:Address></wsa:EndpointReference><d:Types>dn:NetworkVideoTransmitter</d:Types><d:Scopes>onvif://www.onvif.org/type/video_encoder onvif://www.onvif.org/Profile/Streaming onvif://www.onvif.org/type/audio_encoder onvif://www.onvif.org/hardware/M1034-W onvif://www.onvif.org/name/AXIS%20M1034-W onvif://www.onvif.org/location/ </d:Scopes><d:XAddrs>http://169.254.180.212/onvif/device_service http://10.0.0.246/onvif/device_service</d:XAddrs><d:MetadataVersion>1</d:MetadataVersion></d:ProbeMatch></d:ProbeMatches></SOAP-ENV:Body></SOAP-ENV:Envelope>
    """
    val amcrestRes = """
    <?xml version="1.0" encoding="utf-8" standalone="yes" ?>
    <s:Envelope xmlns:sc="http://www.w3.org/2003/05/soap-encoding" xmlns:s="http://www.w3.org/2003/05/soap-envelope" xmlns:dn="http://www.onvif.org/ver10/network/wsdl" xmlns:tds="http://www.onvif.org/ver10/device/wsdl" xmlns:d="http://schemas.xmlsoap.org/ws/2005/04/discovery" xmlns:a="http://schemas.xmlsoap.org/ws/2004/08/addressing">
        <s:Header>
            <a:MessageID>uuid:c89a766b-03ba-4bd8-83e7-a9f569617177</a:MessageID>
            <a:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</a:To>
            <a:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/ProbeMatches</a:Action>
            <a:RelatesTo>urn:uuid:2e6617c4-4a15-4737-b980-ed6e9830a7a8</a:RelatesTo>
        </s:Header>
        <s:Body>
            <d:ProbeMatches>
                <d:ProbeMatch>
                    <a:EndpointReference>
                        <a:Address>uuid:278b1b37-153c-452d-b316-d2ed4e99e6a2</a:Address>
                    </a:EndpointReference>
                    <d:Types>dn:NetworkVideoTransmitter tds:Device</d:Types>
                    <d:Scopes>onvif://www.onvif.org/location/country/china onvif://www.onvif.org/name/Amcrest onvif://www.onvif.org/hardware/IP2M-841B onvif://www.onvif.org/Profile/Streaming onvif://www.onvif.org/type/Network_Video_Transmitter onvif://www.onvif.org/extension/unique_identifier onvif://www.onvif.org/Profile/Q/Operational</d:Scopes>
                    <d:XAddrs>http://10.0.0.154:8088/onvif/device_service</d:XAddrs>
                    <d:MetadataVersion>1</d:MetadataVersion>
                </d:ProbeMatch>
            </d:ProbeMatches>
        </s:Body>
    </s:Envelope>
    """

    @Test
    fun testMessageIDRegexOne() {
        assertThat(WsDiscoveryResponse.REGEX_MESSAGE_ID.matches("<wsadis:MessageID>urn:uuid:bc329e00-1dd8-11b2-8601-00c554036bc1</wsadis:MessageID>")).isTrue()
    }

    @Test
    fun testMessageIDRegexTwo() {
        assertThat(WsDiscoveryResponse.REGEX_MESSAGE_ID.matches("<a:MessageID>uuid:00534dcd-1505-4e4d-b890-57132d522b99</a:MessageID>")).isTrue()
    }

    @Test
    fun testMessageIDRegexThree() {
        assertThat(WsDiscoveryResponse.REGEX_MESSAGE_ID.matches("<wsa:MessageID>urn:uuid:e5817095-6863-71f7-3709-b754eb816214</wsa:MessageID>")).isTrue()
    }

    @Test
    fun testAddressRegexOne() {
        assertThat(WsDiscoveryResponse.REGEX_ADDRESS_ID.matches("<a:Address>uuid:a7f7f00f-09a7-4043-8989-15a0421cba32</a:Address>")).isTrue()
    }

    @Test
    fun testAddressRegexTwo() {
        assertThat(WsDiscoveryResponse.REGEX_ADDRESS_ID.matches("<wsa:Address>urn:uuid:88d87a05-d7f7-7e45-b1e9-C4D6553FDFAB</wsa:Address>")).isTrue()
    }

    @Test
    fun testAddressRegexThree() {
        assertThat(WsDiscoveryResponse.REGEX_ADDRESS_ID.matches("<wsadis:Address>urn:uuid:bc329e00-1dd8-11b2-8601-00107b0f7270</wsadis:Address>")).isTrue()
    }

    @Test
    fun testXAddrsOne() {
        assertThat(WsDiscoveryResponse.REGEX_XADDRS.matches("<d:XAddrs>http://10.0.0.252/onvif/device_service</d:XAddrs>")).isTrue()
    }

    @Test
    fun testXAddrsTwo() {
        assertThat(WsDiscoveryResponse.REGEX_XADDRS.matches("<d:XAddrs>http://169.254.179.205/onvif/device_service http://10.0.0.252/onvif/device_service</d:XAddrs>")).isTrue()
    }

    @Test
    fun testFoscamResponse() {
        val response = WsDiscoveryResponse.parseResponse(ByteString.encodeUtf8(foscamRes))

        assertThat(response).isNotNull()
        assertThat(response?.messageId).isEqualTo(UUID.fromString("79b1ee85-221d-c86e-e6b1-af7c67f3419b"))
        assertThat(response?.addressId).isEqualTo(UUID.fromString("88d87a05-d7f7-7e45-b1e9-C4D6553FDFAB"))
        assertThat(response?.xaddrs).containsExactly("http://10.0.0.227:888/onvif/device_service")
        assertThat(response?.scopes).containsExactly("onvif://www.onvif.org/type/manufacturer/FOSCAM", "onvif://www.onvif.org/type/video_encoder", "onvif://www.onvif.org/type/network_video_transmitter", "onvif://www.onvif.org/name/C1", "onvif://www.onvif.org/hardware/C1", "onvif://www.onvif.org/Profile/Streaming", "onvif://www.onvif.org/location/country/china", "onvif://www.onvif.org/location/city/Shenzhen")
        assertThat(response?.hardware).isEqualTo("C1")
    }

    @Test
    fun testAxisResponse() {
        val response = WsDiscoveryResponse.parseResponse(ByteString.encodeUtf8(axisRes))

        assertThat(response).isNotNull()
        assertThat(response?.messageId).isEqualTo(UUID.fromString("b0bca4a7-5eee-48e6-ad9f-f4293e3198b8"))
        assertThat(response?.addressId).isEqualTo(UUID.fromString("08a6f3cd-312c-4810-837e-fb2859be05bb"))
        assertThat(response?.xaddrs).containsExactly("http://169.254.180.212/onvif/device_service", "http://10.0.0.246/onvif/device_service")
        assertThat(response?.scopes).containsExactly("onvif://www.onvif.org/type/video_encoder", "onvif://www.onvif.org/Profile/Streaming", "onvif://www.onvif.org/type/audio_encoder", "onvif://www.onvif.org/hardware/M1034-W", "onvif://www.onvif.org/name/AXIS%20M1034-W", "onvif://www.onvif.org/location/")
        assertThat(response?.hardware).isEqualTo("M1034-W")
    }

    @Test
    fun testAmcrestResponse() {
        val response = WsDiscoveryResponse.parseResponse(ByteString.encodeUtf8(amcrestRes))

        assertThat(response).isNotNull()
        assertThat(response?.messageId).isEqualTo(UUID.fromString("c89a766b-03ba-4bd8-83e7-a9f569617177"))
        assertThat(response?.addressId).isEqualTo(UUID.fromString("278b1b37-153c-452d-b316-d2ed4e99e6a2"))
        assertThat(response?.xaddrs).containsExactly("http://10.0.0.154:8088/onvif/device_service")
        assertThat(response?.scopes).containsExactly("onvif://www.onvif.org/location/country/china", "onvif://www.onvif.org/name/Amcrest", "onvif://www.onvif.org/hardware/IP2M-841B", "onvif://www.onvif.org/Profile/Streaming", "onvif://www.onvif.org/type/Network_Video_Transmitter", "onvif://www.onvif.org/extension/unique_identifier", "onvif://www.onvif.org/Profile/Q/Operational")
        assertThat(response?.hardware).isEqualTo("IP2M-841B")
    }
}