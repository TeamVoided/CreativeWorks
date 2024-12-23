package org.teamvoided.creative_works.comands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects.*
import net.minecraft.registry.Holder
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import org.teamvoided.creative_works.util.buildChildOf

object PotionCommand {


    @Suppress("UNUSED_VARIABLE")
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("pot").build()
        dispatcher.root.addChild(root)

        literal("nv").pot(NIGHT_VISION, 0).buildChildOf(root)
        literal("st").pot(STRENGTH).buildChildOf(root)
        literal("rs").pot(RESISTANCE).buildChildOf(root)
        literal("sa").pot(SATURATION).buildChildOf(root)

    }

    private fun LiteralArgumentBuilder<ServerCommandSource>.pot(effect: Holder<StatusEffect>, amplifier: Int = 255)
            : LiteralArgumentBuilder<ServerCommandSource> {
        this.executes cmd@{
            val player = it.source?.player ?: return@cmd 0
            player.addStatusEffect(StatusEffectInstance(effect, -1, amplifier, true, false, false))
            return@cmd 1
        }
        return this
    }
}
