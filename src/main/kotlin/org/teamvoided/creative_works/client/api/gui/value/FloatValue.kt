package org.teamvoided.creative_works.client.api.gui.value

import imgui.type.ImFloat
import org.teamvoided.creative_works.client.api.gui.DebugWidgetType
import org.teamvoided.creative_works.client.api.gui.DebugWidgetTypes

class FloatValue() : GuiValue<Float> {

    val float = ImFloat()

    constructor(default: Float) : this() {
        float.set(default)
    }

    override fun get(): Float = float.get()

    override fun set(value: Float) = float.set(value)

    override fun type(): DebugWidgetType<FloatValue> = DebugWidgetTypes.FLOAT

}