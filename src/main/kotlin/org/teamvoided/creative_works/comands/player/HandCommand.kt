package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.NbtOps
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import net.minecraft.world.InteractionHand
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message

object HandCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("hand").executes { exe(it, null, null) }.buildChildOf(dispatcher.root)
        val hand = argument("hand", word())
            .suggests { _, builder -> builder.listSuggestions(InteractionHand.entries.map { it.toString().lowercase() }) }
            .executes { exe(it, InteractionHand.valueOf(getString(it, "hand")), null) }
            .buildChildOf(root)
        argument("entity", EntityArgument.entity())
            .executes {
                exe(
                    it, InteractionHand.valueOf(getString(it, "hand").uppercase()), EntityArgument.getEntity(it, "entity")
                )
            }
            .buildChildOf(hand)
    }

    fun exe(
        ctx: CommandContext<CommandSourceStack>, handIn: InteractionHand?, entity: Entity?
    ): Int {
        val src = ctx.source ?: return 0
        val target: LivingEntity? = entity as? LivingEntity ?: src.player
        if (target == null) {
            src.error("Command has no target!")
            return 0
        }
        val ops = target.level().registryAccess().createSerializationContext(NbtOps.INSTANCE)
        val hand = handIn ?: InteractionHand.MAIN_HAND
        val stack = target.getItemInHand(hand)
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
