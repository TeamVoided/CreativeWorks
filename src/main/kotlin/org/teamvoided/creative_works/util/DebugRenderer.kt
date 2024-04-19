package org.teamvoided.creative_works.util

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.math.BlockPos

object DebugRenderer {
    private var shouldRender = false
    private val debugValues = mutableMapOf<String, Any?>() //Name - Value
    private var client: MinecraftClient? = null

    fun toggle() {
        shouldRender = !shouldRender
    }

    fun Any?.addToRenderer(name: String = this?.javaClass?.simpleName ?: "nil") {
        debugValues[name] = this
    }
    fun clear() = debugValues.clear()

    private fun MutableMap.MutableEntry<String, Any?>.toText(): String = "${this.key}: ${this.value}"

    fun render(gui: GuiGraphics) {
        client = MinecraftClient.getInstance()
        if (!shouldRender || client == null) return
        val textRend = client!!.textRenderer

        var idx = 0
        for (it in debugValues) {
            gui.drawText(textRend, it.toText(), 3, 3 + ((1 + textRend.fontHeight) * idx), 0xffffff, true)
            idx++
        }
    }
    fun getCords(c: MinecraftClient? = client): BlockPos? = c?.player?.blockPos
}
