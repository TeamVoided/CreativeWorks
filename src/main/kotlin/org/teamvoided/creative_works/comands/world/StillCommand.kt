package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import net.minecraft.world.level.GameRules
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.message

object StillCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        literal("still").executes(StillCommand::exe).buildChildOf(dispatcher.root)
    }

    fun exe(ctx: CommandContext<CommandSourceStack>): Int {
        val src = ctx.source ?: return 0
        val server = src.server ?: return 0
        val world = src.level ?: return 0

        world.gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, server)
        world.gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, server)
        server.overworld().setWeatherParameters(-1, 0, false, false)
        server.overworld().setDayTime(6000)

        src.message("Weather and daylight cycle disabled!")
        return 1
    }
}
