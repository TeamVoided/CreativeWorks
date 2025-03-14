package org.teamvoided.creative_works.init

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import org.teamvoided.creative_works.comands.*
import org.teamvoided.creative_works.comands.misc.PacketCommand.createIdPacket
import org.teamvoided.creative_works.network.CWNet.CLEAR_PARTICLES
import org.teamvoided.creative_works.network.CWNet.CW_TEST
import org.teamvoided.creative_works.network.CWNet.IMGUI_DEBUG

object CWCommands {
    fun init() = CommandRegistrationCallback.EVENT.register { dispatcher, ctx, env ->
        // Registry
        TagDumpCommand.init(dispatcher)
        FindTagsCommand.init(dispatcher)
        RegDumpCommand.init(dispatcher)

        // World
        StillCommand.init(dispatcher)
        KillItemCommand.init(dispatcher, ctx)
        StructureCommand.init(dispatcher)
        TrimCommand.init(dispatcher)

        //Player
        GearCommand.init(dispatcher)
        HealthCommand.init(dispatcher)
        HandCommand.init(dispatcher, ctx)
        PotionCommand.init(dispatcher)
        RenameCommand.init(dispatcher)
        ClearCooldownCommand.init(dispatcher)


        dispatcher.createIdPacket("clear_particles", CLEAR_PARTICLES)
        dispatcher.createIdPacket("cw_test", CW_TEST)
        dispatcher.createIdPacket("imgui_debug", IMGUI_DEBUG)
    }
}
