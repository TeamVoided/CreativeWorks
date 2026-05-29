package org.teamvoided.creative_works.client.api.gui.value

import imgui.type.ImDouble
import org.teamvoided.creative_works.client.api.gui.DebugWidgetType
import org.teamvoided.creative_works.client.api.gui.DebugWidgetTypes

class DoubleValue() : GuiValue<Double> {

    val double = ImDouble()

    constructor(default: Double) : this() {
        double.set(default)
    }

    override fun get(): Double = double.get()

    override fun set(value: Double) = double.set(value)

    override fun type(): DebugWidgetType<DoubleValue> = DebugWidgetTypes.DOUBLE

}