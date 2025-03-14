package org.teamvoided.creative_works.comands.args

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.argument.IdentifierArgumentType.identifier
import net.minecraft.registry.Registry
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.getRegistry
import java.util.concurrent.CompletableFuture

object RegistryArgumentType {
    val REGISTRY = "registry"
    val ENTRY = "entry"
    fun registryArg(name: String = REGISTRY): RequiredArgumentBuilder<ServerCommandSource, Identifier> =
        argument(name, identifier()).suggests(::listSuggestions)

    fun regEntryArg(name: String = ENTRY, regName: String = REGISTRY) =
        argument(name, identifier()).suggests { ctx, builder ->
            builder.listSuggestions(getRegistry(ctx, regName).keys.map { it.value.toString() }.toList())
        }

    fun registryTagArg(name: String = REGISTRY): RequiredArgumentBuilder<ServerCommandSource, Identifier> =
        argument(name, identifier()).suggests(::listSuggestionsTagsOnly)

    fun regTagEntryArg(name: String = ENTRY, regName: String = REGISTRY) =
        argument(name, identifier()).suggests { ctx, builder ->
            builder.listSuggestions(getRegistry(ctx, regName).tagKeys.map { it.id.toString() }.toList())
        }

    @Throws(CommandSyntaxException::class)
    fun getRegistry(ctx: CommandContext<ServerCommandSource>, name: String = REGISTRY): Registry<out Any> {
        val id = ctx.getArgument(name, Identifier::class.java)
        return ctx.source.world.registryManager.getRegistry(id)
            ?: throw UNKNOWN_REGISTRY_EXCEPTION.create(id)
    }

    @Throws(CommandSyntaxException::class)
    fun getEntry(ctx: CommandContext<ServerCommandSource>, name: String = ENTRY): Identifier =
        ctx.getArgument(name, Identifier::class.java)

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