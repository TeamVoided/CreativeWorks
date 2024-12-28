package org.teamvoided.creative_works.comands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.IdentifierArgumentType.getIdentifier
import net.minecraft.command.argument.IdentifierArgumentType.identifier
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.HolderSet.NamedSet
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier
import org.teamvoided.creative_works.CreativeWorks.ENTRY_COLOR
import org.teamvoided.creative_works.CreativeWorks.TAG_COLOR
import org.teamvoided.creative_works.comands.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.*
import java.util.*
import kotlin.jvm.optionals.getOrNull

object TagDumpCommand {
    private fun ctc(text: String): MutableText = ltxt("Click to copy: \"$text\"").styled { it.withColor(TAG_COLOR) }

    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("tagdump").buildChildOf(dispatcher.root)

        val reg = argument("registry", identifier()).suggests { cc, builder ->
            val list = cc.source.world.registryManager.registries().map { it.value() }
                .filter { it.tagKeys.toList().isNotEmpty() }.map { it.key.value.toString() }
            builder.listSuggestions(list.toList())
        }.build().childOf(root)

        argument("entry", identifier()).suggests { cc, builder ->
            val list = cc.source.world.registryManager.getRegistry(getIdentifier(cc, "registry"))
                ?.tagKeys?.map { it.id.toString() }
            builder.listSuggestions(list?.toList())
        }.executes { tagDump(it, getIdentifier(it, "registry"), getIdentifier(it, "entry")) }.build().childOf(reg)

    }

    private fun tagDump(ctx: CommandContext<ServerCommandSource>, regId: Identifier?, entryId: Identifier?): Int {
        if (regId == null || entryId == null) return 0
        val src = ctx.source

        val reg = src.world.registryManager.getRegistry(regId) ?: return 0
        val tag = reg.getTag(entryId)
        if (tag.isPresent) printNamedSet(src, tag.get())
        else src.sendSystemMessage(ltxt("Tag $entryId not found"))
        return 1
    }

    fun <T> Registry<T>.getTag(id: Identifier): Optional<NamedSet<T>> = this.getTag(TagKey.of<T>(this.key, id))
    fun DynamicRegistryManager.getRegistry(id: Identifier): Registry<out Any>? =
        this.getOptional(RegistryKey.ofRegistry<Any>(id)).getOrNull()

    fun <T : Any> printNamedSet(src: ServerCommandSource, set: NamedSet<T>) {
        val name = set.key.id.toString()
        src.sendSystemMessage(
            ltxt("Tag : $name").styled {
                it.withColor(TAG_COLOR)
                    .clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, name)
                    .hoverEvent(HoverEvent.Action.SHOW_TEXT, ctc(name))
            }
        )
        if (set.toList().isEmpty()) {
            src.sendSystemMessage(ltxt(" Tag is empty!").styled { it.withColor(ENTRY_COLOR) })
            return
        }
        set.map { it.key.get().value.toString() }.forEach { entry ->
            src.sendSystemMessage(
                ltxt(" - $entry ").styled { style ->
                    style.withColor(ENTRY_COLOR)
                        .clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entry)
                        .hoverEvent(
                            HoverEvent.Action.SHOW_TEXT, ctc(entry).styled { it.withColor(ENTRY_COLOR) })
                }
            )
        }
    }
}
