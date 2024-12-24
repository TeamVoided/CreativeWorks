package org.teamvoided.creative_works

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.BlockItem
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.teamvoided.creative_works.comands.network.CWNet
import org.teamvoided.creative_works.init.CWCommands
import org.teamvoided.creative_works.util.ltxt

@Suppress("unused")
object CreativeWorks {
    const val MODID = "creative_works"

    const val TAG_COLOR = 0xDDDDDD
    const val ENTRY_COLOR = 0xAAAAAA

    @JvmField
    val log: Logger = LoggerFactory.getLogger(CreativeWorks::class.simpleName)

    fun commonInit() {
        CWNet.init()
        CWCommands.init()
    }

    fun clientInit() {
        CWNet.clientInit()
        ItemTooltipCallback.EVENT.register { stack, c, cfg, text ->
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
                if (stack.item is BlockItem) {
                    val state = (stack.item as BlockItem).block.defaultState
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
            }
        }
    }


    fun id(path: String) = Identifier.of(MODID, path)
}
