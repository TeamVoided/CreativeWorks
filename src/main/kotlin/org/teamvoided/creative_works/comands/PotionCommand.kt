package org.teamvoided.creative_works.comands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.registry.Holder
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import org.teamvoided.creative_works.util.childOf

object PotionCommand {


    @Suppress("UNUSED_VARIABLE")
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("pot").build()
        dispatcher.root.addChild(root)

        val nvNode = literal("nv")
            .executes { pot(it, StatusEffects.NIGHT_VISION, 0) }
            .build()
            .childOf(root)
        val stNode = literal("st")
            .executes { pot(it, StatusEffects.STRENGTH) }
            .build()
            .childOf(root)
        val rsNode = literal("rs")
            .executes { pot(it, StatusEffects.RESISTANCE) }
            .build()
            .childOf(root)
        val saNode = literal("sa")
            .executes { pot(it, StatusEffects.SATURATION) }
            .build()
            .childOf(root)

    }

    private fun pot(c: CommandContext<ServerCommandSource>, effect: Holder<StatusEffect>, amplifier: Int = 255): Int {
        val player = c.source.player ?: return 0
        player.addStatusEffect(
            StatusEffectInstance(effect, -1, amplifier, true, false, false)
        )
        return 1
    }
}
