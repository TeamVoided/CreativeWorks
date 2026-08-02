package org.teamvoided.creative_works.client.init

import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.phys.AABB
import org.teamvoided.creative_works.CreativeWorksClient.clientConfig
import org.teamvoided.creative_works.client.utils.loopParticles
import org.teamvoided.creative_works.client.utils.position


object CWRenderers {

    fun debugRenderers(ctx: WorldRenderContext) {
        val partialTick = ctx.tickCounter().getGameTimeDeltaPartialTick(false)
        val frustum = ctx.frustum()!!
        val poseStack = ctx.matrixStack() ?: PoseStack()
        val camPos = ctx.camera().position
        val bufferSource = ctx.consumers()!!
        val lineConsumer = bufferSource.getBuffer(RenderType.LINES)

        if (clientConfig.particleHitboxes.shouldRender()) loopParticles { type, particle ->
            var bb = particle.boundingBox
            if (!bb.isInfinite() && frustum.isVisible(bb)) {
                val bbX = (bb.minX + bb.maxX) / 2
                val bbY = (bb.minY + bb.maxY) / 2
                val bbZ = (bb.minZ + bb.maxZ) / 2
                val height = bb.maxY - bb.minY
                bb = bb.move(-bbX, -bbY, -bbZ)

                /*    poseStack.pushPose()
                    poseStack.translate(bbX - camPos.x(), bbY - camPos.y(), bbZ - camPos.z())

                    LevelRenderer.renderLineBox(poseStack, lineConsumer, bb, 1f, 0f, 1f, 1f)
                    poseStack.popPose()*/

                poseStack.pushPose()
                val pos = particle.position(partialTick).subtract(camPos)
                poseStack.translate(pos.x, pos.y + (height / 2), pos.z)

                LevelRenderer.renderLineBox(poseStack, lineConsumer, bb, 1f, 1f, 1f, 1f)
                poseStack.popPose()
            }
        }

    }

    fun AABB.isInfinite(): Boolean {
        return minX.isInfinite()
                && minY.isInfinite()
                && minZ.isInfinite()
                && maxX.isInfinite()
                && maxY.isInfinite()
                && maxZ.isInfinite()
    }


}