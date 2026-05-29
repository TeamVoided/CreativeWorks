package org.teamvoided.creative_works.client.api.gui.value

import imgui.type.ImBoolean
import org.teamvoided.creative_works.client.api.gui.DebugWidgetType
import org.teamvoided.creative_works.client.api.gui.DebugWidgetTypes

class BoolValue() : GuiValue<Boolean> {

    val bool = ImBoolean()

    constructor(default: Boolean) : this() {
        bool.set(default)
    }

    override fun get(): Boolean = bool.get()

    override fun set(value: Boolean) = bool.set(value)

    fun toggle(): Boolean {
        set(!get())
        return get()
    }

    override fun type(): DebugWidgetType<BoolValue> = DebugWidgetTypes.BOOL

}