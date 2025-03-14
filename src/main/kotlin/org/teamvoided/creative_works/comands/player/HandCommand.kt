package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtOps
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Hand
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message

object HandCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("hand").executes { exe(it, null, null) }.buildChildOf(dispatcher.root)
        val hand = argument("hand", word())
            .suggests { _, builder -> builder.listSuggestions(Hand.entries.map { it.toString().lowercase() }) }
            .executes { exe(it, Hand.valueOf(getString(it, "hand")), null) }
            .buildChildOf(root)
        argument("entity", EntityArgumentType.entity())
            .executes {
                exe(
                    it, Hand.valueOf(getString(it, "hand").uppercase()), EntityArgumentType.getEntity(it, "entity")
                )
            }
            .buildChildOf(hand)
    }

    fun exe(
        ctx: CommandContext<ServerCommandSource>, handIn: Hand?, entity: Entity?
    ): Int {
        val src = ctx.source ?: return 0
        val target: LivingEntity? = if (entity is LivingEntity) entity else src.player
        if (target == null) {
            src.error("Command has no target!")
            return 0
        }
        val ops = target.world.registryManager.createSerializationContext(NbtOps.INSTANCE)
        val hand = handIn ?: Hand.MAIN_HAND
        val stack = target.getStackInHand(hand)
        if (stack.isEmpty) {
            src.error(if (entity == null) "You are not holding an item!" else "The entity is not holding an item!")
            return 0
        }

        val data = ItemStack.CODEC.encodeStart(ops, stack)
        if (data.isError) {
            src.error("Error while trying to get hand data: ${data.error().get().message()}")
            return 0
        }
        src.message("Stack data: ${data.getOrThrow()}")
        return 1
    }
}
