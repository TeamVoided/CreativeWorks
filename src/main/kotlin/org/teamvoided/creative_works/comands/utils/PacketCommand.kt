package org.teamvoided.creative_works.comands.utils

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import org.teamvoided.creative_works.network.CWNet.pack
import org.teamvoided.creative_works.util.buildChildOf

object PacketCommand {
    private fun create(dispatcher: CommandDispatcher<CommandSourceStack>, name: String, packet: CustomPacketPayload) {
        literal(name).executes cmd@{
            val player = it.source?.player ?: return@cmd 0
            ServerPlayNetworking.send(player, packet)
            return@cmd 1
        }.buildChildOf(dispatcher.root)
    }

    fun CommandDispatcher<CommandSourceStack>.createPacket(name: String, packet: CustomPacketPayload) =
        create(this, name, packet)

    fun CommandDispatcher<CommandSourceStack>.createIdPacket(name: String, id: Int) =
        createPacket(name, id.pack())
}
