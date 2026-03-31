package org.teamvoided.creative_works.comands.args

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.world.item.armortrim.TrimMaterial
import net.minecraft.core.registries.Registries
import net.minecraft.commands.Commands
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.teamvoided.creative_works.util.getRegistry
import java.util.concurrent.CompletableFuture

object MaterialArgumentType {
    fun materialArg(name: String): RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> {
        return Commands.argument(name, ResourceLocationArgument.id())
            .suggests(MaterialArgumentType::listSuggestions)
    }

    @Throws(CommandSyntaxException::class)
    fun getMaterial(ctx: CommandContext<CommandSourceStack>, name: String): TrimMaterial {
        val id = ctx.getArgument(name, ResourceLocation::class.java)
        return ctx.getRegistry(Registries.TRIM_MATERIAL).get(id) ?: throw UNKNOWN_MATERIAL_EXCEPTION.create(id)
    }

    private fun listSuggestions(
        commandContext: CommandContext<CommandSourceStack>, suggestionsBuilder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return if (commandContext.source is SharedSuggestionProvider) SharedSuggestionProvider.suggest(
            commandContext.source.level.registryAccess().registryOrThrow(Registries.TRIM_MATERIAL).registryKeySet()
                .map { it.location().toString() },
            suggestionsBuilder
        ) else Suggestions.empty()
    }

    private val UNKNOWN_MATERIAL_EXCEPTION =
        DynamicCommandExceptionType { Component.translatable("Material %s not found!", it) }
}