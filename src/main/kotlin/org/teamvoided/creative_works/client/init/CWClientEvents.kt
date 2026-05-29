package org.teamvoided.creative_works.client.init

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.Minecraft

object CWClientEvents {

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register(::clientTick)
        ItemTooltipCallback.EVENT.register(TooltipExtensions::appendTooltip)
    }

    fun clientTick(client: Minecraft) {
        CWKeyMappings.tick(client)
    }
}