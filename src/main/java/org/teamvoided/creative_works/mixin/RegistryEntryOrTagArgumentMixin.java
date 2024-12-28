package org.teamvoided.creative_works.mixin;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.RegistryEntryOrTagArgument;
import net.minecraft.registry.HolderLookup;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teamvoided.creative_works.comands.misc.ImprovedLookup;

import java.util.concurrent.CompletableFuture;

@Debug(export = true)
@Mixin(RegistryEntryOrTagArgument.class)
public class RegistryEntryOrTagArgumentMixin<T> {
    @Final
    @Shadow
    private HolderLookup<T> lookup;

    @Inject(method = "listSuggestions", at = @At("RETURN"), cancellable = true)
    private void improvedSuggestions(CommandContext<T> commandContext, SuggestionsBuilder suggestionsBuilder, CallbackInfoReturnable<CompletableFuture<Suggestions>> cir) {
        cir.setReturnValue(ImprovedLookup.listSuggestions(lookup, commandContext, suggestionsBuilder));
    }
}
