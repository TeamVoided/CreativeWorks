package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos
import net.minecraft.commands.arguments.coordinates.BlockPosArgument.getSpawnablePos
import net.minecraft.commands.arguments.DimensionArgument.dimension
import net.minecraft.commands.arguments.DimensionArgument.getDimension
import net.minecraft.world.entity.RelativeMovement
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.level.ServerLevel
import net.minecraft.core.BlockPos
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message

object DimTpCommand {
    const val DIM = "dimension"
    const val POS = "position"
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val tp = literal("dimtp").buildChildOf(dispatcher.root)
        val dim = argument(DIM, dimension())
            .executes { tp(it, getDimension(it, DIM), null) }
            .buildChildOf(tp)
        argument(POS, blockPos())
            .executes { tp(it, getDimension(it, DIM), getSpawnablePos(it, POS)) }
            .buildChildOf(dim)
    }

    private fun tp(
        ctx: CommandContext<CommandSourceStack>, world: ServerLevel, blockPos: BlockPos?,
    ): Int {
        val src = ctx.source ?: return 0
        val player = src.player
        if (player == null) {
            src.error("Command can only be run by a Player!")
            return 0
        }
        val pos = blockPos ?: player.blockPosition()

        player.teleportTo(
            world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), RelativeMovement.ALL, player.xRot, player.yRot
        )
        src.message("Teleporting to ${world.dimension().identifier()}!")
        return Command.SINGLE_SUCCESS
    }
}
