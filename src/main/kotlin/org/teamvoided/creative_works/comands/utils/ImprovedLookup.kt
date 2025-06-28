package org.teamvoided.creative_works.comands.utils

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.registry.HolderLookup
import java.util.concurrent.CompletableFuture

object ImprovedLookup {
    @JvmStatic
    fun <S> listElementsAndTags(
        lookup: HolderLookup<S>, ignored: CommandContext<S>, suggestionsBuilder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val list = lookup.streamTagKeys().map { "#${it.id()}" }.toList() +
                lookup.streamElementKeys().map { it.value.toString() }.toList()
        return suggestionsBuilder.listSuggestions(list)
    }

    fun SuggestionsBuilder.listSuggestions(list: Iterable<String>?, ignoreCaps: Boolean = true)
            : CompletableFuture<Suggestions> {
        val query = this.remainingLowerCase.trim().lowercase()
        list?.filter { filterByQuery(it.trim().lowercase(), query) }?.forEach(this::suggest)
        return this.buildFuture()
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

          /*  println(buildString {
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
