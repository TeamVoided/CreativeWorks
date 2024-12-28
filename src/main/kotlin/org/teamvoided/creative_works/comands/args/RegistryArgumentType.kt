package org.teamvoided.creative_works.comands.args

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.command.argument.IdentifierArgumentType.getIdentifier
import net.minecraft.registry.Registry
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.teamvoided.creative_works.comands.misc.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.getRegistry
import java.util.concurrent.CompletableFuture

object RegistryArgumentType {
    fun registryArg(name: String): RequiredArgumentBuilder<ServerCommandSource, Identifier> {
        return CommandManager.argument(name, IdentifierArgumentType.identifier())
            .suggests(RegistryArgumentType::listSuggestions)
    }
    fun registryTagArg(name: String): RequiredArgumentBuilder<ServerCommandSource, Identifier> {
        return CommandManager.argument(name, IdentifierArgumentType.identifier())
            .suggests(RegistryArgumentType::listSuggestionsTagsOnly)
    }

    @Throws(CommandSyntaxException::class)
    fun getRegistry(ctx: CommandContext<ServerCommandSource>, name: String): Registry<out Any> {
        val id = ctx.getArgument(name, Identifier::class.java)
        return ctx.source.world.registryManager.getRegistry(getIdentifier(ctx, "registry"))
            ?: throw UNKNOWN_REGISTRY_EXCEPTION.create(id)
    }

    private fun listSuggestions(
        ctx: CommandContext<ServerCommandSource>, builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val list = ctx.source.world.registryManager.registries().map { it.value().key.value.toString() }
        return builder.listSuggestions(list.toList())
    }
    private fun listSuggestionsTagsOnly(
        ctx: CommandContext<ServerCommandSource>, builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val list = ctx.source.world.registryManager
            .registries().map { it.value() }
            .filter { it.tagKeys.toList().isNotEmpty() }
            .map { it.key.value.toString() }
        return builder.listSuggestions(list.toList())
    }

    private val UNKNOWN_REGISTRY_EXCEPTION =
        DynamicCommandExceptionType { Text.translatable("Registry %s not found!", it) }
}