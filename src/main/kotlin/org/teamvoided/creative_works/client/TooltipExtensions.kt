package org.teamvoided.creative_works.client

import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import com.mojang.serialization.JsonOps
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.network.chat.Component
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.entity.ai.village.poi.PoiTypes
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SpawnEggItem
import org.teamvoided.creative_works.CreativeWorks
import org.teamvoided.creative_works.CreativeWorks.MAIN_COLOR
import org.teamvoided.creative_works.CreativeWorks.SECONDARY_COLOR
import org.teamvoided.creative_works.CreativeWorks.WARNING_COLOR
import org.teamvoided.creative_works.mixin.BucketItemAccessor
import org.teamvoided.creative_works.util.basicJsonToText
import org.teamvoided.creative_works.util.ltxt
import org.teamvoided.creative_works.util.sortTags
import org.teamvoided.creative_works.util.toText
import java.util.*
import kotlin.jvm.optionals.getOrNull

object TooltipExtensions {
    fun renderTooltip() = ItemTooltipCallback.EVENT.register { stack, ctx, cfg, text ->
        if (cfg.isAdvanced) {
            if (Screen.hasShiftDown()) tagToolTips(stack, text)
            if (Screen.hasAltDown()) componentToolTips(stack, text, ctx)
            // Mixin to this to get comp copying and dumping
            // MinecraftClient.getInstance().keyboard
        }
    }

    @Suppress("DEPRECATION")
    private fun tagToolTips(stack: ItemStack, text: MutableList<Component>) {
        val item = stack.item

        text.listTags("Item", item.builtInRegistryHolder().toSortedTags())

        if (item is BlockItem) {
            text.listTags("Block", item.block.builtInRegistryHolder().toSortedTags())
            val poi = PoiTypes.forState(item.block.defaultBlockState()).getOrNull()
            if (poi != null)
                text.listTags("POI", poi.toSortedTags(), " (${(poi as Holder.Reference<PoiType>).key().location()})")
        }

        if (item is SpawnEggItem)
            text.listTags("Entity", item.getType(stack).builtInRegistryHolder().toSortedTags())

        if (item is BucketItemAccessor)
            text.listTags("Fluid", item.cw_content().builtInRegistryHolder().toSortedTags())

        val storedEnchants = stack.get(DataComponents.STORED_ENCHANTMENTS)
        val enchants = stack.get(DataComponents.ENCHANTMENTS)
        if (storedEnchants != null) {
            val enchantments = storedEnchants.keySet()
            if (enchantments.size > 1)
                text.addLast(ltxt("Item has more then 1 stored enchantment").withColor(WARNING_COLOR))
            else if (enchantments.isEmpty())
                text.addLast(ltxt("No stored enchantments").withColor(WARNING_COLOR))
            else
                text.listTags("Enchantment", enchantments.first().toSortedTags())
        } else if (enchants != null) {
            val enchantments = enchants.keySet()
            if (enchantments.size > 1)
                text.addLast(ltxt("Item has more then 1 enchantment").withColor(WARNING_COLOR))
            else if (!enchantments.isEmpty())
                text.listTags("Enchantment", enchantments.first().toSortedTags())
        }

        val instrument = stack.get(DataComponents.INSTRUMENT)
        if (instrument != null)
            text.listTags("Instrument", instrument.toSortedTags())

    }

    private fun componentToolTips(stack: ItemStack, text: MutableList<Component>, ctx: Item.TooltipContext) {
        val ops = ctx.registries()?.createSerializationContext(JsonOps.INSTANCE) ?: return

        val rawComponents = stack.components
        if (rawComponents !is PatchedDataComponentMap) return

        if (CreativeWorks.config.enableBaseComponents)
            rawComponents.prototype.toList().sortedBy { it.type.toString() }.let { components ->
                if (components.isNotEmpty()) {
                    text.addLast(ltxt("Base Components:").withColor(MAIN_COLOR))
                    components.forEach {
                        val result = it.encodeValue(ops)
                        val data =
                            if (result.isSuccess) result.getOrThrow()
                            else JsonPrimitive(result.error().getOrNull()?.message() ?: "Failed to get encoding error!")
                        text.addLast(
                            ltxt(" ${it.type.toString().removeMc()}: ").withColor(SECONDARY_COLOR)
                                .append(basicJsonToText(data).toText())
                        )
                    }
                }
            }
        rawComponents.patch.toList().sortedBy { it.first.toString() }.let { components ->
            if (components.isNotEmpty()) {
                text.addLast(ltxt("Components:").withColor(MAIN_COLOR))
                val removed = JsonArray()
                components.forEach comp@{ (type, data) ->
                    val ts = type.toString().removeMc()
                    if (data.isEmpty) removed.add(ts)
                    else {
                        val x = type as DataComponentType<Any>
                        val y = data as Optional<Any>
                        val result = x.codec()?.encodeStart(ops, y.get())
                        val resultData =
                            if (result != null && result.isSuccess) result.getOrThrow()
                            else JsonPrimitive(
                                result?.error()?.getOrNull()?.message() ?: "Failed to get encoding error!"
                            )
                        text.addLast(
                            ltxt(" $ts: ").withColor(SECONDARY_COLOR)
                                .append(basicJsonToText(resultData).toText())
                        )
                    }
                }
                if (!removed.isEmpty) {
                    text.addLast(ltxt("Removed Components: ").withColor(WARNING_COLOR))
                    text.addLast(ltxt(" ").append(basicJsonToText(removed).toText()))
                }
            }
        }
    }

    fun <T : Any> MutableList<Component>.listTags(name: String, tags: MutableList<TagKey<T>>, suffix: String = "") =
        if (tags.isNotEmpty()) {
            this.addLast(ltxt("$name Tags${suffix}:").withColor(MAIN_COLOR))
            tags.forEach { tag -> this.addLast(ltxt(" #${tag.location}").withColor(SECONDARY_COLOR)) }
        } else Unit

    fun <T> Holder<T>.toSortedTags() = this.tags().sorted(::sortTags).toList()
    fun String.removeMc() = this.removePrefix("minecraft:")
}