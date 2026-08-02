package org.teamvoided.creative_works.mixin.client;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface ParticleAccessor {
    @Accessor("x")
    double cw_x();

    @Accessor("y")
    double cw_y();

    @Accessor("z")
    double cw_z();

    @Accessor("xo")
    double cw_xo();

    @Accessor("yo")
    double cw_yo();

    @Accessor("zo")
    double cw_zo();
}
