package org.teamvoided.creative_works.network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import org.teamvoided.creative_works.CreativeWorks.id
import org.teamvoided.creative_works.CreativeWorks.log
import org.teamvoided.creative_works.util.clearParticles

object CWNet {
    const val CLEAR_PARTICLES = 0
    const val CW_TEST = 1
    const val IMGUI_DEBUG = 2
    fun Int.pack() = ClientEventPacket(this)

    fun init() {
        PayloadTypeRegistry.playS2C().register(ClientEventPacket.ID, ClientEventPacket.CODEC)
        PayloadTypeRegistry.playS2C().register(OpenFileMessagePacket.ID, OpenFileMessagePacket.CODEC)
    }

    fun clientInit() {
        ClientPlayNetworking.registerGlobalReceiver(ClientEventPacket.ID) { packet, c ->
            when (packet.id) {
                CLEAR_PARTICLES -> Minecraft.getInstance().particleEngine.clearParticles()
                CW_TEST -> runTests(c)
                IMGUI_DEBUG -> imguiDebug()
                else -> log.info("Unknown event id [{}]", packet.id)
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(OpenFileMessagePacket.ID) { packet, c ->
            packet.text?.let{ c.player().sendSystemMessage(it) }
        }
    }

    data class ClientEventPacket(val id: Int = 0) : CustomPacketPayload {
        private constructor(buffer: FriendlyByteBuf) : this(buffer.readInt())

        override fun type() = ID

        companion object {
            val CODEC: StreamCodec<FriendlyByteBuf, ClientEventPacket> =
                CustomPacketPayload.codec({ packet, buf -> buf.writeInt(packet.id) }, ::ClientEventPacket)
            val ID = CustomPacketPayload.Type<ClientEventPacket>(id("client_event"))
        }
    }

    data class OpenFileMessagePacket(val text: Component? = null) : CustomPacketPayload {
        private constructor(buffer: RegistryFriendlyByteBuf) :
                this(FriendlyByteBuf.readNullable(buffer, ComponentSerialization.TRUSTED_STREAM_CODEC))

        override fun type() = ID

        companion object {
            val CODEC: StreamCodec<RegistryFriendlyByteBuf, OpenFileMessagePacket> = CustomPacketPayload.codec({ packet, buf ->
                RegistryFriendlyByteBuf.writeNullable(buf, packet.text, ComponentSerialization.TRUSTED_STREAM_CODEC)
            }, ::OpenFileMessagePacket)
            val ID = CustomPacketPayload.Type<OpenFileMessagePacket>(id("open_file_message"))
        }
    }
}