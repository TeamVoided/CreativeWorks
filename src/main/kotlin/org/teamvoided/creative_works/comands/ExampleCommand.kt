package org.teamvoided.creative_works.comands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.message

object ExampleCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        literal("EXAMPLE").executes(::exe).buildChildOf(dispatcher.root)
    }

    fun exe(ctx: CommandContext<ServerCommandSource>): Int {
        val src = ctx.source ?: return 0
        val server = src.server ?: return 0
        val world = src.world ?: return 0


        src.message("Exmaple!")
        return Command.SINGLE_SUCCESS
    }
}
