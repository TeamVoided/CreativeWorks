package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.client.renderer.debug.DebugRenderer.getTargetedEntity
import net.minecraft.server.commands.data.EntityDataAccessor
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.mc.error

object SeeDataCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        literal("see_data").executes(::exe).buildChildOf(dispatcher.root)
    }

    private fun exe(ctx: CommandContext<CommandSourceStack>): Int {
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
        val entity = EntityDataAccessor(hit.get())
        src.sendSuccess({ entity.getPrintSuccess(entity.data) }, false)
        return Command.SINGLE_SUCCESS
    }
}
