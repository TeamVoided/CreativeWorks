package org.teamvoided.creative_works.comands.utils

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Function

object ImprovedLookup {
    @JvmStatic
    fun <S> listElementsAndTags(
        lookup: HolderLookup<S>, ignored: CommandContext<S>, suggestionsBuilder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val list = lookup.listTagIds().map { "#${it.location()}" }.toList() +
                lookup.listElementIds().map { it.location().toString() }.toList()
        return suggestionsBuilder.listSuggestions(list)
    }

    fun SuggestionsBuilder.listSuggestions(list: Iterable<String>?)
            : CompletableFuture<Suggestions> {
        val query = this.remainingLowerCase.trim().lowercase()
        list?.filter { filterByQuery(it.trim().lowercase(), query) }?.forEach(this::suggest)
        return this.buildFuture()
    }

    @JvmStatic
    fun <T> filterByQuery(
        candidates: Iterable<T>, query: String, prefix: String,
        getId: Function<T, ResourceLocation>, addToBuilder: Consumer<T>,
    ) {
        for (obj in candidates) {
            val id = prefix + getId.apply(obj).toString()
            if (filterByQuery(id, query)) {
                addToBuilder.accept(obj)
            }
        }
    }

    fun filterByQuery(entry: String, query: String): Boolean {
        if (query.contains(":") && entry.contains(":")) {
            val splitQuery = query.split(":")
            val queryN = splitQuery[0].trim()
            val queryP = splitQuery.drop(1).joinToString("").trim()
            val splitEntry = entry.split(":")
            val entryN = splitEntry[0]
            val entryP = splitEntry.drop(1).joinToString("")

            var namespaceMatches = true
            if (!queryN.isEmpty()) {
                namespaceMatches = entryN.contains(queryN)
            }
            /*println(buildString {
                appendLine()
                appendLine("Entry: \"$entry\"")
                appendLine("\t- \"$entryN\" |:| \"$entryP\"")
                appendLine("Query: \"$query\"")
                appendLine("\t- \"$queryN\" |:| \"$queryP\"")
                appendLine("Namespace match: $namespaceMatches")
                appendLine("Path match: ${entryP.contains(queryP)}")
                })*/
            return namespaceMatches && entryP.contains(queryP)
        }
        return entry.contains(query)
    }
}
