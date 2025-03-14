package org.teamvoided.creative_works.comands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandBuildContext
import net.minecraft.command.argument.RegistryEntryOrTagArgument
import net.minecraft.entity.ItemEntity
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.TypeFilter
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message
import kotlin.jvm.optionals.getOrNull

object KillItemCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>, ctx: CommandBuildContext) {
        val root = literal("killitem").executes { exe(it, null) }
            .buildChildOf(dispatcher.root)

        argument("type", RegistryEntryOrTagArgument.create(ctx, RegistryKeys.ITEM))
            .suggests { _, builder ->
                val lookup = ctx.getLookupOrThrow(RegistryKeys.ITEM)
                builder.listSuggestions(lookup.streamElementKeys().map { it.value.toString() }.toList())
            }
            .executes {
                val result = RegistryEntryOrTagArgument.getResult(it, "type", RegistryKeys.ITEM)
                exe(it, result.resultValue.left().getOrNull()?.value())
            }.buildChildOf(root)
    }


    private fun exe(ctx: CommandContext<ServerCommandSource?>, type: Item?): Int {
        val src = ctx.source ?: return 0
        val world = src.world ?: return 0
        val targets = world.getEntitiesByType(TypeFilter.equals(ItemEntity::class.java)) {
            if (it is ItemEntity) {
                if (type == null) return@getEntitiesByType true
                else it.stack.item == type
            } else false
        }
        if (targets.isEmpty()) {
            src.error("No targets found!")
            return 0
        }

        for (entity in targets) entity.kill()

        src.message(if (targets.size == 1) "Killed one Item!" else "Killed ${targets.size} Items!")
        return targets.size
    }
}
