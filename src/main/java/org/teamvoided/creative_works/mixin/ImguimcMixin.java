package org.teamvoided.creative_works.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.breadloaf.imguimc.Imguimc;

@Mixin(value = Imguimc.class, remap = false)
public class ImguimcMixin {
    @Inject(method = "onInitializeClient", at = @At("HEAD"), cancellable = true, remap = false)
    void cancelDefaultDebugRenderer(CallbackInfo ci) {
        ci.cancel();
    }
}
