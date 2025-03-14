package org.teamvoided.creative_works.init

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import org.teamvoided.creative_works.comands.*
import org.teamvoided.creative_works.comands.misc.PacketCommand.createIdPacket
import org.teamvoided.creative_works.network.CWNet.CLEAR_PARTICLES
import org.teamvoided.creative_works.network.CWNet.CW_TEST
import org.teamvoided.creative_works.network.CWNet.IMGUI_DEBUG

object CWCommands {
    fun init() = CommandRegistrationCallback.EVENT.register { dispatcher, ctx, env ->
        TagDumpCommand.init(dispatcher)
        PotionCommand.init(dispatcher)
        StructureCommand.init(dispatcher)
        TrimCommand.init(dispatcher)
        StillCommand.init(dispatcher)
        KillItemCommand.init(dispatcher, ctx)
        RenameCommand.init(dispatcher)
        FindTagsCommand.init(dispatcher)
        HandCommand.init(dispatcher, ctx)
        HealthCommand.init(dispatcher)
        GearCommand.init(dispatcher)
        RegDumpCommand.init(dispatcher)

        dispatcher.createIdPacket("clear_particles", CLEAR_PARTICLES)
        dispatcher.createIdPacket("cw_test", CW_TEST)
        dispatcher.createIdPacket("imgui_debug", IMGUI_DEBUG)
    }
}
