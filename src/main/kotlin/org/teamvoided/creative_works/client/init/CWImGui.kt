package org.teamvoided.creative_works.client.init

import foundry.imgui.api.ImGuiMCEvents
import org.teamvoided.creative_works.client.api.gui.DebugUI.renderDebuggingUI

object CWImGui {

    fun init() {
        ImGuiMCEvents.INSTANCE.postRenderImGuiEvent(::renderGui)
    }

    fun renderGui() {
        renderDebuggingUI()
    }
}