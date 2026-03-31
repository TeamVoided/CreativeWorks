package org.teamvoided.creative_works.comands.args

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.arguments.ResourceLocationArgument.id
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

object RegistryEntryArgumentType {
    val REGISTRY = "registry"
    val ENTRY = "entry"

    fun <T> registryEntryArg(name: String, registry: ResourceKey<Registry<T>>) =
        argument(name, id()).suggests { ctx, builder ->
            builder.listSuggestions(getRegistry(ctx, registry).registryKeySet().map { it.location().toString() }.toList())
        }


    @Throws(CommandSyntaxException::class)
    fun <T> getRegistry(ctx: CommandContext<CommandSourceStack>, name: ResourceKey<Registry<T>>): Registry<T> {
        return ctx.source.level.registryAccess().registry(name).getOrNull()
            ?: throw UNKNOWN_REGISTRY_EXCEPTION.create(name)
    }

    @Throws(CommandSyntaxException::class)
    fun <T> getEntry(
        ctx: CommandContext<CommandSourceStack>, name: String, registry: ResourceKey<Registry<T>>,
    ): Holder.Reference<T> {
        val id = ctx.getArgument(name, Identifier::class.java)
        return getRegistry(ctx, registry).getHolder(id).getOrNull()
            ?: throw UNKNOWN_REGISTRY_ENTRY_EXCEPTION.create(id)
    }

    private fun listSuggestions(
        ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val list = ctx.source.level.registryAccess().registries().map { it.value().key().location().toString() }
        return builder.listSuggestions(list.toList())
    }

    private fun listSuggestionsTagsOnly(
        ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val list = ctx.source.level.registryAccess()
            .registries().map { it.value() }
            .filter { it.tagNames.toList().isNotEmpty() }
            .map { it.key().location().toString() }
        return builder.listSuggestions(list.toList())
    }

    private val UNKNOWN_REGISTRY_EXCEPTION =
        DynamicCommandExceptionType { Component.translatable("Registry %s not found!", it) }
    private val UNKNOWN_REGISTRY_ENTRY_EXCEPTION =
        DynamicCommandExceptionType { Component.translatable("Registry entry %s not found!", it) }
}