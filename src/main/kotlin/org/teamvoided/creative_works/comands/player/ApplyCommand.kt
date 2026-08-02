package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.arguments.EntityArgument.entity
import net.minecraft.commands.arguments.EntityArgument.getEntity
import net.minecraft.world.entity.Entity
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.mc.error
import org.teamvoided.creative_works.util.mc.message

object ApplyCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("apply").buildChildOf(dispatcher.root)

        val type = argument("type", word())
            .suggests { _, builder -> builder.listSuggestions(applyTypes()) }
            .executes { exe(it, type(it), null, 100) }
            .buildChildOf(root)

        val target = argument("target", entity())
            .executes { exe(it, type(it), getEntity(it, "target"), 100) }
            .buildChildOf(type)

        argument("amount", integer(1))
            .executes { exe(it, type(it), getEntity(it, "target"), getInteger(it, "amount")) }
            .buildChildOf(target)
    }

    fun exe(ctx: CommandContext<CommandSourceStack>, type: ApplyType, entity: Entity?, amount: Int): Int {
        val src = ctx.source ?: return 0
        val target = entity ?: src.player
        if (target == null) {
            src.error("Command has no target!")
            return 0
        }


        val message = when (type) {
            ApplyType.FIRE_TICKS -> {
                target.remainingFireTicks = amount
                "Fire ticks set to $amount"
            }

            ApplyType.FROZEN_TICKS -> {
                target.ticksFrozen = amount
                "Frozen ticks set to $amount"
            }
        }

        src.message(message)
        return Command.SINGLE_SUCCESS
    }

    enum class ApplyType {
        FIRE_TICKS, FROZEN_TICKS;
    }

    private fun applyTypes() = ApplyType.entries.map { it.toString().lowercase() }
    private fun type(it: CommandContext<CommandSourceStack>) = ApplyType.valueOf(getString(it, "type").uppercase())

}
