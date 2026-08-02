package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.server.commands.data.BlockDataAccessor
import net.minecraft.server.commands.data.EntityDataAccessor
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.entity.projectile.ProjectileUtil.getHitResultOnViewVector
import net.minecraft.world.phys.BlockHitResult
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.mc.error
import java.util.*
import kotlin.jvm.optionals.getOrNull

object SeeDataCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        literal("see_data").executes(::exe).buildChildOf(dispatcher.root)
    }

    private fun exe(ctx: CommandContext<CommandSourceStack>): Int {
        val src = ctx.source ?: return 0
        val level = src.level ?: return -1
        val player = src.player
        if (player == null) {
            src.error("Command has to be run by a player!")
            return 0
        }
        val hit = player.getTargetedEntity(32.0)
        if (!hit.isEmpty) {
            val dataAccess = EntityDataAccessor(hit.get())
            src.sendSuccess({ dataAccess.getPrintSuccess(dataAccess.data) }, false)
            return Command.SINGLE_SUCCESS
        }

        val hitBlock = player.getHitBlock(32.0).getOrNull()
        if (hitBlock != null) {
            val be = level.getBlockEntity(hitBlock.blockPos)
            if (be != null) {
                val dataAccess = BlockDataAccessor(be, hitBlock.blockPos)
                src.sendSuccess({ dataAccess.getPrintSuccess(dataAccess.data) }, false)
                return Command.SINGLE_SUCCESS
            }
        }

        src.error("Didn't see an entity or block entity!")
        return 0
    }

    fun Entity?.getTargetedEntity(maxDist: Double): Optional<Entity> {
        if (this == null) return Optional.empty<Entity>()

        val eyePos = this.eyePosition
        val distVec = this.getViewVector(1.0f).scale(maxDist)
        val scanlineVec = eyePos.add(distVec)
        val aABB = this.boundingBox.expandTowards(distVec).inflate(1.0)
        val maxDistSq = maxDist * maxDist
        val hitResult = ProjectileUtil.getEntityHitResult(
            this, eyePos, scanlineVec, aABB, { !it.isSpectator && it.isPickable }, maxDistSq
        ) ?: return Optional.empty<Entity>()
        return if (eyePos.distanceToSqr(hitResult.getLocation()) > maxDistSq)
            Optional.empty<Entity>()
        else
            Optional.of(hitResult.entity)
    }

    fun Player.getHitBlock(range: Double): Optional<BlockHitResult> {
        return Optional.ofNullable(
            getHitResultOnViewVector(this, { !it.isSpectator && it.isPickable }, range) as? BlockHitResult
        )
    }

}