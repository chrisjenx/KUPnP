package kupnp.controlpoint

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root
import org.simpleframework.xml.convert.Convert
import org.simpleframework.xml.convert.ConvertException
import org.simpleframework.xml.convert.Converter
import org.simpleframework.xml.stream.InputNode
import org.simpleframework.xml.stream.OutputNode

/**
 * Created by chris on 22/09/2016.
 */


@Root(name = "Envelope")
@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope", prefix = "s")
data class ActionRequest(@field:Element(name = "Body") var body: ActionBody? = null) {
    @field:Attribute(name = "encodingStyle")
    @field:Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope", prefix = "s")
    var encodingStyle: String = "http://schemas.xmlsoap.org/soap/encoding/"
        private set
}

@Root
@Convert(ActionBodyConverter::class)
@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope")
data class ActionBody(var actionName: ActionName)

@Root
@Convert(ActionNameConverter::class)
data class ActionName(
        val actionName: String,
        val serviceType: String,
        val serviceVersion: String = "1",
        val arguments: Map<String, String>? = null
) {
    val namespaceReference: String = "urn:schemas-upnp-org:service:$serviceType:$serviceVersion"
    val namespacePrefix = "u"
}

@Root(name = "Envelope")
@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope", prefix = "s")
data class ActionResponse(@field:Element(name = "Body") var body: ActionBody? = null)

@Root(name = "Envelope")
@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope", prefix = "s")
data class ActionFault(@field:Element(name = "Body") var body: BodyFault? = null)

@Root(name = "Body")
@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope")
data class BodyFault(
        @field:Element(name = "Fault", required = false) var fault: Fault? = null
)

@Root
@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope")
data class Fault(
        @field:Element(name = "faultCode", required = false) var faultCode: String? = null,
        @field:Element(name = "faultString", required = false) var faultString: String? = null,
        @field:Element(name = "detail", required = false) var detail: FaultDetail? = null
)


data class FaultDetail(@field:Element(name = "UPnPError") var upnpError: UPnPError? = null)

@Namespace(reference = "urn:schemas-upnp-org:control-1-0")
data class UPnPError(
        @field:Element(name = "errorCode") var errorCode: String? = null,
        @field:Element(name = "errorDescription") var errorDescription: String? = null
)

object ActionBodyConverter : Converter<ActionBody> {

    override fun read(node: InputNode): ActionBody {
        val actionName = node.next
        actionName ?: throw ConvertException("Missing required ActionName Node")
        return ActionBody(ActionNameConverter.read(actionName))
    }

    override fun write(node: OutputNode, value: ActionBody) {
        ActionNameConverter.write(node.getChild("actionName"), value.actionName)
    }

}

object ActionNameConverter : Converter<ActionName> {
    override fun read(node: InputNode): ActionName {
        val actionName = node.name.substringBeforeLast("Response")
        val refMatches = "urn:schemas-upnp-org:service:(.+):(\\d)".toRegex().find(node.reference)
        val serviceType = refMatches?.groupValues?.get(1)
        val serviceVersion = refMatches?.groupValues?.get(2)

        serviceType ?: throw ConvertException("Couldn't get serviceType out of the namespace reference", node.reference)
        serviceVersion ?: throw ConvertException("Couldn't get serviceVersion out of the namespace reference", node.reference)

        val arguments = mutableMapOf<String, String>()
        do {
            val arg = node.next?.apply {
                arguments.put(this.name, this.value)
            }
        } while (arg != null)
        return ActionName(actionName, serviceType, serviceVersion, if (arguments.isNotEmpty()) arguments else null)
    }

    override fun write(node: OutputNode, value: ActionName) {
        node.namespaces.setReference(value.namespaceReference, value.namespacePrefix)
        node.name = "${value.namespacePrefix}:${value.actionName}"
        // Print out the arguments nodes
        value.arguments?.forEach {
            node.getChild(it.key).value = it.value
        }
    }

}