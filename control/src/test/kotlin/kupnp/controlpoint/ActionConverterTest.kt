package kupnp.controlpoint

import com.google.common.truth.Truth.assertThat
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.Assert.fail
import org.junit.Test
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import org.simpleframework.xml.stream.Verbosity
import retrofit2.HttpException

/**
 * Created by chris on 22/09/2016.
 */
class ActionConverterTest {

    val body = """
        <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
            <s:Body>
                <u:GetConnectionTypeInfoResponse xmlns:u="urn:schemas-upnp-org:service:WANIPConnection:1">
                    <NewConnectionType>IP_Routed</NewConnectionType>
                    <NewPossibleConnectionTypes>IP_Routed</NewPossibleConnectionTypes>
                </u:GetConnectionTypeInfoResponse>
            </s:Body>
        </s:Envelope>""".trimIndent()

    val bodyError = """
        <?xml version="1.0"?>
        <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
            <s:Body>
                <s:Fault>
                    <faultcode>s:Client</faultcode>
                    <faultstring>UPnPError</faultstring>
                    <detail>
                        <UPnPError xmlns="urn:schemas-upnp-org:control-1-0">
                            <errorCode>error code</errorCode>
                            <errorDescription>error string</errorDescription>
                        </UPnPError>
                    </detail>
                </s:Fault>
            </s:Body>
        </s:Envelope>""".trimIndent()

    val persister = Persister(AnnotationStrategy(), Format(0, Verbosity.HIGH))

    val server = MockWebServer()
    val service by lazy { getDeviceService(server.url("/")) }


    @Test
    fun testSoapActionNameConverter_name() {
        val original = ActionName("MyActionName", "MyServiceType", "1")

        val buffer = Buffer()
        persister.write(original, buffer.outputStream())

        val expected = """<u:MyActionName xmlns:u="urn:schemas-upnp-org:service:MyServiceType:1"/>""".trimIndent()

        assertThat(buffer.readUtf8()).isEqualTo(expected)
    }

    @Test
    fun testSoapAction_noArguments_noResponse() {
        server.enqueue(MockResponse().setBody(body))
        val actionName = ActionName("MyActionName", "MyServiceType", "1")
        val request = ActionRequest(ActionBody(actionName))

        service.postActionCommand("/", "${actionName.namespaceReference}#${actionName.actionName}", request).toBlocking().single()
        val serverRequest = server.takeRequest()

        val expected = """<s:Envelope s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" xmlns:s="http://schemas.xmlsoap.org/soap/envelope"><s:Body><u:MyActionName xmlns:u="urn:schemas-upnp-org:service:MyServiceType:1"/></s:Body></s:Envelope>"""
        val expectedHeader = "urn:schemas-upnp-org:service:MyServiceType:1#MyActionName"
        assertThat(serverRequest.body.readUtf8()).isEqualTo(expected)
        assertThat(serverRequest.getHeader("SOAPACTION")).isEqualTo(expectedHeader)
    }

    @Test
    fun testSoapAction_arguments_noResponse() {
        server.enqueue(MockResponse().setBody(body))
        val actionName = ActionName("MyActionName", "MyServiceType", "1",
                arguments = mapOf("NewConnectionType" to "SomethingElse", "NewRemoteHost" to "192.168.1.1")
        )

        service.postActionCommand("/", "${actionName.namespaceReference}#${actionName.actionName}", ActionRequest(ActionBody(actionName))).toBlocking().single()
        val serverRequest = server.takeRequest()

        val expected = """<s:Envelope s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" xmlns:s="http://schemas.xmlsoap.org/soap/envelope"><s:Body><u:MyActionName xmlns:u="urn:schemas-upnp-org:service:MyServiceType:1"><NewConnectionType>SomethingElse</NewConnectionType><NewRemoteHost>192.168.1.1</NewRemoteHost></u:MyActionName></s:Body></s:Envelope>"""
        val expectedHeader = "urn:schemas-upnp-org:service:MyServiceType:1#MyActionName"
        assertThat(serverRequest.body.readUtf8()).isEqualTo(expected)
        assertThat(serverRequest.getHeader("SOAPACTION")).isEqualTo(expectedHeader)
    }

    @Test
    fun testSoapAction_response() {
        server.enqueue(MockResponse().setBody(body))

        val actionName = ActionName("GetConnectionTypeInfoResponse", "WANIPConnection", "1")

        val response = service.postActionCommand("/", "${actionName.namespaceReference}#${actionName.actionName}", ActionRequest(ActionBody(actionName))).toBlocking().single()

        assertThat(response.body).isEqualTo(ActionBody(ActionName("GetConnectionTypeInfo", "WANIPConnection", "1", mapOf(
                "NewConnectionType" to "IP_Routed",
                "NewPossibleConnectionTypes" to "IP_Routed"
        ))))
    }


    @Test
    fun testSoapAction_responseError() {
        server.enqueue(MockResponse().setBody(bodyError).setResponseCode(500))

        val actionName = ActionName("GetConnectionTypeInfoResponse", "WANIPConnection", "1")

        try {
            val response = service.postActionCommand("/", "${actionName.namespaceReference}#${actionName.actionName}", ActionRequest(ActionBody(actionName))).toBlocking().single()
            fail("Should of thrown HttpException")
        } catch (e: Exception) {
            assertThat(e.cause).isInstanceOf(HttpException::class.java)
            val httpEx = e.cause as HttpException

            val errorBody = getRetrofit(server.url("/"))
                    .responseBodyConverter<ActionFault>(ActionFault::class.java, arrayOf<Annotation>())
                    .convert(httpEx.response().errorBody())
            assertThat(errorBody).isNotNull()
            assertThat(errorBody?.body).isNotNull()
            assertThat(errorBody?.body?.fault).isNotNull()
            assertThat(errorBody?.body?.fault?.faultCode).isEqualTo("s:Client")
            assertThat(errorBody?.body?.fault?.faultString).isEqualTo("UPnPError")
            assertThat(errorBody?.body?.fault?.detail?.upnpError?.errorCode).isEqualTo("error code")
            assertThat(errorBody?.body?.fault?.detail?.upnpError?.errorDescription).isEqualTo("error string")
        }
    }

}