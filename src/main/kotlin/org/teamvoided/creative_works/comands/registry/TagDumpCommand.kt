package org.teamvoided.creative_works.comands.registry

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.core.Registry
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
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
    fun ctc(text: String): MutableComponent = ltxt("Click to copy: \"$text\"").withStyle { it.withColor(MAIN_COLOR) }
    fun cto(text: String): MutableComponent = ltxt("Click to open: \"$text\"").withStyle { it.withColor(MAIN_COLOR) }


    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("tagdump").buildChildOf(dispatcher.root)
        val reg = registryTagArg("registry").buildChildOf(root)
        regTagEntryArg().executes { tagDump(it, getRegistry(it), getEntry(it)) }.buildChildOf(reg)
    }

    private fun tagDump(ctx: CommandContext<CommandSourceStack>, reg: Registry<out Any>?, entryId: ResourceLocation?): Int {
        if (reg == null || entryId == null) return 0
        val src = ctx.source

        val tag = reg.getTag(entryId)
        if (tag.isPresent) {
            val id = tag.get().key().location
            src.sendNamedList(
                "Tag : $id", id.toString(), "Tag is empty!",
                tag.get().map { it.unwrapKey().get().location().toString() }
            )
        } else src.sendSystemMessage(ltxt("Tag $entryId not found"))
        return 1
    }
}
