package org.teamvoided.creative_works.client.api.gui.value

import imgui.type.ImLong
import org.teamvoided.creative_works.client.api.gui.DebugWidgetType
import org.teamvoided.creative_works.client.api.gui.DebugWidgetTypes

class LongValue() : GuiValue<Long> {

    val long = ImLong()

    constructor(default: Long) : this() {
        long.set(default)
    }

    override fun get(): Long = long.get()

    override fun set(value: Long) = long.set(value)

    override fun type(): DebugWidgetType<LongValue> = DebugWidgetTypes.LONG

}