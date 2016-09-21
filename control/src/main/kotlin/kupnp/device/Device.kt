package kupnp.device

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

/**
 * Created by chris on 21/09/2016.
 */
@Root(name = "root")
data class DeviceRoot(
        @Element(name = "specVersion") val specVersion: SpecVersion
)

data class SpecVersion(
        @Element val major: Int,
        @Element val minor: Int
)