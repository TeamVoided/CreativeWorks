package org.teamvoided.creative_works.comands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.registry.HolderLookup
import java.util.concurrent.CompletableFuture

object ImprovedLookup {
    @JvmStatic
    fun <S> listSuggestions(
        lookup: HolderLookup<S>, ignored: CommandContext<S>, suggestionsBuilder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        (lookup.streamTagKeys().map { "#${it.id()}" }.toList() +
                lookup.streamElementKeys().map { it.value.toString() }.toList())
            .filter { it.contains(suggestionsBuilder.remainingLowerCase) }
            .forEach(suggestionsBuilder::suggest)
        return suggestionsBuilder.buildFuture()
    }
}
