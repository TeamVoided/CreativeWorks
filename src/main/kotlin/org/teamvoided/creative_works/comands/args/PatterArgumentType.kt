package org.teamvoided.creative_works.comands.args

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.world.item.armortrim.TrimPattern
import net.minecraft.core.registries.Registries
import net.minecraft.commands.Commands
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import org.teamvoided.creative_works.util.getRegistry
import java.util.concurrent.CompletableFuture

object PatterArgumentType {
    fun patternArg(name: String): RequiredArgumentBuilder<CommandSourceStack, Identifier> {
        return Commands.argument(name, ResourceLocationArgument.id())
            .suggests(PatterArgumentType::listSuggestions)
    }

    @Throws(CommandSyntaxException::class)
    fun getPattern(ctx: CommandContext<CommandSourceStack>, name: String): TrimPattern {
        val id = ctx.getArgument(name, Identifier::class.java)
        return ctx.getRegistry(Registries.TRIM_PATTERN).get(id) ?: throw UNKNOWN_PATTERN_EXCEPTION.create(id)
    }

    private fun listSuggestions(
        commandContext: CommandContext<CommandSourceStack>, suggestionsBuilder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return if (commandContext.source is SharedSuggestionProvider) SharedSuggestionProvider.suggest(
            commandContext.source.level.registryAccess().registryOrThrow(Registries.TRIM_PATTERN).registryKeySet()
                .map { it.identifier().toString() },
            suggestionsBuilder
        ) else Suggestions.empty()
    }

    private val UNKNOWN_PATTERN_EXCEPTION =
        DynamicCommandExceptionType { Component.translatable("Pattern %s not found!", it) }
}