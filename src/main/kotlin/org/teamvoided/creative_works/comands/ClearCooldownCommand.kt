package org.teamvoided.creative_works.comands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType.getPlayer
import net.minecraft.command.argument.EntityArgumentType.player
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message

object ClearCooldownCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("clear_cooldown").executes { exe(it, null) }.buildChildOf(dispatcher.root)
        argument("entity", player()).executes { exe(it, getPlayer(it, "entity")) }.buildChildOf(root)
    }

    fun exe(ctx: CommandContext<ServerCommandSource>, player: ServerPlayerEntity?): Int {
        val src = ctx.source ?: return 0
        val target = player ?: src.player
        if (target == null) {
            src.error("Command has no target!")
            return 0
        }

        target.itemCooldownManager.entries.map { it.key }.forEach { target.itemCooldownManager.set(it, 0) }
        src.message("Cooldowns cleared!")
        return Command.SINGLE_SUCCESS
    }
}
