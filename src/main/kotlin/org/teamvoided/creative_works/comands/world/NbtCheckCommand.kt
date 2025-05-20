package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.mob.MobEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.message

object NbtCheckCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("nbt_check").buildChildOf(dispatcher.root)
        argument("nbt", StringArgumentType.word())
            .executes { exe(it, StringArgumentType.getString(it, "nbt")) }
            .buildChildOf(root)
    }

    private fun exe(ctx: CommandContext<ServerCommandSource>, nbtName: String): Int {
        val src = ctx.source ?: return 0
        val world = src.world ?: return 0
        val target = src.player ?: return 0
        val pos = target.blockPos

        var posIdx = 0
        for (type in Registries.ENTITY_TYPE) {
            val entity = type.create(world) ?: continue
            val nbt = NbtCompound()
            try {
                entity.writeNbt(nbt)
            } catch (e: Exception) {
                println(e)
            }


            if (nbt.contains(nbtName)) {
                val real = pos.north(posIdx)
                entity.setNoGravity(true)
                if (entity is MobEntity) entity.isAiDisabled = true
                entity.setPosition(real.x.toDouble(), real.y.toDouble(), real.z.toDouble())
                world.spawnEntity(entity)
                posIdx += 2
            }
        }

        src.message("Exmaple!")
        return Command.SINGLE_SUCCESS
    }
}
