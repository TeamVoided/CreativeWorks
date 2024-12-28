package org.teamvoided.creative_works.init

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import org.teamvoided.creative_works.comands.*
import org.teamvoided.creative_works.network.CWNet
import org.teamvoided.creative_works.network.CWNet.pack

object CWCommands {
    fun init() = CommandRegistrationCallback.EVENT.register { dispatcher, c, env ->
        TagDumpCommand.init(dispatcher)
        PotionCommand.init(dispatcher)
        StructureCommand.init(dispatcher)
        TrimCommand.init(dispatcher)
        StillCommand.init(dispatcher)

        PacketCommand.create(dispatcher, "clear_particles", CWNet.CLEAR_PARTICLES.pack())
        PacketCommand.create(dispatcher, "cw_test", CWNet.CLEAR_PARTICLES.pack())

    }
}
