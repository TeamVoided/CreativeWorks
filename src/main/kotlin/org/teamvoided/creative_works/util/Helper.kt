package org.teamvoided.creative_works.util

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import java.awt.Color
import kotlin.math.abs

fun <S> CommandNode<S>.childOf(node: CommandNode<S>): CommandNode<S> {
    node.addChild(this)
    return this
}

fun <S> LiteralArgumentBuilder<S>.buildChildOf(node: CommandNode<S>): CommandNode<S> {
    return this.build().childOf(node)
}

fun ltxt(s: String) = Text.literal(s)

fun Color.toHSL(): Triple<Int, Int, Int> {
    // Convert RGB [0, 255] range to [0, 1]
    val rf = this.red / 255.0
    val gf = this.green / 255.0
    val bf = this.blue / 255.0

    // Get the min and max of r,g,b
    val max = maxOf(rf, gf, bf)
    val min = minOf(rf, gf, bf)

    // Lightness is the average of the largest and smallest color components
    val lum = (max + min) / 2

    val hue: Double
    val sat: Double

    if (max == min) { // No saturation
        hue = 0.0
        sat = 0.0
    } else {
        val c = max - min // Chroma
        // Saturation is simply the chroma scaled to fill the interval [0, 1]
        sat = c / (1 - abs(2 * lum - 1))
        hue = when (max) {
            rf -> 60 * ((gf - bf) / c + (if (gf < bf) 6 else 0))
            gf -> 60 * ((bf - rf) / c + 2)
            else -> 60 * ((rf - gf) / c + 4)
        }
    }
    // Convert hue to degrees, sat and lum to percentage
    val h = ((hue + 360) % 360).toInt() // Ensure hue is within [0, 360)
    val s = (sat * 100).toInt()
    val l = (lum * 100).toInt()

    return Triple(h, s, l)
}

fun <T, R : Registry<T>> CommandContext<ServerCommandSource>.getRegistry(key: RegistryKey<R>): Registry<T> =
    this.source.world.registryManager.get(key)
