package org.teamvoided.creative_works.client

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW
import org.teamvoided.creative_works.client.screen.SpleenScreen
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding as makeKey

object Clint {
    val debugKey: KeyMapping = makeKey(KeyMapping("DebugKeybind", GLFW.GLFW_KEY_G, "Debug"))

    fun init() = ClientTickEvents.END_CLIENT_TICK.register {
        while (debugKey.consumeClick()) {
            it.setScreen(SpleenScreen())
        }
    }
}
