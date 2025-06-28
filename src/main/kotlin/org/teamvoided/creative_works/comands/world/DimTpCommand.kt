package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.BlockPosArgumentType.blockPos
import net.minecraft.command.argument.BlockPosArgumentType.getBlockPos
import net.minecraft.command.argument.DimensionArgumentType.dimension
import net.minecraft.command.argument.DimensionArgumentType.getDimensionArgument
import net.minecraft.entity.MovementFlag
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message

object DimTpCommand {
    const val DIM = "dimension"
    const val POS = "position"
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val tp = literal("dimtp").buildChildOf(dispatcher.root)
        val dim = argument(DIM, dimension())
            .executes { tp(it, getDimensionArgument(it, DIM), null) }
            .buildChildOf(tp)
        argument(POS, blockPos())
            .executes { tp(it, getDimensionArgument(it, DIM), getBlockPos(it, POS)) }
            .buildChildOf(dim)
    }

    private fun tp(
        ctx: CommandContext<ServerCommandSource>, world: ServerWorld, blockPos: BlockPos?,
    ): Int {
        val src = ctx.source ?: return 0
        val player = src.player
        if (player == null) {
            src.error("Command can only be run by a Player!")
            return 0
        }
        val pos = blockPos ?: player.blockPos

        player.teleport(
            world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), MovementFlag.ALL, player.pitch, player.yaw
        )
        src.message("Teleporting to ${world.registryKey.value}!")
        return Command.SINGLE_SUCCESS
    }
}
