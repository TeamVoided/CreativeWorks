package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.ResourceOrTagArgument
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.level.entity.EntityTypeTest
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message
import kotlin.jvm.optionals.getOrNull

object KillItemCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>, ctx: CommandBuildContext) {
        val root = literal("killitem").executes { exe(it, null) }
            .buildChildOf(dispatcher.root)

        argument("type", ResourceOrTagArgument.resourceOrTag(ctx, Registries.ITEM))
            .suggests { _, builder ->
                val lookup = ctx.lookupOrThrow(Registries.ITEM)
                builder.listSuggestions(lookup.listElementIds().map { it.identifier().toString() }.toList())
            }
            .executes {
                val result = ResourceOrTagArgument.getResourceOrTag(it, "type", Registries.ITEM)
                exe(it, result.unwrap().left().getOrNull()?.value())
            }.buildChildOf(root)
    }


    private fun exe(ctx: CommandContext<CommandSourceStack?>, type: Item?): Int {
        val src = ctx.source ?: return 0
        val world = src.level ?: return 0
        val targets = world.getEntities(EntityTypeTest.forExactClass(ItemEntity::class.java)) {
            if (type == null) return@getEntities true
            else it.item.item == type
        }
        if (targets.isEmpty()) {
            src.error("No targets found!")
            return 0
        }

        for (entity in targets) entity.kill(world)

        src.message(if (targets.size == 1) "Killed one Item!" else "Killed ${targets.size} Items!")
        return targets.size
    }
}
