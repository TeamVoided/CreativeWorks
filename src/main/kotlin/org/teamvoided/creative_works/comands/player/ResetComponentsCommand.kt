package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandBuildContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Hand
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message

object ResetComponentsCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>, ctx: CommandBuildContext) {
        literal("reset_components").executes(::exe).buildChildOf(dispatcher.root)
    }

    fun exe(ctx: CommandContext<ServerCommandSource>): Int {
        val src = ctx.source ?: return 0
        val target = src.player
        if (target == null) {
            src.error("Command has no target!")
            return 0
        }
        val stack = target.getStackInHand(Hand.MAIN_HAND)
        if (stack.isEmpty) {
            src.error("You are not holding an item!")
            return 0
        }
        val comps = stack.componentPatch
        if (comps.isEmpty) {
            src.error("Item has no components!")
            return 0
        }
        var amount = 0
        for (comp in comps.entrySet()) {
            stack.remove(comp.key)
            amount++
        }
        src.message("Removed $amount component${if (amount > 1) "s" else ""}!")
        return Command.SINGLE_SUCCESS
    }
}
