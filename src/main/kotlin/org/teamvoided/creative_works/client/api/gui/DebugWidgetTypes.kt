package org.teamvoided.creative_works.client.api.gui

import imgui.ImGui
import net.minecraft.resources.ResourceLocation
import org.teamvoided.creative_works.CreativeWorks.id
import org.teamvoided.creative_works.CreativeWorks.log
import org.teamvoided.creative_works.client.api.gui.value.*

object DebugWidgetTypes {

    typealias WidgetRenderer<T> = (label: String, value: T) -> Unit

    internal var REGISTRY = mutableMapOf<ResourceLocation, DebugWidgetType<*>>()
    internal var RENDERERS = mutableMapOf<DebugWidgetType<*>, WidgetRenderer<*>>()

    @Suppress("UNCHECKED_CAST")
    fun <T : GuiValue<*>> getRenderer(value: T): WidgetRenderer<T>? = RENDERERS[value.type()] as? WidgetRenderer<T>

    var BOOL = register<BoolValue>(id("bool")) { label, value -> value.set(ImGui.button(label)) }
    var INT = register<IntValue>(id("int")) { label, value -> ImGui.inputInt(label, value.int) }
    var FLOAT = register<FloatValue>(id("float")) { label, value -> ImGui.inputFloat(label, value.float) }
    var DOUBLE = register<DoubleValue>(id("double")) { label, value -> ImGui.inputDouble(label, value.double) }
    var LONG = register<LongValue>(id("long")) { label, value -> ImGui.inputScalar(label, value.long) }
    var STRING = register<StringValue>(id("string")) { label, value -> ImGui.inputText(label, value.string) }
    var TEXT = register<TextValue>(id("text")) { label, value -> ImGui.text(label + value.get()) }
    var COLOR = register<ColorValue>(id("color")) { label, value -> ImGui.colorEdit3(label, value.color) }
    var ARBITRARY = register<ArbitraryValue>(id("arbitrary")) { label, value -> value.renderUI(label) }


    fun <T : GuiValue<*>> register(name: String, renderer: WidgetRenderer<T>): DebugWidgetType<T> {
        return register(id(name), renderer)
    }

    fun <T : GuiValue<*>> register(id: ResourceLocation, renderer: WidgetRenderer<T>): DebugWidgetType<T> {
        val type = DebugWidgetType.SimpleWidgetType<T>()
        val oldType = REGISTRY.put(id, type)
        if (oldType != null) {
            log.info("Override debug widget type: {} - {}", id, oldType)
        }
        val oldRender = RENDERERS.put(type, renderer)
        if (oldRender != null) {
            log.info("Override debug widget renderer: {}", id)
        }

        return type
    }

    fun init() = Unit

}