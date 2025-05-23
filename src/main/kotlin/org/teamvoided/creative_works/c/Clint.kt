package org.teamvoided.creative_works.c

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.option.KeyBind
import org.lwjgl.glfw.GLFW
import org.teamvoided.creative_works.c.screen.SpleenScreen
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding as makeKey

object Clint {
    val debugKey: KeyBind = makeKey(KeyBind("DebugKeybind", GLFW.GLFW_KEY_G, "Debug"))

    fun init() = ClientTickEvents.END_CLIENT_TICK.register {
        while (debugKey.wasPressed()) {
            it.setScreen(SpleenScreen())
        }
    }
}
