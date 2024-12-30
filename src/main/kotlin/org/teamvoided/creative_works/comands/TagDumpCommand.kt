package org.teamvoided.creative_works.comands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.registry.Registry
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier
import org.teamvoided.creative_works.CreativeWorks.MAIN_COLOR
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.getEntry
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.getRegistry
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.regTagEntryArg
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.registryTagArg
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.getTag
import org.teamvoided.creative_works.util.ltxt
import org.teamvoided.creative_works.util.sendNamedList

object TagDumpCommand {
    fun ctc(text: String): MutableText = ltxt("Click to copy: \"$text\"").styled { it.withColor(MAIN_COLOR) }

    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("tagdump").buildChildOf(dispatcher.root)
        val reg = registryTagArg("registry").buildChildOf(root)
        regTagEntryArg().executes { tagDump(it, getRegistry(it), getEntry(it)) }.buildChildOf(reg)
    }

    private fun tagDump(ctx: CommandContext<ServerCommandSource>, reg: Registry<out Any>?, entryId: Identifier?): Int {
        if (reg == null || entryId == null) return 0
        val src = ctx.source

        val tag = reg.getTag(entryId)
        if (tag.isPresent) {
            val id = tag.get().key.id
            src.sendNamedList(
                "Tag : $id", id.toString(), "Tag is empty!",
                tag.get().map { it.key.get().value.toString() }
            )
        } else src.sendSystemMessage(ltxt("Tag $entryId not found"))
        return 1
    }
}
