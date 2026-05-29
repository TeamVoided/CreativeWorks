package org.teamvoided.creative_works.client.init

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW
import org.teamvoided.creative_works.CreativeWorks.log
import org.teamvoided.creative_works.CreativeWorksClient
import org.teamvoided.creative_works.client.api.gui.DebugUI.uiEnabled
import org.teamvoided.creative_works.client.screen.SpleenScreen


object CWKeyMappings {
    val testingKey: KeyMapping = key("Testing Keybind")
    val debugUIKey: KeyMapping = key("Toggle Debug UI", GLFW.GLFW_KEY_X)

    fun init() = Unit

    fun tick(client: Minecraft) {
        if (testingKey.consumeClick()) {
            client.setScreen(SpleenScreen())
        }

        if (CreativeWorksClient.hasImGui && debugUIKey.consumeClick()) {
            log.info("Value: {}", uiEnabled.toggle())
        }
    }

    fun key(name: String, key: Int = GLFW.GLFW_KEY_UNKNOWN): KeyMapping {
        return registerKeyBinding(KeyMapping(name, key, "Debug"))
    }

}