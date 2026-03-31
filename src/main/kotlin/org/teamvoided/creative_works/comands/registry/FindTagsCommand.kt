package org.teamvoided.creative_works.comands.registry

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.core.DefaultedRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry
import net.minecraft.tags.TagKey
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import net.minecraft.resources.ResourceLocation
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.getEntry
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.getRegistry
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.regEntryArg
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.registryTagArg
import org.teamvoided.creative_works.util.*

object FindTagsCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("findtags").executes { exe(it, BuiltInRegistries.ITEM, null) }.buildChildOf(dispatcher.root)
        val reg = registryTagArg().buildChildOf(root)
        regEntryArg().executes { exe(it, getRegistry(it), getEntry(it)) }.buildChildOf(reg)

//        argument("entry", identifier()).suggests { ctx, builder ->
//            builder.listSuggestions(getRegistry(ctx, ).keys.map { it.value.toString() }.toList())
//        }
//            .executes { exe(it, getRegistry(it, ), getIdentifier(it, "entry")) }
//            .buildChildOf(reg)

    }

    fun exe(ctx: CommandContext<CommandSourceStack>, regsitry: Registry<out Any>, entryId: ResourceLocation?): Int {
        val src = ctx.source ?: return 0
        val player = src.player ?: return 0

        var id = entryId
        val tags = if (entryId == null) {
            val stack = player.mainHandItem
            if (stack.isEmpty) {
                src.error("You are not holding an item!")
                return 0
            }
            id = BuiltInRegistries.ITEM.getKey(stack.item)
            stack.tags
                .sorted(::sortTags)
                .toList()
        } else {
            val entry = regsitry.get(entryId)
            if (entry == null) {
                src.error("Registry entry \"$entryId\" not found!")
                return 0
            }
            if (regsitry is DefaultedRegistry<*> && regsitry.defaultKey != entryId && regsitry.get(regsitry.defaultKey) == entry) {
                src.message("\"$entryId\" returned default registry entry: ${regsitry.defaultKey}!")
                return 0
            }
            regsitry.tags
                .filter { it.second.map { hld -> hld.value() }.contains(entry) }
                .map { it.first }
                .sorted { first, second ->
                    @Suppress("UNCHECKED_CAST")
                    sortTags(first as TagKey<Any>, second as TagKey<Any>)
                }
                .toList()
        }
        src.sendNamedList("Entry : $id", id.toString(), "Entry has no tags!", tags.map { it.location.toString() })
        return 1
    }
}
