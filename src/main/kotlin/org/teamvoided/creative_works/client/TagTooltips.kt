package org.teamvoided.creative_works.client

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.gui.screen.Screen
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.BlockItem
import net.minecraft.item.SpawnEggItem
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.Text
import org.teamvoided.creative_works.CreativeWorks.MAIN_COLOR
import org.teamvoided.creative_works.CreativeWorks.SECONDARY_COLOR
import org.teamvoided.creative_works.CreativeWorks.WARNING_COLOR
import org.teamvoided.creative_works.util.ltxt
import org.teamvoided.creative_works.util.sortTags

object TagTooltips {
    fun renderTagTooltip() = ItemTooltipCallback.EVENT.register { stack, _, cfg, text ->
        if (Screen.hasShiftDown() && cfg.shouldShowAdvancedDetails()) {
            val itemTags = stack
                .streamTags()
                .sorted(::sortTags)
                .toList()

            text.listTags("Item", itemTags)

            val item = stack.item

            if (item is BlockItem) {
                val blockTags = item.block.defaultState
                    .streamTags()
                    .sorted(::sortTags)
                    .toList()
                text.listTags("Block", blockTags)
            }

            if (item is SpawnEggItem) {
                val entityTags = item.getEntityType(stack).builtInRegistryHolder
                    .streamTags()
                    .sorted(::sortTags)
                    .toList()

                text.listTags("Entity", entityTags)
            }

            val enchantmentsComponent = stack.get(DataComponentTypes.STORED_ENCHANTMENTS)
            if (enchantmentsComponent != null) {
                val enchantments = enchantmentsComponent.enchantments
                if (enchantments.size > 1)
                    text.addLast(ltxt("Has more then 1 stored enchantment").setColor(WARNING_COLOR))
                else if (enchantments.isEmpty())
                    text.addLast(ltxt("No stored enchantments").setColor(WARNING_COLOR))
                else {
                    val enchantmentTags = enchantments.first()
                        .streamTags()
                        .sorted(::sortTags)
                        .toList()

                    text.listTags("Enchantment", enchantmentTags)
                }

            }
        }
    }

    fun <T : Any> MutableList<Text>.listTags(name: String, tags: MutableList<TagKey<T>>) = if (tags.isNotEmpty()) {
        this.addLast(ltxt("$name Tags:").setColor(MAIN_COLOR))
        tags.forEach { tag -> this.addLast(ltxt(" #${tag.id}").setColor(SECONDARY_COLOR)) }
    } else Unit
}