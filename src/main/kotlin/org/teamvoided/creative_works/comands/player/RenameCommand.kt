package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.core.component.DataComponents
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.mc.error
import org.teamvoided.creative_works.util.mc.message

object RenameCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("rename").executes { exe(it, null) }.buildChildOf(dispatcher.root)
        argument("name", StringArgumentType.greedyString())
            .executes { exe(it, StringArgumentType.getString(it, "name")) }
            .buildChildOf(root)
    }

    fun exe(ctx: CommandContext<CommandSourceStack>, name: String?): Int {
        val src = ctx.source ?: return 0
        val player = src.player ?: return 0
        val stack = player.mainHandItem
        if (stack.isEmpty) {
            src.error("You are not holding an item!")
            return 0
        }
        val text = if (name == null) {
            stack.remove(DataComponents.CUSTOM_NAME)
            "Custom name has been removed!"
        } else {
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(name))
            "Item has been renamed!"
        }

        src.message(text)
        return 1
    }
}
