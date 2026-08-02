package org.teamvoided.creative_works.client.init

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.Minecraft
import org.teamvoided.creative_works.client.init.CWRenderers.debugRenderers

object CWClientEvents {

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register(::clientTick)
        ItemTooltipCallback.EVENT.register(TooltipExtensions::appendTooltip)
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(::debugRenderers)
    }

    fun clientTick(client: Minecraft) {
        CWKeyMappings.tick(client)
    }
}