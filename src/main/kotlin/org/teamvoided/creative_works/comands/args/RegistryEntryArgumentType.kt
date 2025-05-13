package org.teamvoided.creative_works.comands.args

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.argument.IdentifierArgumentType.identifier
import net.minecraft.registry.Holder
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.gen.DensityFunctions
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

object RegistryEntryArgumentType {
    val REGISTRY = "registry"
    val ENTRY = "entry"

    fun <T> registryEntryArg(name: String, registry: RegistryKey<Registry<T>>) =
        argument(name, identifier()).suggests { ctx, builder ->
            builder.listSuggestions(getRegistry(ctx, registry).keys.map { it.value.toString() }.toList())
        }


    @Throws(CommandSyntaxException::class)
    fun <T> getRegistry(ctx: CommandContext<ServerCommandSource>, name: RegistryKey<Registry<T>>): Registry<T> {
        return ctx.source.world.registryManager.getOptional(name).getOrNull()
            ?: throw UNKNOWN_REGISTRY_EXCEPTION.create(name)
    }

    @Throws(CommandSyntaxException::class)
    fun <T> getEntry(
        ctx: CommandContext<ServerCommandSource>, name: String, registry: RegistryKey<Registry<T>>,
    ): Holder.Reference<T> {
        val id = ctx.getArgument(name, Identifier::class.java)
        return getRegistry(ctx, registry).getHolder(id).getOrNull()
            ?: throw UNKNOWN_REGISTRY_ENTRY_EXCEPTION.create(id)
    }

    private fun listSuggestions(
        ctx: CommandContext<ServerCommandSource>, builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val list = ctx.source.world.registryManager.registries().map { it.value().key.value.toString() }
        return builder.listSuggestions(list.toList())
    }

    private fun listSuggestionsTagsOnly(
        ctx: CommandContext<ServerCommandSource>, builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val list = ctx.source.world.registryManager
            .registries().map { it.value() }
            .filter { it.tagKeys.toList().isNotEmpty() }
            .map { it.key.value.toString() }
        return builder.listSuggestions(list.toList())
    }

    private val UNKNOWN_REGISTRY_EXCEPTION =
        DynamicCommandExceptionType { Text.translatable("Registry %s not found!", it) }
    private val UNKNOWN_REGISTRY_ENTRY_EXCEPTION =
        DynamicCommandExceptionType { Text.translatable("Registry entry %s not found!", it) }
}