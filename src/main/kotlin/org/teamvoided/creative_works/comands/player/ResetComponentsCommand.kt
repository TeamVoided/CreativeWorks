package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.arguments.ResourceLocationArgument.getId
import net.minecraft.commands.arguments.ResourceLocationArgument.id
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import net.minecraft.world.InteractionHand
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message

object ResetComponentsCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("reset_components").executes { exe(it, null) }.buildChildOf(dispatcher.root)

        argument("component", id())
            .suggests { _, builder -> builder.listSuggestions(
                BuiltInRegistries.DATA_COMPONENT_TYPE.registryKeySet().map { it.identifier().toString() }) }
            .executes { exe(it, BuiltInRegistries.DATA_COMPONENT_TYPE.get(getId(it, "component"))) }
            .buildChildOf(root)
    }

    fun exe(ctx: CommandContext<CommandSourceStack>, type: DataComponentType<*>?): Int {
        val src = ctx.source ?: return 0
        val target = src.player
        if (target == null) {
            src.error("Command has no target!")
            return 0
        }
        val stack = target.getItemInHand(InteractionHand.MAIN_HAND)
        if (stack.isEmpty) {
            src.error("You are not holding an item!")
            return 0
        }

        val comps = stack.componentsPatch
        if (comps.isEmpty) {
            src.error("Item has no components!")
            return 0
        }
        if (type != null && ! stack.has(type)){
            src.error("Item has component ${type}!")
            return 0
        }
        val searchComps = if (type != null) listOf(type) else comps.entrySet().map { it.key }
        for (comp in searchComps) stack.remove(comp)
        src.message(if (searchComps.size == 1) "Removed ${searchComps.first()} component!" else "Removed ${searchComps.size} components!")
        return Command.SINGLE_SUCCESS
    }
}
