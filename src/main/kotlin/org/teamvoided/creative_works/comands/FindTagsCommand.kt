package org.teamvoided.creative_works.comands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.IdentifierArgumentType.getIdentifier
import net.minecraft.command.argument.IdentifierArgumentType.identifier
import net.minecraft.registry.DefaultedRegistry
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.getRegistry
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.registryTagArg
import org.teamvoided.creative_works.comands.misc.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.*

object FindTagsCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("findtags").executes { exe(it, Registries.ITEM, null) }.buildChildOf(dispatcher.root)
        val reg = registryTagArg("registry").buildChildOf(root)

        argument("entry", identifier()).suggests { ctx, builder ->
            builder.listSuggestions(getRegistry(ctx, "registry").keys.map { it.value.toString() }.toList())
        }
            .executes { exe(it, getRegistry(it, "registry"), getIdentifier(it, "entry")) }
            .buildChildOf(reg)

    }

    fun exe(ctx: CommandContext<ServerCommandSource>, regsitry: Registry<out Any>, entryId: Identifier?): Int {
        val src = ctx.source ?: return 0
        val player = src.player ?: return 0

        var id = entryId
        val tags = if (entryId == null) {
            val stack = player.mainHandStack
            if (stack.isEmpty) {
                src.error("You are not holding an item!")
                return 0
            }
            id = Registries.ITEM.getId(stack.item)
            stack.streamTags()
                .sorted(::sortTags)
                .toList()
        } else {
            val entry = regsitry.get(entryId)
            if (entry == null) {
                src.error("Registry entry \"$entryId\" not found!")
                return 0
            }
            if (regsitry is DefaultedRegistry<*> && regsitry.defaultId != entryId && regsitry.get(regsitry.defaultId) == entry) {
                src.message("\"$entryId\" returned default registry entry: ${regsitry.defaultId}!")
                return 0
            }
            regsitry.tags
                .filter { it.second.map { hld -> hld.value() }.contains(entry) }
                .map { it.first }
                .sorted(::sortTags)
                .toList()
        }
        src.sendNamedList("Entry : $id", id.toString(), "Entry has no tags!", tags.map { it.id.toString() })
        return 1
    }
}
