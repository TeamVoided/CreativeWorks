package org.teamvoided.creative_works.client.screen

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.RenderLayer
import net.minecraft.text.Text
import net.minecraft.util.Util
import java.awt.Color
import kotlin.math.abs

class SpleenScreen : Screen(Text.literal("Gay!")) {
    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(graphics, mouseX, mouseY, delta)
        val l = Util.getMeasuringTimeMs()

        val i = this.width / 2
        val j = this.height / 2
        drawChunkMap(graphics, i, j, 2, 0)
        val k: Int = 10 + 9 + 2
        graphics.drawCenteredShadowedText(this.textRenderer, Text.literal("GAY!"), i, j - k, 16777215)
    }

    fun drawChunkMap(
        graphics: GuiGraphics,
        centerX: Int,
        centerY: Int,
        pixelSize: Int,
        pixelMargin: Int,
    ) {
        val i = pixelSize + pixelMargin
        val j = 30
        val k = j * i - pixelMargin
        val l = 50
        val m = l * i - pixelMargin
        val n = centerX - m / 2
        val o = centerY - m / 2
        val outOff = k / 2 + 1
        val outline = -16772609
        graphics.runManaged {
            if (pixelMargin != 0) {
                graphics.fill(centerX - outOff, centerY - outOff, centerX - outOff + 1, centerY + outOff, outline)
                graphics.fill(centerX + outOff - 1, centerY - outOff, centerX + outOff, centerY + outOff, outline)
                graphics.fill(centerX - outOff, centerY - outOff, centerX + outOff, centerY - outOff + 1, outline)
                graphics.fill(centerX - outOff, centerY + outOff - 1, centerX + outOff, centerY + outOff, outline)
            }
            for (r in 0..<l) {
                for (s in 0..<l) {
                    val x = n + r * i
                    val y = o + s * i
                    val color = if ((abs(r) % 2 == 0) xor (abs(s) % 2 == 0)) 0xff000000.toInt() else 0xffffffff.toInt()
                    graphics.fill(x, y, x + pixelSize, y + pixelSize, color)
                }
            }
            graphics.customFill(10, 30, 40, 45, 0, Color.WHITE.rgb)
        }
    }

    fun GuiGraphics.customFill(x1: Number, y1: Number, x2: Number, y2: Number, z: Number, color: Int) {
        var x1proc = x1.toFloat()
        var y1 = y1.toFloat()
        var x2proc = x2.toFloat()
        var y2 = y2.toFloat()
        val matrix4f = this.matrices.peek().model
        /*if (x1proc < x2proc) {
            x1proc = x2.toFloat()
            x2proc = x1.toFloat()
        }

        if (y1 < y2) {
            val i = y1
            y1 = y2
            y2 = i
        }*/

        val vertexConsumer: VertexConsumer = this.vertexConsumers.getBuffer(RenderLayer.getGui())
        vertexConsumer.xyz(matrix4f, x1proc, y1, z.toFloat()).color(color)
        vertexConsumer.xyz(matrix4f, x1proc, y2, z.toFloat()).color(color)
        vertexConsumer.xyz(matrix4f, x2proc, y2, z.toFloat()).color(color)
        vertexConsumer.xyz(matrix4f, x2proc, y1, z.toFloat()).color(color)
//        this.flushIfUnmanaged()
    }
}