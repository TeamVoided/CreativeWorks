package org.teamvoided.creative_works.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teamvoided.creative_works.util.DebugRenderer;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;draw()V"))
    private void renderCustomGuiRenderer(CallbackInfo ci, @Local(ordinal = 0) GuiGraphics gui) {
        DebugRenderer.INSTANCE.render(gui);
    }
}