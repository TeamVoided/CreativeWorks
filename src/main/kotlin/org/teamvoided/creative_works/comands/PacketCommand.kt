package org.teamvoided.creative_works.comands

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.packet.payload.CustomPayload
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import org.teamvoided.creative_works.util.buildChildOf

object PacketCommand {
    fun create(dispatcher: CommandDispatcher<ServerCommandSource>, name: String, packet: CustomPayload) {
        literal(name).executes cmd@{
            val player = it.source?.player ?: return@cmd 0
            ServerPlayNetworking.send(player, packet)
            return@cmd 1
        }.buildChildOf(dispatcher.root)
    }
}
