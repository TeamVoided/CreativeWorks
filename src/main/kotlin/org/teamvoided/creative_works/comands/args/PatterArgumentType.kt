package org.teamvoided.creative_works.comands.args

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.item.trim.ArmorTrimPattern
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.teamvoided.creative_works.util.getRegistry
import java.util.concurrent.CompletableFuture

object PatterArgumentType {
    fun patternArg(name: String): RequiredArgumentBuilder<ServerCommandSource, Identifier> {
        return CommandManager.argument(name, IdentifierArgumentType.identifier())
            .suggests(PatterArgumentType::listSuggestions)
    }

    @Throws(CommandSyntaxException::class)
    fun getPattern(ctx: CommandContext<ServerCommandSource>, name: String): ArmorTrimPattern {
        val id = ctx.getArgument(name, Identifier::class.java)
        return ctx.getRegistry(RegistryKeys.TRIM_PATTERN).get(id) ?: throw UNKNOWN_PATTERN_EXCEPTION.create(id)
    }

    private fun listSuggestions(
        commandContext: CommandContext<ServerCommandSource>, suggestionsBuilder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return if (commandContext.source is CommandSource) CommandSource.suggestMatching(
            commandContext.source.world.registryManager.get(RegistryKeys.TRIM_PATTERN).keys.map { it.value.toString() },
            suggestionsBuilder
        ) else Suggestions.empty()
    }

    private val UNKNOWN_PATTERN_EXCEPTION =
        DynamicCommandExceptionType { Text.translatable("Pattern %s not found!", it) }
}