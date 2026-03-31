package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.core.Holder
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects.*
import org.teamvoided.creative_works.util.buildChildOf

object PotionCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("pot").build()
        dispatcher.root.addChild(root)

        literal("nvs").pot(NIGHT_VISION, 0).buildChildOf(root)
        literal("str").pot(STRENGTH).buildChildOf(root)
        literal("res").pot(RESISTANCE).buildChildOf(root)
        literal("sat").pot(SATURATION).buildChildOf(root)
        literal("hst").pot(HASTE).buildChildOf(root)
        literal("clear").executes {
            val player = it.source?.player ?: return@executes 0
            player.removeAllEffects()
            1
        }.buildChildOf(root)

    }

    private fun LiteralArgumentBuilder<CommandSourceStack>.pot(effect: Holder<MobEffect>, amplifier: Int = 255)
            : LiteralArgumentBuilder<CommandSourceStack> {
        this.executes cmd@{
            val player = it.source?.player ?: return@cmd 0
            player.addEffect(MobEffectInstance(effect, -1, amplifier, true, false, false))
            return@cmd 1
        }
        return this
    }
}
