package org.teamvoided.creative_works.comands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandBuildContext
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.RegistryEntryOrTagArgument
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import org.teamvoided.creative_works.CreativeWorks.ENTRY_COLOR
import org.teamvoided.creative_works.CreativeWorks.TAG_COLOR
import org.teamvoided.creative_works.CreativeWorks.ltxt
import org.teamvoided.creative_works.util.childOf

object TagDumpCommand {

    val ClickToCopy = ltxt("Click to copy.")

    @Suppress("UNUSED_VARIABLE")
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>, c: CommandBuildContext) {
        val root = literal("tagdump").build()
        dispatcher.root.addChild(root)


        val item = argument("item", RegistryEntryOrTagArgument.create(c, RegistryKeys.ITEM))
            .suggests { cc, builder ->
                val lookup = c.getLookupOrThrow(RegistryKeys.ITEM)
                CommandSource.suggestResource(lookup.streamTagKeys().map { it.id }, builder, "#")
            }
            .executes { itemTagDump(it, RegistryEntryOrTagArgument.getResult(it, "item", RegistryKeys.ITEM)) }
            .build()
            .childOf(root)

    }

    private fun itemTagDump(
        c: CommandContext<ServerCommandSource>,
        result: RegistryEntryOrTagArgument.Result<Item>
    ): Int {
        val src = c.source

        val value = result.resultValue
        if (value.left().isPresent) {
            src.message("Result<single> : ${value.left().get()} ")
        }
        if (value.right().isPresent) {
            val left = value.right().get()

            src.sendSystemMessage(
                ltxt("Tag : ${left.key.id}").setStyle(
                    Style.EMPTY
                        .withColor(TAG_COLOR)
                        .withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, left.key.id.toString()))
                        .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, ClickToCopy))
                )
            )
            left.forEach {
                src.sendSystemMessage(
                    ltxt(" - ${it.value()} ").setStyle(
                        Style.EMPTY
                            .withColor(ENTRY_COLOR)
                            .withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, it.value().toString()))
                            .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, ClickToCopy))
                    )
                )
            }
        }

        return 1
    }

    fun ServerCommandSource.message(msg: String) = this.sendSystemMessage(Text.literal(msg))
}
