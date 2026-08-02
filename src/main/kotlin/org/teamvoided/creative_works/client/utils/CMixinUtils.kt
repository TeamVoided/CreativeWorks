package org.teamvoided.creative_works.client.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import org.teamvoided.creative_works.mixin.client.ParticleAccessor
import org.teamvoided.creative_works.mixin.client.ParticleEngineAccessor

fun loopParticles(particleConsumer: (ParticleRenderType, Particle) -> Unit) {
    (Minecraft.getInstance().particleEngine as ParticleEngineAccessor).cw_particles().forEach { (type, queue) ->
        queue.forEach { particle ->
            particleConsumer(type, particle)
        }
    }
}

fun Particle.position(tickDelta: Float): Vec3 {
    val old = positionOld()
    val pos = position()
    val x = Mth.lerp(tickDelta.toDouble(), old.x, pos.x)
    val y = Mth.lerp(tickDelta.toDouble(), old.y, pos.y)
    val z = Mth.lerp(tickDelta.toDouble(), old.z, pos.z)

    return Vec3(x, y, z)
}

fun Particle.positionOld(): Vec3 {
    val pa = this as ParticleAccessor
    return Vec3(pa.cw_xo(), pa.cw_yo(), pa.cw_zo())
}

fun Particle.position(): Vec3 {
    val pa = this as ParticleAccessor
    return Vec3(pa.cw_x(), pa.cw_y(), pa.cw_z())
}