package org.teamvoided.creative_works.mixin;

import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {
    @Invoker("clear")
     void invokeClear();
}
