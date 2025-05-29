package org.teamvoided.creative_works.client

import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import com.mojang.serialization.JsonOps
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.gui.screen.Screen
import net.minecraft.component.DataComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.PatchedDataComponentMap
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.SpawnEggItem
import net.minecraft.registry.Holder
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.Text
import org.teamvoided.creative_works.CreativeWorks
import org.teamvoided.creative_works.CreativeWorks.MAIN_COLOR
import org.teamvoided.creative_works.CreativeWorks.SECONDARY_COLOR
import org.teamvoided.creative_works.CreativeWorks.WARNING_COLOR
import org.teamvoided.creative_works.util.basicJsonToText
import org.teamvoided.creative_works.util.ltxt
import org.teamvoided.creative_works.util.sortTags
import org.teamvoided.creative_works.util.toText
import java.util.*
import kotlin.jvm.optionals.getOrNull

object TooltipExtensions {
    fun renderTooltip() = ItemTooltipCallback.EVENT.register { stack, ctx, cfg, text ->
        if (cfg.shouldShowAdvancedDetails()) {
            if (Screen.hasShiftDown()) tagToolTips(stack, text)
            if (Screen.hasAltDown()) componentToolTips(stack, text, ctx)
            // Mixin to this to get comp copying and dumping
            // MinecraftClient.getInstance().keyboard
        }
    }

    @Suppress("DEPRECATION")
    private fun tagToolTips(stack: ItemStack, text: MutableList<Text>) {
        val item = stack.item

        text.listTags("Item", item.builtInRegistryHolder.toSortedTags())

        if (item is BlockItem) text.listTags("Block", item.block.builtInRegistryHolder.toSortedTags())

        if (item is SpawnEggItem)
            text.listTags("Entity", item.getEntityType(stack).builtInRegistryHolder.toSortedTags())

        val enchantmentsComponent = stack.get(DataComponentTypes.STORED_ENCHANTMENTS)
        if (enchantmentsComponent != null) {
            val enchantments = enchantmentsComponent.enchantments
            if (enchantments.size > 1)
                text.addLast(ltxt("Has more then 1 stored enchantment").setColor(WARNING_COLOR))
            else if (enchantments.isEmpty())
                text.addLast(ltxt("No stored enchantments").setColor(WARNING_COLOR))
            else text.listTags("Enchantment", enchantments.first().toSortedTags())
        }
    }

    private fun componentToolTips(stack: ItemStack, text: MutableList<Text>, ctx: Item.TooltipContext) {
        val ops = ctx.lookup?.createSerializationContext(JsonOps.INSTANCE) ?: return

        val rawComponents = stack.components
        if (rawComponents !is PatchedDataComponentMap) return

        if (CreativeWorks.config.enableBaseComponents)
            rawComponents.baseComponents.toList().sortedBy { it.type.toString() }.let { components ->
                if (components.isNotEmpty()) {
                    text.addLast(ltxt("Base Components:").setColor(MAIN_COLOR))
                    components.forEach {
                        val result = it.encodeValue(ops)
                        val data =
                            if (result.isSuccess) result.getOrThrow()
                            else JsonPrimitive(result.error().getOrNull()?.message() ?: "Failed to get encoding error!")
                        text.addLast(
                            ltxt(" ${it.type.toString().removeMc()}: ").setColor(SECONDARY_COLOR)
                                .append(basicJsonToText(data).toText())
                        )
                    }
                }
            }
        rawComponents.patchedComponents.toList().sortedBy { it.first.toString() }.let { components ->
            if (components.isNotEmpty()) {
                text.addLast(ltxt("Components:").setColor(MAIN_COLOR))
                val removed = JsonArray()
                components.forEach comp@{ (type, data) ->
                    val ts = type.toString().removeMc()
                    if (data.isEmpty) removed.add(ts)
                    else {
                        val x = type as DataComponentType<Any>
                        val y = data as Optional<Any>
                        val result = x.codec?.encodeStart(ops, y.get())
                        val resultData =
                            if (result != null && result.isSuccess) result.getOrThrow()
                            else JsonPrimitive(
                                result?.error()?.getOrNull()?.message() ?: "Failed to get encoding error!"
                            )
                        text.addLast(
                            ltxt(" $ts: ").setColor(SECONDARY_COLOR)
                                .append(basicJsonToText(resultData).toText())
                        )
                    }
                }
                if (!removed.isEmpty) {
                    text.addLast(ltxt("Removed Components: ").setColor(WARNING_COLOR))
                    text.addLast(ltxt(" ").append(basicJsonToText(removed).toText()))
                }
            }
        }
    }

    fun <T : Any> MutableList<Text>.listTags(name: String, tags: MutableList<TagKey<T>>) = if (tags.isNotEmpty()) {
        this.addLast(ltxt("$name Tags:").setColor(MAIN_COLOR))
        tags.forEach { tag -> this.addLast(ltxt(" #${tag.id}").setColor(SECONDARY_COLOR)) }
    } else Unit

    fun <T> Holder<T>.toSortedTags() = this.streamTags().sorted(::sortTags).toList()
    fun String.removeMc() = this.removePrefix("minecraft:")
}