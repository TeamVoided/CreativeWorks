package org.teamvoided.creative_works.comands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message

object ExampleCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        literal("EXAMPLE").executes(::exe).buildChildOf(dispatcher.root)
    }

    private fun exe(ctx: CommandContext<CommandSourceStack>): Int {
        val src = ctx.source ?: return 0
        val server = src.server ?: return 0
        val world = src.level ?: return 0
        val target = src.player
        if (target == null) {
            src.error("Command has no target!")
            return 0
        }

        src.message("Exmaple!")
        return Command.SINGLE_SUCCESS
    }
}
