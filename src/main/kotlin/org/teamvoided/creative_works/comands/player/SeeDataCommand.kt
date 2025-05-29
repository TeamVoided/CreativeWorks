package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.client.render.debug.DebugRenderer.getTargetedEntity
import net.minecraft.command.EntityDataObject
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error

object SeeDataCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        literal("see_data").executes(::exe).buildChildOf(dispatcher.root)
    }

    private fun exe(ctx: CommandContext<ServerCommandSource>): Int {
        val src = ctx.source ?: return 0
        val player = src.player
        if (player == null) {
            src.error("Command has to be run by a player!")
            return 0
        }
        val hit = getTargetedEntity(player, 32)
        if (hit.isEmpty) {
            src.error("Didn't find an entity!")
            return 0
        }
        val entity = EntityDataObject(hit.get())
        src.sendFeedback({ entity.feedbackQuery(entity.nbt) }, false)
        return Command.SINGLE_SUCCESS
    }
}
