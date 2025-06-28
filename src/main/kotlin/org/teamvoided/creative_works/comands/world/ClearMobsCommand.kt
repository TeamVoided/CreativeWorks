package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.Entity
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.TypeFilter
import net.minecraft.util.math.Box
import net.minecraft.world.GameRules
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.message

object ClearMobsCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        literal("clear_mobs").executes(::exe).buildChildOf(dispatcher.root)
    }

    val ENTITY_FILTER = object : TypeFilter<Entity, Entity> {
        override fun downcast(entity: Entity): Entity = entity
        override fun getBaseClass(): Class<out Entity> = Entity::class.java
    }

    private fun exe(ctx: CommandContext<ServerCommandSource>): Int {
        val src = ctx.source ?: return 0
        val server = src.server ?: return 0
        val world = src.world ?: return 0

        val gameRules = world.gameRules
        val doModDrops = gameRules.getBooleanValue(GameRules.DO_MOB_LOOT)
        gameRules.get(GameRules.DO_MOB_LOOT).setValue(false, server)
        gameRules.get(GameRules.DO_MOB_SPAWNING).setValue(false, server)

        val list = mutableListOf<Entity>()
        val max = Int.MAX_VALUE
        val box = Box(-max + 0.0, -max + 0.0, -max + 0.0, max + 1.0, max + 1.0, max + 1.0)
        world.collectEntities(ENTITY_FILTER, box, {
            println(it)
            true
        }, list)
        list.forEach {
            println(it)
            it.kill()
        }

        gameRules.get(GameRules.DO_MOB_LOOT).setValue(doModDrops, server)
        src.message("Cleared Mobs!")
        return Command.SINGLE_SUCCESS
    }
}
