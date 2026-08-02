package org.teamvoided.creative_works.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static org.teamvoided.creative_works.data.tags.CWItemTags.HAS_BLOCK_PARTICLE;

@Mixin(ClientLevel.class)
public class MarkerParticleItemsAdderMixin {
    @ModifyExpressionValue(method = "getMarkerParticleTarget", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    boolean modifyBlockParticles(boolean original, @Local ItemStack stack) {
        return stack.is(HAS_BLOCK_PARTICLE);
    }
}
