package kupnp.controlpoint

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

/**
 * Created by chris on 22/09/2016.
 */

@Root(name = "scpd")
data class ServiceDescription(
        @field:Element(name = "specVersion") var specVersion: SpecVersion? = null,
        @field:ElementList(name = "actionList") var actionList: List<Action>? = null,
        @field:ElementList(name = "serviceStateTable") var serviceStateTable: List<StateVariable>? = null
) {

    /**
     * Will find the matching Action by name from the ActionList
     */
    fun containsAction(name: String): Boolean {
        actionList?.forEach {
            if (name.equals(it.name, true)) return true
        }
        return false
    }

    /**
     * Will find the matching StateVariable by name from the StateTable
     */
    fun containsStateVariable(name: String): Boolean {
        serviceStateTable?.forEach {
            if (name.equals(it.name, true)) return true
        }
        return false
    }

}

@Root(name = "action")
data class Action(
        @field:Element(name = "name") var name: String? = null,
        @field:ElementList(name = "argumentList", required = false) var argumentList: List<Argument>? = null
)

@Root(name = "argument")
data class Argument(
        @field:Element(name = "name") var name: String? = null,
        @field:Element(name = "direction") var direction: String? = null,
        @field:Element(name = "retval", required = false) var retval: String? = null,
        @field:Element(name = "relatedStateVariable") var relatedStateVariable: String? = null
)

data class StateVariable(
        @field:Attribute(name = "sendEvents", required = false) var sendEvents: String? = null,
        @field:Attribute(name = "multicast", required = false) var multicast: String? = null,
        @field:Element(name = "name") var name: String? = null,
        @field:Element(name = "dataType") var dataType: String? = null,
        @field:Element(name = "defaultValue", required = false) var defaultValue: String? = null,
        @field:ElementList(name = "allowedValueList", required = false) var allowedValueList: List<String>? = null,
        @field:Element(name = "allowedValueRange", required = false) var allowedValueRange: AllowedValueRange? = null
)

data class AllowedValueRange(
        @field:Element(name = "minimum", required = false) var minimum: Double? = null,
        @field:Element(name = "maximum", required = false) var maximum: Double? = null,
        @field:Element(name = "step", required = false) var step: Double? = null
)