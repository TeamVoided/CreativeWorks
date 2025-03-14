package org.teamvoided.creative_works.comands.utils

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
        val list = lookup.streamTagKeys().map { "#${it.id()}" }.toList() +
                lookup.streamElementKeys().map { it.value.toString() }.toList()
        return suggestionsBuilder.listSuggestions(list)
    }

    fun SuggestionsBuilder.listSuggestions(list: Iterable<String>?, ignoreCaps: Boolean = true)
            : CompletableFuture<Suggestions> {
        list?.filter { it.lowercase().contains(this.remainingLowerCase) }?.forEach(this::suggest)
        return this.buildFuture()
    }
}
