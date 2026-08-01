package org.teamvoided.creative_works.client.example

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.Minecraft
import org.teamvoided.creative_works.client.api.gui.DebugUI
import org.teamvoided.creative_works.client.api.gui.DebugUI.addText

@Suppress("unused")
object TestRenderer {
    val textX = DebugUI.addInt(": Text X", 10)
    val textY = DebugUI.addInt(": Text Y", 10)
    val color = DebugUI.addColor(": Color")
    val funnyValue = DebugUI.addSlider(": Slider", 0, 30)

    fun init() = HudRenderCallback.EVENT.register { gui, delta ->
        gui.drawString(
            Minecraft.getInstance().font,
            "Debug Text ${funnyValue.get()}",
            textX.get(), textY.get(),
            color.rgb(), true
        )
        addText("text_id", ": ${Minecraft.getInstance().player?.position()}")
    }
}