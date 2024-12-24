package org.teamvoided.creative_works.init

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import org.teamvoided.creative_works.comands.PotionCommand
import org.teamvoided.creative_works.comands.StructureCommand
import org.teamvoided.creative_works.comands.TagDumpCommand
import org.teamvoided.creative_works.comands.TrimCommand

object CWCommands {
    fun init() = CommandRegistrationCallback.EVENT.register { dispatcher, c, env ->
        TagDumpCommand.init(dispatcher, c)
        PotionCommand.init(dispatcher)
        StructureCommand.init(dispatcher)

        TrimCommand.init(dispatcher)
    }
}
