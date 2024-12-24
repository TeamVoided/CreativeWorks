package org.teamvoided.creative_works.util

import net.minecraft.client.particle.ParticleManager
import org.teamvoided.creative_works.mixin.ParticleManagerAccessor

fun ParticleManager.clearParticles() = (this as ParticleManagerAccessor).invokeClear()
