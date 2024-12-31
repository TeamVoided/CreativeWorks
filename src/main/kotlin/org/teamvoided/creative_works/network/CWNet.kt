package org.teamvoided.creative_works.network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.payload.CustomPayload
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
import org.teamvoided.creative_works.CreativeWorks.id
import org.teamvoided.creative_works.CreativeWorks.log
import org.teamvoided.creative_works.util.clearParticles

object CWNet {
    const val CLEAR_PARTICLES = 0
    const val CW_TEST = 1
    fun Int.pack() = ClientEventPacket(this)

    fun init() {
        PayloadTypeRegistry.playS2C().register(ClientEventPacket.ID, ClientEventPacket.CODEC)
        PayloadTypeRegistry.playS2C().register(OpenFileMessagePacket.ID, OpenFileMessagePacket.CODEC)
    }

    fun clientInit() {
        ClientPlayNetworking.registerGlobalReceiver(ClientEventPacket.ID) { packet, c ->
            when (packet.id) {
                CLEAR_PARTICLES -> MinecraftClient.getInstance().particleManager.clearParticles()
                CW_TEST -> runTests(c)
                else -> log.info("Unknown event id [{}]", packet.id)
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(OpenFileMessagePacket.ID) { packet, c ->
            packet.text?.let{ c.player().sendSystemMessage(it) }
        }
    }

    data class ClientEventPacket(val id: Int = 0) : CustomPayload {
        private constructor(buffer: PacketByteBuf) : this(buffer.readInt())

        override fun getId() = ID

        companion object {
            val CODEC: PacketCodec<PacketByteBuf, ClientEventPacket> =
                CustomPayload.create({ packet, buf -> buf.writeInt(packet.id) }, ::ClientEventPacket)
            val ID = CustomPayload.Id<ClientEventPacket>(id("client_event"))
        }
    }

    data class OpenFileMessagePacket(val text: Text? = null) : CustomPayload {
        private constructor(buffer: RegistryByteBuf) :
                this(PacketByteBuf.readNullable(buffer, TextCodecs.REGISTRY_UNLIMITED_TEXT_CODEC))

        override fun getId() = ID

        companion object {
            val CODEC: PacketCodec<RegistryByteBuf, OpenFileMessagePacket> = CustomPayload.create({ packet, buf ->
                RegistryByteBuf.writeNullable(buf, packet.text, TextCodecs.REGISTRY_UNLIMITED_TEXT_CODEC)
            }, ::OpenFileMessagePacket)
            val ID = CustomPayload.Id<OpenFileMessagePacket>(id("open_file_message"))
        }
    }
}