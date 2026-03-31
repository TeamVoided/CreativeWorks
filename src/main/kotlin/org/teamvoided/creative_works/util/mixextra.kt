package org.teamvoided.creative_works.util

import net.minecraft.client.particle.ParticleEngine
import org.teamvoided.creative_works.mixin.ParticleEngineAccessor

fun ParticleEngine.clearParticles() = (this as ParticleEngineAccessor).invokeClearParticles()
