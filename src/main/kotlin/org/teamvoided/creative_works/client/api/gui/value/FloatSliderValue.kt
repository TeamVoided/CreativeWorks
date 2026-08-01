package org.teamvoided.creative_works.client.api.gui.value

import org.teamvoided.creative_works.client.api.gui.DebugWidgetType
import org.teamvoided.creative_works.client.api.gui.DebugWidgetTypes

class FloatSliderValue(val min: Float, val max: Float, default: Float) : GuiValue<Float> {

    val float = floatArrayOf(default)

    override fun get(): Float = float[0]

    override fun set(value: Float) = float.set(0, value)

    override fun type(): DebugWidgetType<FloatSliderValue> = DebugWidgetTypes.FLOAT_SLIDER

}