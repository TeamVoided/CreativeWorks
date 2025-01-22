package org.teamvoided.creative_works.network

import imgui.ImGui
import imgui.type.ImFloat
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import xyz.breadloaf.imguimc.Imguimc
import xyz.breadloaf.imguimc.debug.DebugRenderable
import xyz.breadloaf.imguimc.interfaces.Renderable
import xyz.breadloaf.imguimc.interfaces.Theme
import xyz.breadloaf.imguimc.theme.ImGuiDarkTheme


fun runTests(c: ClientPlayNetworking.Context) {
    println("Running tests")
    val window = TestWindow()
    Imguimc.pushRenderable(window)
}

var dfloat = ImFloat(1f)

class TestWindow : Renderable {
    override fun getName(): String = "Test Window"
    override fun getTheme(): Theme = ImGuiDarkTheme()
    override fun render() {
        ImGui.begin(name)
        ImGui.text("Hello, world!")
        if (ImGui.button("Click me!")) {
            Imguimc.pullRenderableAfterRender(this)
        }
        ImGui.inputFloat("DebutText", dfloat)
        ImGui.end()
    }
}

val debugRenderer = DebugRenderable()
var debugRendererToggle = false
fun imguiDebug() {
    debugRendererToggle = !debugRendererToggle
    if (debugRendererToggle) Imguimc.pushRenderable(debugRenderer)
    else Imguimc.pullRenderableAfterRender(debugRenderer)
}