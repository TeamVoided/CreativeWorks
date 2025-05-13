package org.teamvoided.creative_works.client

import imgui.ImColor
import imgui.ImGui
import imgui.type.*
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.option.KeyBind
import org.lwjgl.glfw.GLFW
import xyz.breadloaf.imguimc.Imguimc
import xyz.breadloaf.imguimc.interfaces.Renderable
import xyz.breadloaf.imguimc.interfaces.Theme
import xyz.breadloaf.imguimc.theme.ImGuiDarkTheme
import java.awt.Color
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding as makeKey

@Suppress("unused")
object DebugWidgetRegistry {
    private val customScreen = CustomScreen()
    var widgetsEnabled = false
    val debugKey: KeyBind = makeKey(KeyBind("Debug Widget Key", GLFW.GLFW_KEY_V, "Debug"))

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register {
            if (debugKey.wasPressed()) {
                widgetsEnabled = !widgetsEnabled
                if (widgetsEnabled) Imguimc.pushRenderable(customScreen)
                else Imguimc.pullRenderableAfterRender(customScreen)
            }
        }
    }

    private val debugValues = mutableListOf<Pair<String, Any>>()

    class CustomScreen : Renderable {
        override fun getName(): String = "Debug Widget Window"
        override fun getTheme(): Theme = ImGuiDarkTheme()
        override fun render() {
            ImGui.begin(name)
            ImGui.text("Debug Widgets")
            if (ImGui.button("Close")) Imguimc.pullRenderableAfterRender(this)
            debugValues.forEach { (key, value) ->
                when (value) {
                    is ImBoolean -> value.set(ImGui.button(key))
                    is ImDouble -> ImGui.inputDouble(key, value)
                    is ImFloat -> ImGui.inputFloat(key, value)
                    is ImInt -> ImGui.inputInt(key, value)
                    is ImString -> ImGui.inputText(key, value)
                    is FloatArray -> {
                        if (value.size == 3) ImGui.colorEdit3(key, value)
                        else ImGui.text("Invalid color array size")
                    }
                    is String -> ImGui.text(value)
                    else -> ImGui.text("Unknown type [${value.javaClass.simpleName}] for $key ")
                }
            }
            ImGui.end()
        }
    }

    fun addButton(name: String, default: Boolean = false): ImBoolean {
        val bool = ImBoolean()
        bool.set(default)
        debugValues.add(name to bool)
        return bool
    }

    fun addDouble(name: String, default: Double = 0.0): ImDouble {
        val double = ImDouble()
        double.set(default)
        debugValues.add(name to double)
        return double
    }

    fun addFloat(name: String, default: Float = 0f): ImFloat {
        val float = ImFloat()
        float.set(default)
        debugValues.add(name to float)
        return float
    }

    fun addInt(name: String, default: Int = 0): ImInt {
        val int = ImInt()
        int.set(default)
        debugValues.add(name to int)
        return int
    }

    fun addString(name: String, default: String = ""): ImString {
        val string = ImString()
        string.set(default)
        debugValues.add(name to string)
        return string
    }

    fun addColor(name: String, default: Color = Color.WHITE): FloatArray {
        val color = FloatArray(3)
        color[0] = default.red.toFloat()
        color[1] = default.green.toFloat()
        color[2] = default.blue.toFloat()
        debugValues.add(name to color)
        return color
    }
    fun addText(text: String) = debugValues.add("" to text)
}

fun FloatArray.getColor() = ImColor.rgb(this[2], this[1], this[0])
