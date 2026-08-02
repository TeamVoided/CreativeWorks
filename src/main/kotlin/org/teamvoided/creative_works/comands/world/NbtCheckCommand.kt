package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.world.entity.Mob
import net.minecraft.nbt.CompoundTag
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.mc.message

object NbtCheckCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("nbt_check").buildChildOf(dispatcher.root)
        argument("nbt", StringArgumentType.word())
            .executes { exe(it, StringArgumentType.getString(it, "nbt")) }
            .buildChildOf(root)
    }

    private fun exe(ctx: CommandContext<CommandSourceStack>, nbtName: String): Int {
        val src = ctx.source ?: return 0
        val world = src.level ?: return 0
        val target = src.player ?: return 0
        val pos = target.blockPosition()

        var posIdx = 0
        for (type in BuiltInRegistries.ENTITY_TYPE) {
            val entity = type.create(world) ?: continue
            val nbt = CompoundTag()
            try {
                entity.saveWithoutId(nbt)
            } catch (e: Exception) {
                println(e)
            }


            if (nbt.contains(nbtName)) {
                val real = pos.north(posIdx)
                entity.setNoGravity(true)
                if (entity is Mob) entity.setNoAi(true)
                entity.setPos(real.x.toDouble(), real.y.toDouble(), real.z.toDouble())
                world.addFreshEntity(entity)
                posIdx += 2
            }
        }

        src.message("Exmaple!")
        return Command.SINGLE_SUCCESS
    }
}
