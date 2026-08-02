package org.teamvoided.creative_works.cfg

import net.minecraft.client.Minecraft

enum class ParticleHitboxState {
    ON, SYNC_WITH_HITBOXES, OFF;

    fun shouldRender(): Boolean {
        return when(this){
            ON -> true
            SYNC_WITH_HITBOXES -> Minecraft.getInstance().entityRenderDispatcher.shouldRenderHitBoxes()
            OFF -> false
        }
    }
}
