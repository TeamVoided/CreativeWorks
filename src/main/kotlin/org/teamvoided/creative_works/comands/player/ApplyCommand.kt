package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType.entity
import net.minecraft.command.argument.EntityArgumentType.getEntity
import net.minecraft.entity.Entity
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message

object ApplyCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
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

    fun exe(ctx: CommandContext<ServerCommandSource>, type: ApplyType, entity: Entity?, amount: Int): Int {
        val src = ctx.source ?: return 0
        val target = entity ?: src.player
        if (target == null) {
            src.error("Command has no target!")
            return 0
        }


        val message = when (type) {
            ApplyType.FIRE_TICKS -> {
                target.fireTicks = amount
                "Fire ticks set to $amount"
            }

            ApplyType.FROZEN_TICKS -> {
                target.frozenTicks = amount
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
    private fun type(it: CommandContext<ServerCommandSource>) = ApplyType.valueOf(getString(it, "type").uppercase())

}
