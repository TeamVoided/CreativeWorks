package org.teamvoided.creative_works.init

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import org.teamvoided.creative_works.comands.player.*
import org.teamvoided.creative_works.comands.registry.FindTagsCommand
import org.teamvoided.creative_works.comands.registry.RegDumpCommand
import org.teamvoided.creative_works.comands.registry.TagDumpCommand
import org.teamvoided.creative_works.comands.utils.PacketCommand.createIdPacket
import org.teamvoided.creative_works.comands.world.*
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
        PlaceAllCommand.init(dispatcher)

        //Player
        GearCommand.init(dispatcher)
        HealthCommand.init(dispatcher)
        PotionCommand.init(dispatcher)
        ClearCooldownCommand.init(dispatcher)
        RenameCommand.init(dispatcher)
        HandCommand.init(dispatcher, ctx)
        ApplyCommand.init(dispatcher)
        ResetComponentsCommand.init(dispatcher, ctx)


        dispatcher.createIdPacket("clear_particles", CLEAR_PARTICLES)
        dispatcher.createIdPacket("cw_test", CW_TEST)
        dispatcher.createIdPacket("imgui_debug", IMGUI_DEBUG)
    }
}
