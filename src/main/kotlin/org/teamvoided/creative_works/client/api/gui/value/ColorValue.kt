package org.teamvoided.creative_works.client.api.gui.value

import imgui.ImColor
import org.teamvoided.creative_works.client.api.gui.DebugWidgetType
import org.teamvoided.creative_works.client.api.gui.DebugWidgetTypes
import java.awt.Color

class ColorValue() : GuiValue<Color> {

    val color = FloatArray(3)

    constructor(default: Color) : this() {
        set(default)
    }

    fun rgb() = ImColor.rgb(color[2], color[1], color[0])

    override fun get(): Color = Color(rgb())

    override fun set(value: Color) {
        color[2] = value.red / 255f
        color[1] = value.green / 255f
        color[0] = value.blue / 255f
    }

    override fun type(): DebugWidgetType<ColorValue> = DebugWidgetTypes.COLOR

}