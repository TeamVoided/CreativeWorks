package org.teamvoided.creative_works.mixin;

import net.minecraft.command.CommandSource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.teamvoided.creative_works.comands.utils.ImprovedLookup.filterByQuery;

@Mixin(CommandSource.class)
public interface CommandSourceMixin {

    @Inject(method = "forEachMatching(Ljava/lang/Iterable;Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private static <T> void modifyQueries(Iterable<T> candidates, String query,
                                          Function<T, Identifier> getId, Consumer<T> addToBuilder,
                                          CallbackInfo ci) {
        filterByQuery(candidates, query, "", getId, addToBuilder);
        ci.cancel();
    }

    @Inject(method = "forEachMatching(Ljava/lang/Iterable;Ljava/lang/String;Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private static <T> void modifyQueriesWithPrefix(Iterable<T> candidates,
                                                    String query, String prefix,
                                                    Function<T, Identifier> getId, Consumer<T> addToBuilder,
                                                    CallbackInfo ci) {
        filterByQuery(candidates, query, prefix, getId, addToBuilder);
        ci.cancel();
    }
}
