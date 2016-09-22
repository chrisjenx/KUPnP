package kupnp.controlpoint

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

/**
 * Created by chris on 21/09/2016.
 */

@Root(name = "root")
data class DeviceDescription(
        @field:Element(name = "specVersion") var specVersion: SpecVersion? = null,
        @field:Element(name = "device") var device: Device? = null
)

data class SpecVersion(
        @field:Element(name = "major") var major: Int? = null,
        @field:Element(name = "minor") var minor: Int? = null
)

data class Device(
        @field:Element(name = "deviceType") var deviceType: String? = null,
        @field:Element(name = "friendlyName") var friendlyName: String? = null,
        @field:Element(name = "manufacturer") var manufacturer: String? = null,
        @field:Element(name = "manufacturerURL", required = false) var manufacturerURL: String? = null,
        @field:Element(name = "modelName") var modelName: String? = null,
        @field:Element(name = "modelURL", required = false) var modelURL: String? = null,
        @field:Element(name = "serialNumber", required = false) var serialNumber: String? = null,
        @field:Element(name = "UDN") var UDN: String? = null,
        @field:Element(name = "UPC", required = false) var UPC: String? = null,
        @field:ElementList(name = "serviceList", required = false) var serviceList: List<Service>? = null,
        @field:ElementList(name = "deviceList", required = false) var deviceList: List<Device>? = null,
        @field:Element(name = "presentationURL", required = false) var presentationURL: String? = null
)

data class Service(
        @field:Element(name = "serviceType") var serviceType: String? = null,
        @field:Element(name = "serviceId") var serviceId: String? = null,
        @field:Element(name = "SCPDURL") var SCPDURL: String? = null,
        @field:Element(name = "controlURL") var controlURL: String? = null,
        @field:Element(name = "eventSubURL") var eventSubURL: String? = null
)