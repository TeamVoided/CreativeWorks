package org.teamvoided.creative_works.client.api.gui.value

import org.teamvoided.creative_works.client.api.gui.DebugWidgetType
import org.teamvoided.creative_works.client.api.gui.DebugWidgetTypes

class IntSliderValue(val min: Int, val max: Int, default: Int) : GuiValue<Int> {

    val int = intArrayOf(default)

    override fun get(): Int = int[0]

    override fun set(value: Int) = int.set(0, value)

    override fun type(): DebugWidgetType<IntSliderValue> = DebugWidgetTypes.INT_SLIDER

}