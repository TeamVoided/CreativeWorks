package org.teamvoided.creative_works.client

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.Minecraft


@Suppress("unused")
object TestRenderer {
    val textX = DebugWidgetRegistry.addInt(": Text X", 10)
    val textY = DebugWidgetRegistry.addInt(": Text Y", 10)
    val color = DebugWidgetRegistry.addColor(": Color")

    fun init() = HudRenderCallback.EVENT.register { gui, delta ->
        gui.drawString(
            Minecraft.getInstance().font,
            "Debug Text",
            textX.get(), textY.get(),
            color.getColor(), true
        )
    }
}
