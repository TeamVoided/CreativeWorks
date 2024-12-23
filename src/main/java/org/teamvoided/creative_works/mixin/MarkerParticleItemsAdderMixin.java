package org.teamvoided.creative_works.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Holder;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public class MarkerParticleItemsAdderMixin {

    @Mutable
    @Final
    @Shadow
    private static Set<Item> MARKER_PARTICLE_ITEMS;

    @Inject(method = "<init>", at = @At("TAIL"))
    void theThingThatMessesWithStuff(ClientPlayNetworkHandler netHandler, ClientWorld.Properties clientWorldProperties, RegistryKey registryKey, Holder dimensionType, int chunkManager, int simulationDistance, Supplier profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
        var set = new HashSet<>(MARKER_PARTICLE_ITEMS);
        set.add(Items.STRUCTURE_VOID);
        MARKER_PARTICLE_ITEMS = set;
    }
}
