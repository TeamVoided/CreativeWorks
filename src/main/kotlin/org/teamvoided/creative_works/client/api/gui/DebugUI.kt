package org.teamvoided.creative_works.client.api.gui

import imgui.ImGui
import org.teamvoided.creative_works.CreativeWorks.log
import org.teamvoided.creative_works.client.api.gui.value.*
import java.awt.Color

@Suppress("unused")
object DebugUI {

    var uiEnabled = BoolValue(false)
    internal val debugValues = mutableMapOf<String, GuiValue<*>>()

    fun <V, T : GuiValue<V>> addGuiValue(id: String, value: T): T {
        if (id.trim().isEmpty()) {
            log.error("Label is empty for {}", value)
            return value
        }
        debugValues[id] = value
        return value
    }

    fun addButton(label: String, default: Boolean = false) = addGuiValue(label, BoolValue(default))

    fun addDouble(label: String, default: Double = 0.0) = addGuiValue(label, DoubleValue(default))

    fun addFloat(label: String, default: Float = 0f) = addGuiValue(label, FloatValue(default))

    fun addInt(label: String, default: Int = 0) = addGuiValue(label, IntValue(default))

    fun addString(label: String, default: String = "") = addGuiValue(label, StringValue(default))

    fun addColor(label: String, default: Color = Color.WHITE) = addGuiValue(label, ColorValue(default))

    fun addText(label: String, text: String) = addGuiValue(label, TextValue(text))

    fun addSlider(label: String, min: Float, max: Float, default: Float = 0f) =
        addGuiValue(label, FloatSliderValue(min, max, default))

    fun addSlider(label: String, min: Int, max: Int, default: Int = 0) =
        addGuiValue(label, IntSliderValue(min, max, default))

    internal fun renderDebuggingUI() {
        if (!uiEnabled.get()) return
        ImGui.begin("Debug Widget Window", uiEnabled.bool)

        ImGui.text("Debug Widgets")

        for ((label, value) in debugValues) {
            val renderer = DebugWidgetTypes.getRenderer(value)
            if (renderer != null) {
                renderer(label, value)
            } else {
                ImGui.text("Unknown type [${value.javaClass.simpleName}] for $label ")
            }

        }

        ImGui.end()
    }

}