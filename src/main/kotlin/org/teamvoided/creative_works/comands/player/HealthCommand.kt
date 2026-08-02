package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.FloatArgumentType.floatArg
import com.mojang.brigadier.arguments.FloatArgumentType.getFloat
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.arguments.EntityArgument.entities
import net.minecraft.commands.arguments.EntityArgument.getEntities
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.mc.error
import org.teamvoided.creative_works.util.mc.message

object HealthCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("health")
            .executes { exe(it, null, null) }
            .buildChildOf(dispatcher.root)
        val amount = argument("amount", floatArg(-1f, Float.MAX_VALUE))
            .suggests { _, builder -> builder.listSuggestions(listOf(-1, 5, 10, 20).map { it.toString() }) }
            .executes { exe(it, getFloat(it, "amount"), null) }
            .buildChildOf(root)
        argument("entities", entities())
            .executes { exe(it, getFloat(it, "amount"), getEntities(it, "entities")) }
            .buildChildOf(amount)

        dispatcher.register(literal("heal").executes { exe(it, null, null) }.redirect(root))
    }

    fun exe(ctx: CommandContext<CommandSourceStack>, amountIn: Float?, entityIn: MutableCollection<out Entity>?): Int {
        val src = ctx.source ?: return 0
        val toHeal = mutableListOf<LivingEntity>()
        if (entityIn == null) toHeal.add(src.player ?: return 0)
        else {
            val mapped = entityIn.filterIsInstance<LivingEntity>()
            if (mapped.isEmpty()) {
                src.error("No entities found!")
                return 0
            }
            toHeal.addAll(mapped)
        }

        val amount = amountIn ?: -1f
        for (entity in toHeal) {
            entity.health = if (amount == -1f) entity.maxHealth
            else (entity.health + amount).coerceAtMost(entity.maxHealth)
        }

        when (val entities = toHeal.size) {
            0 -> src.error("No entities found!")
            1 -> src.message(if (amount == -1f) "Fully Healed Entity!" else "Added $amount health to Entity!")
            else -> src.message(if (amount == -1f) "Fully Healed $entities Entities!" else "Added $amount health to $entities Entities!")
        }

        return 1
    }
}
