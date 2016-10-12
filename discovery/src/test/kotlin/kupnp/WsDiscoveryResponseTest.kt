package kupnp

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Created by chris on 12/10/2016.
 */
class WsDiscoveryResponseTest {

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
        "<a:Address>uuid:a7f7f00f-09a7-4043-8989-15a0421cba32</a:Address>"
        "<wsa:Address>urn:uuid:88d87a05-d7f7-7e45-b1e9-C4D6553FDFAB</wsa:Address>"
        "<wsadis:Address>urn:uuid:bc329e00-1dd8-11b2-8601-00107b0f7270</wsadis:Address>"
        assertThat(WsDiscoveryResponse.REGEX_MESSAGE_ID.matches("<wsa:MessageID>urn:uuid:e5817095-6863-71f7-3709-b754eb816214</wsa:MessageID>")).isTrue()
    }
}