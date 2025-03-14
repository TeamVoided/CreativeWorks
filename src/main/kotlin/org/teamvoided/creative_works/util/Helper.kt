package org.teamvoided.creative_works.util

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.HolderSet.NamedSet
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.teamvoided.creative_works.CreativeWorks.MAIN_COLOR
import org.teamvoided.creative_works.CreativeWorks.SECONDARY_COLOR
import org.teamvoided.creative_works.comands.TagDumpCommand.ctc
import java.awt.Color
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs

fun <S> CommandNode<S>.childOf(node: CommandNode<S>): CommandNode<S> {
    node.addChild(this)
    return this
}

fun <S, Q : ArgumentBuilder<S, Q>> ArgumentBuilder<S, Q>.buildChildOf(node: CommandNode<S>): CommandNode<S> {
    return this.build().childOf(node)
}

fun ltxt(s: String) = Text.literal(s)

fun ServerCommandSource.message(msg: String) = this.sendSystemMessage(Text.literal(msg))
fun ServerCommandSource.error(msg: String) = this.sendError(Text.literal(msg))

fun ServerCommandSource.copyMessage(msg: String, copy: String, copyText: String = copy) =
    this.sendSystemMessage(Text.literal(msg).styled {
        it.withColor(SECONDARY_COLOR).clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copy)
            .hoverEvent(HoverEvent.Action.SHOW_TEXT, ctc(copyText).styled { it.withColor(SECONDARY_COLOR) })
    })

fun ServerCommandSource.openMessage(msg: String, folder: String, openText: String = folder) =
    this.sendSystemMessage(Text.literal(msg).styled {
        it.withColor(SECONDARY_COLOR).clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, folder)
            .hoverEvent(HoverEvent.Action.SHOW_TEXT, ctc(openText).styled { it.withColor(SECONDARY_COLOR) })
    })


fun Style.clickEvent(action: ClickEvent.Action, value: String): Style = this.withClickEvent(ClickEvent(action, value))
fun <T> Style.hoverEvent(action: HoverEvent.Action<T>, value: T): Style = this.withHoverEvent(HoverEvent(action, value))

fun ServerCommandSource.sendNamedList(name: String, nameCopy: String, emptyMessage: String, set: List<String>) {
    this.sendSystemMessage(
        ltxt(name).styled {
            it.withColor(MAIN_COLOR)
                .clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, nameCopy)
                .hoverEvent(HoverEvent.Action.SHOW_TEXT, ctc(nameCopy))
        }
    )
    if (set.toList().isEmpty()) {
        this.sendSystemMessage(ltxt(" $emptyMessage").styled { it.withColor(SECONDARY_COLOR) })
        return
    }
    set.forEach { entry ->
        this.sendSystemMessage(
            ltxt(" - $entry ").styled { style ->
                style.withColor(SECONDARY_COLOR)
                    .clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entry)
                    .hoverEvent(
                        HoverEvent.Action.SHOW_TEXT, ctc(entry).styled { it.withColor(SECONDARY_COLOR) })
            }
        )
    }
}

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

fun <T> sortTags(a: TagKey<T>, b: TagKey<T>) = sortIdentifier(a.id, b.id)
fun sortIdentifier(a: Identifier, b: Identifier) = a.path.compareTo(b.path)

fun <T, R : Registry<T>> CommandContext<ServerCommandSource>.getRegistry(key: RegistryKey<R>): Registry<T> =
    this.source.world.registryManager.get(key)

fun DynamicRegistryManager.getRegistry(id: Identifier): Registry<out Any>? =
    this.getOptional(RegistryKey.ofRegistry<Any>(id)).getOrNull()

fun <T> Registry<T>.getTag(id: Identifier): Optional<NamedSet<T>> = this.getTag(TagKey.of<T>(this.key, id))
