package org.teamvoided.creative_works.comands.args

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.arguments.ResourceLocationArgument.id
import net.minecraft.core.Registry
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.getRegistry
import java.util.concurrent.CompletableFuture

object RegistryArgumentType {
    val REGISTRY = "registry"
    val ENTRY = "entry"
    fun registryArg(name: String = REGISTRY): RequiredArgumentBuilder<CommandSourceStack, Identifier> =
        argument(name, id()).suggests(::listSuggestions)

    fun regEntryArg(name: String = ENTRY, regName: String = REGISTRY) =
        argument(name, id()).suggests { ctx, builder ->
            builder.listSuggestions(getRegistry(ctx, regName).registryKeySet().map { it.location().toString() }.toList())
        }

    fun registryTagArg(name: String = REGISTRY): RequiredArgumentBuilder<CommandSourceStack, Identifier> =
        argument(name, id()).suggests(::listSuggestionsTagsOnly)

    fun regTagEntryArg(name: String = ENTRY, regName: String = REGISTRY) =
        argument(name, id()).suggests { ctx, builder ->
            builder.listSuggestions(getRegistry(ctx, regName).tagNames.map { it.location.toString() }.toList())
        }

    @Throws(CommandSyntaxException::class)
    fun getRegistry(ctx: CommandContext<CommandSourceStack>, name: String = REGISTRY): Registry<out Any> {
        val id = ctx.getArgument(name, Identifier::class.java)
        return ctx.source.level.registryAccess().getRegistry(id)
            ?: throw UNKNOWN_REGISTRY_EXCEPTION.create(id)
    }

    @Throws(CommandSyntaxException::class)
    fun getEntry(ctx: CommandContext<CommandSourceStack>, name: String = ENTRY): Identifier =
        ctx.getArgument(name, Identifier::class.java)

    private fun listSuggestions(
        ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val list = ctx.source.level.registryAccess().registries().map { it.value().key().location().toString() }
        return builder.listSuggestions(list.toList())
    }

    private fun listSuggestionsTagsOnly(
        ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val list = ctx.source.level.registryAccess()
            .registries().map { it.value() }
            .filter { it.tagNames.toList().isNotEmpty() }
            .map { it.key().location().toString() }
        return builder.listSuggestions(list.toList())
    }

    private val UNKNOWN_REGISTRY_EXCEPTION =
        DynamicCommandExceptionType { Component.translatable("Registry %s not found!", it) }
}