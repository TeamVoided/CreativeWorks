package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.world.GameRules
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.message

object StillCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        literal("still").executes(StillCommand::exe).buildChildOf(dispatcher.root)
    }

    fun exe(ctx: CommandContext<ServerCommandSource>): Int {
        val src = ctx.source ?: return 0
        val server = src.server ?: return 0
        val world = src.world ?: return 0

        world.gameRules.get(GameRules.DO_WEATHER_CYCLE).setValue(false, server)
        world.gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).setValue(false, server)
        server.overworld.setWeather(-1, 0, false, false)
        server.overworld.timeOfDay = 6000

        src.message("Weather and daylight cycle disabled!")
        return 1
    }
}
