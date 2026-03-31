package org.teamvoided.creative_works.comands.registry

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.JsonOps
import net.fabricmc.fabric.api.event.registry.DynamicRegistries.getDynamicRegistries
import net.minecraft.advancements.Advancement
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.IdentifierArgument
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.RegistryDataLoader.DIMENSION_REGISTRIES
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.level.storage.LevelResource.ROOT
import net.minecraft.world.level.storage.loot.LootTable
import org.teamvoided.creative_works.CreativeWorks.SECONDARY_COLOR
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.getEntry
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.getRegistry
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.regEntryArg
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.registryArg
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.*
import java.io.File

object RegDumpCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("regdump").executes(RegDumpCommand::dumpAll).buildChildOf(dispatcher.root)
        val registry = registryArg().executes { regdump(it, getRegistry(it)) }.buildChildOf(root)
        regEntryArg().executes { regdump(it, getRegistry(it), getEntry(it)) }.buildChildOf(registry)


        val extra = literal("regdump_extra").buildChildOf(dispatcher.root)

        argument("extra", IdentifierArgument.id())
            .suggests { ctx, builder ->
                val list = mutableListOf(Registries.LOOT_TABLE, Registries.ADVANCEMENT, Registries.RECIPE)
                    .map { it.identifier().toString() }
                builder.listSuggestions(list)

            }
            .executes { painAndSuffering(it, IdentifierArgument.getId(it, "extra")) }.buildChildOf(extra)
    }

    val gson = GsonBuilder().setPrettyPrinting().create()
    val REG_LIST = (getDynamicRegistries() + DIMENSION_REGISTRIES).associate { it.key to it.elementCodec }

    fun dumpAll(ctx: CommandContext<CommandSourceStack>): Int {
        val src = ctx.source ?: return 0
        val dynReg = src.level?.registryAccess() ?: return 0
        dynReg.registries().forEach { regdump(ctx, it.value, null, true) }
        return 1
    }

    fun regdump(
        ctx: CommandContext<CommandSourceStack>, registry: Registry<out Any>, entryId: Identifier? = null,
        silent: Boolean = false,
    ): Int {
        val src = ctx.source ?: return 0
        val world = src.level ?: return 0
        val dynReg = world.registryAccess() ?: return 0
        val codec = (REG_LIST[registry.key()]) as Codec<Any>?
        val ops = dynReg.createSerializationContext(JsonOps.INSTANCE)
        val id = registry.key().identifier()
        src.message("Registry $id ")

        if (entryId != null) {
            val entry = registry.get(entryId)
            codec?.encodeStart(ops, entry)
                ?.ifError { src.error(" Error while encoding $it") }
                ?.map { gson.toJson(it) }
                ?.ifSuccess { src.copyMessage(it, it, entryId.toString()) }
                ?: src.copyMessage(" Entry: $entryId ", entryId.toString())
            return 1
        }
        if (codec == null) {
            val obj = JsonArray()
            registry.entrySet().forEach { obj.add(it.key.identifier().toString()) }
            with(world.dumpFolder(id.namespace, false).resolve("${id.fileFormatOLD()}.json")) {
                parentFile.mkdirs()
                createNewFile()
                writeText(gson.toJson(obj))
                src.openMessage(" Registry dumped to file: ${this.name}", this.toString())
            }
            return 1
        }
        val list = registry.entrySet().associate { it.key.identifier() to codec.encodeStart(ops, it.value) }
        src.dumpResources(world, id, list, true, silent)

        return 1
    }

    fun painAndSuffering(
        ctx: CommandContext<CommandSourceStack>,
        extra: Identifier,
        entryId: Identifier? = null,
    ): Int {
        val src = ctx.source ?: return 0
        val world = src.level ?: return 0
        val dynReg = world.registryAccess() ?: return 0
        val server = world.server

        val ops = dynReg.createSerializationContext(JsonOps.INSTANCE)
        val resource = when (extra) {
            Registries.LOOT_TABLE.identifier() -> {
                server.reloadableRegistries().lookup().lookupOrThrow(Registries.LOOT_TABLE).listElements().toList()
                    .associate { it.key().identifier() to LootTable.DIRECT_CODEC.encodeStart(ops, it.value()) }
            }

            Registries.ADVANCEMENT.identifier() ->
                server.advancements.allAdvancements.associate { it.id to Advancement.CODEC.encodeStart(ops, it.value) }

            Registries.RECIPE.identifier() -> server.recipeManager.recipes
                .associate { it.id.identifier() to Recipe.CODEC.encodeStart(ops, it.value) }

            else -> {
                src.error("Unknown extra $extra")
                return 0
            }
        }
        src.dumpResources(world, extra, resource)


        return 1
    }

    fun CommandSourceStack.dumpResources(
        world: ServerLevel,
        name: Identifier,
        list: Map<Identifier, DataResult<JsonElement>>,
        toFile: Boolean = true,
        silent: Boolean = false,
    ) {
        val folder = world.dumpFolder(name.fileFormat(), !toFile)
        list.forEach { (name, data) ->
            val file = folder.resolve("${name.fileFormat()}.json")

            var output = ""
            data.ifError {
                output = it.toString()
                this.error(" Error while encoding $output")
            }.ifSuccess {
                output = gson.toJson(it)
                if (!silent) this.sendSystemMessage(
                    Component.literal(" $output").withStyle { s -> s.withColor(SECONDARY_COLOR) })
            }
            if (toFile) {
                file.parentFile.mkdirs()
                file.createNewFile()
                file.writeText(output)
            }
        }
    }

    fun ServerLevel.dumpFolder(name: String, deleteOld: Boolean = true): File {
        val server = this.server
        val folder = server.getWorldPath(ROOT).toFile().resolve("dump")
        if (folder.isFile || !folder.exists()) folder.mkdirs()
        val regFolder = folder.resolve(name)
        if (deleteOld && regFolder.exists() && regFolder.isDirectory) regFolder.deleteRecursively()
        regFolder.mkdirs()

        return regFolder
    }

    fun Identifier?.fileFormat(): String = this?.toString()?.replace(":", "/") ?: "null"
    fun Identifier?.fileFormatOLD(): String =
        this?.toString()?.replace("minecraft:", "")?.replace(":", "-") ?: "null"

}

