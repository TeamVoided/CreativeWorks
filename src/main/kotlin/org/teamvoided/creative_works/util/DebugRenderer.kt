package org.teamvoided.creative_works.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos

object DebugRenderer {
    private var shouldRender = false
    private val debugValues = mutableMapOf<String, Any?>() //Name - Value
    private var client: Minecraft? = null

    fun toggle() {
        shouldRender = !shouldRender
    }

    fun Any?.addToRenderer(name: String = this?.javaClass?.simpleName ?: "nil") {
        debugValues[name] = this
    }
    fun clear() = debugValues.clear()

    private fun MutableMap.MutableEntry<String, Any?>.toText(): String = "${this.key}: ${this.value}"

    fun render(gui: GuiGraphics) {
        client = Minecraft.getInstance()
        if (!shouldRender || client == null) return
        val textRend = client!!.font

        var idx = 0
        for (it in debugValues) {
            gui.drawString(textRend, it.toText(), 3, 3 + ((1 + textRend.lineHeight) * idx), 0xffffff, true)
            idx++
        }
    }
    fun getCords(c: Minecraft? = client): BlockPos? = c?.player?.blockPosition()
}
