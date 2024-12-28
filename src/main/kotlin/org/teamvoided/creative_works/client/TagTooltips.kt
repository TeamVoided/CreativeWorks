package org.teamvoided.creative_works.client

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.BlockItem
import net.minecraft.item.SpawnEggItem
import org.teamvoided.creative_works.CreativeWorks.ENTRY_COLOR
import org.teamvoided.creative_works.CreativeWorks.TAG_COLOR
import org.teamvoided.creative_works.util.ltxt

object TagTooltips {
    fun renderTagTooltip() = ItemTooltipCallback.EVENT.register { stack, _, cfg, text ->
        if (Screen.hasShiftDown() && cfg.shouldShowAdvancedDetails()) {
            val itemTags = stack
                .streamTags()
                .sorted { a, b -> a.id.path.compareTo(b.id.path) }
                .toList()

            if (itemTags.isNotEmpty())
                text.addLast(ltxt("ItemTags:").setColor(TAG_COLOR))

            itemTags.forEach { tag ->
                text.addLast(ltxt(" #${tag.id}").setColor(ENTRY_COLOR))
            }
            val item = stack.item
            if (item is BlockItem) {
                val state = item.block.defaultState
                val blockTags = state
                    .streamTags()
                    .sorted { a, b -> a.id.path.compareTo(b.id.path) }
                    .toList()

                if (blockTags.isNotEmpty())
                    text.addLast(ltxt("BlockTags:").setColor(TAG_COLOR))

                blockTags.forEach { tag ->
                    text.addLast(ltxt(" #${tag.id}").setColor(ENTRY_COLOR))
                }
            }
            if (item is SpawnEggItem){
                val entity = item.getEntityType(stack)
                val entityTags = entity.builtInRegistryHolder
                    .streamTags()
                    .sorted { a, b -> a.id.path.compareTo(b.id.path) }
                    .toList()

                if (entityTags.isNotEmpty())
                    text.addLast(ltxt("EntityTags:").setColor(TAG_COLOR))

                entityTags.forEach { tag ->
                    text.addLast(ltxt(" #${tag.id}").setColor(ENTRY_COLOR))
                }
            }
        }
    }
}