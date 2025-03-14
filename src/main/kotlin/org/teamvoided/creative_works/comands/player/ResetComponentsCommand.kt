package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandBuildContext
import net.minecraft.command.argument.IdentifierArgumentType.getIdentifier
import net.minecraft.command.argument.IdentifierArgumentType.identifier
import net.minecraft.component.DataComponentType
import net.minecraft.registry.Registries
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Hand
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message

object ResetComponentsCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("reset_components").executes { exe(it, null) }.buildChildOf(dispatcher.root)

        argument("component", identifier())
            .suggests { _, builder -> builder.listSuggestions(Registries.DATA_COMPONENT_TYPE.keys.map { it.value.toString() }) }
            .executes { exe(it, Registries.DATA_COMPONENT_TYPE.get(getIdentifier(it, "component"))) }
            .buildChildOf(root)
    }

    fun exe(ctx: CommandContext<ServerCommandSource>, type: DataComponentType<*>?): Int {
        val src = ctx.source ?: return 0
        val target = src.player
        if (target == null) {
            src.error("Command has no target!")
            return 0
        }
        val stack = target.getStackInHand(Hand.MAIN_HAND)
        if (stack.isEmpty) {
            src.error("You are not holding an item!")
            return 0
        }

        val comps = stack.componentPatch
        if (comps.isEmpty) {
            src.error("Item has no components!")
            return 0
        }
        if (type != null && ! stack.contains(type)){
            src.error("Item has component ${type}!")
            return 0
        }
        val searchComps = if (type != null) listOf(type) else comps.entrySet().map { it.key }
        for (comp in searchComps) stack.remove(comp)
        src.message(if (searchComps.size == 1) "Removed ${searchComps.first()} component!" else "Removed ${searchComps.size} components!")
        return Command.SINGLE_SUCCESS
    }
}
