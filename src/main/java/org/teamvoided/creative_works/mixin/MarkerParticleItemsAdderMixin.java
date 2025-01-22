package org.teamvoided.creative_works.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static org.teamvoided.creative_works.data.tags.CWItemTags.HAVE_BLOCK_PARTICLE;

@Mixin(ClientWorld.class)
public class MarkerParticleItemsAdderMixin {
    @ModifyExpressionValue(method = "getMarkerParticleTarget", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    boolean x(boolean original, @Local ItemStack stack) {
        return stack.isIn(HAVE_BLOCK_PARTICLE);
    }
}
