package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.world.entity.Entity
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import net.minecraft.world.level.entity.EntityTypeTest
import net.minecraft.world.phys.AABB
import net.minecraft.world.level.GameRules
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.mc.message

object ClearMobsCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        literal("clear_mobs").executes(::exe).buildChildOf(dispatcher.root)
    }

    val ENTITY_FILTER = object : EntityTypeTest<Entity, Entity> {
        override fun tryCast(entity: Entity): Entity = entity
        override fun getBaseClass(): Class<out Entity> = Entity::class.java
    }

    private fun exe(ctx: CommandContext<CommandSourceStack>): Int {
        val src = ctx.source ?: return 0
        val server = src.server ?: return 0
        val world = src.level ?: return 0

        val gameRules = world.gameRules
        val doModDrops = gameRules.getBoolean(GameRules.RULE_DOMOBLOOT)
        gameRules.getRule(GameRules.RULE_DOMOBLOOT).set(false, server)
        gameRules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, server)

        val list = mutableListOf<Entity>()
        val max = Int.MAX_VALUE
        val box = AABB(-max + 0.0, -max + 0.0, -max + 0.0, max + 1.0, max + 1.0, max + 1.0)
        world.getEntities(ENTITY_FILTER, box, {
            println(it)
            true
        }, list)
        list.forEach {
            println(it)
            it.kill()
        }

        gameRules.getRule(GameRules.RULE_DOMOBLOOT).set(doModDrops, server)
        src.message("Cleared Mobs!")
        return Command.SINGLE_SUCCESS
    }
}
