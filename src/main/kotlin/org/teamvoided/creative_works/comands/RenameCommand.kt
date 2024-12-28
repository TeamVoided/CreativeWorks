package org.teamvoided.creative_works.comands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.component.DataComponentTypes
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message

object RenameCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("rename").executes { exe(it, null) }.buildChildOf(dispatcher.root)
        argument("name", StringArgumentType.greedyString())
            .executes { exe(it, StringArgumentType.getString(it, "name")) }
            .buildChildOf(root)
    }

    fun exe(ctx: CommandContext<ServerCommandSource>, name: String?): Int {
        val src = ctx.source ?: return 0
        val player = src.player ?: return 0
        val stack = player.mainHandStack
        if (stack.isEmpty) {
            src.error("You are not holding an item!")
            return 0
        }
        val text = if (name == null) {
            stack.remove(DataComponentTypes.CUSTOM_NAME)
            "Custom name has been removed!"
        } else {
            stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(name))
            "Item has been renamed!"
        }

        src.message(text)
        return 1
    }
}
