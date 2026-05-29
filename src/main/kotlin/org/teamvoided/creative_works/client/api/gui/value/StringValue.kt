package org.teamvoided.creative_works.client.api.gui.value

import imgui.type.ImString
import org.teamvoided.creative_works.client.api.gui.DebugWidgetType
import org.teamvoided.creative_works.client.api.gui.DebugWidgetTypes

class StringValue() : GuiValue<String> {

    val string = ImString()

    constructor(default: String) : this() {
        string.set(default)
    }

    override fun get(): String = string.get()

    override fun set(value: String) = string.set(value)

    override fun type(): DebugWidgetType<StringValue> = DebugWidgetTypes.STRING

}