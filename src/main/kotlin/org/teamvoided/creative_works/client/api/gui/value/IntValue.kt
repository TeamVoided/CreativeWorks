package org.teamvoided.creative_works.client.api.gui.value

import imgui.type.ImInt
import org.teamvoided.creative_works.client.api.gui.DebugWidgetType
import org.teamvoided.creative_works.client.api.gui.DebugWidgetTypes

class IntValue() : GuiValue<Int> {

    val int = ImInt()

    constructor(default: Int) : this() {
        int.set(default)
    }

    override fun get(): Int = int.get()

    override fun set(value: Int) = int.set(value)

    override fun type(): DebugWidgetType<IntValue> = DebugWidgetTypes.INT

}