package org.teamvoided.creative_works.comands.registry

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.core.Registry
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.getEntry
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.getRegistry
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.regTagEntryArg
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.registryTagArg
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.getTag
import org.teamvoided.creative_works.util.mc.error
import org.teamvoided.creative_works.util.mc.textMain
import org.teamvoided.creative_works.util.sendNamedList

object TagDumpCommand {
    fun ctc(text: String): MutableComponent = textMain("Click to copy: \"$text\"")
    fun cto(text: String): MutableComponent = textMain("Click to open: \"$text\"")


    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("tagdump").buildChildOf(dispatcher.root)
        val reg = registryTagArg("registry").buildChildOf(root)
        regTagEntryArg().executes { dumpTag(it, getRegistry(it), getEntry(it)) }.buildChildOf(reg)
    }

    private fun dumpTag(
        ctx: CommandContext<CommandSourceStack>, reg: Registry<out Any>?, entryId: ResourceLocation?,
    ): Int {
        if (reg == null || entryId == null) return 0
        val src = ctx.source

        val tag = reg.getTag(entryId)
        if (tag.isPresent) {
            val id = tag.get().key().location
            src.sendNamedList(
                "Tag : $id", id.toString(), "Tag is empty!",
                tag.get().map { it.unwrapKey().get().location().toString() }
            )
        } else {
            src.error("Tag $entryId not found")
        }
        return 1
    }
}
