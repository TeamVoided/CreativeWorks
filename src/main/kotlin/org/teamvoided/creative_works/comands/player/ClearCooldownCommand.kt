package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.arguments.EntityArgument.getPlayer
import net.minecraft.commands.arguments.EntityArgument.player
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.level.ServerPlayer
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message

object ClearCooldownCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("clear_cooldown").executes { exe(it, null) }.buildChildOf(dispatcher.root)
        argument("entity", player()).executes { exe(it, getPlayer(it, "entity")) }.buildChildOf(root)
    }

    fun exe(ctx: CommandContext<CommandSourceStack>, player: ServerPlayer?): Int {
        val src = ctx.source ?: return 0
        val target = player ?: src.player
        if (target == null) {
            src.error("Command has no target!")
            return 0
        }

        @Suppress("INACCESSIBLE_TYPE")
        target.cooldowns.cooldowns.map { it.key }.forEach { target.cooldowns.addCooldown(it, 0) }
        src.message("Cooldowns cleared!")
        return Command.SINGLE_SUCCESS
    }
}
