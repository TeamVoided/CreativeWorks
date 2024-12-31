package org.teamvoided.creative_works.comands

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.JsonOps
import net.fabricmc.fabric.api.event.registry.DynamicRegistries.getDynamicRegistries
import net.minecraft.advancement.Advancement
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.loot.LootTable
import net.minecraft.recipe.Recipe
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryLoader.DIMENSION_REGISTRIES
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.WorldSavePath.ROOT
import org.teamvoided.creative_works.CreativeWorks.SECONDARY_COLOR
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.getEntry
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.getRegistry
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.regEntryArg
import org.teamvoided.creative_works.comands.args.RegistryArgumentType.registryArg
import org.teamvoided.creative_works.comands.misc.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.*
import java.io.File

object RegDumpCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("regdump").buildChildOf(dispatcher.root)
        val registry = registryArg().executes { regdump(it, getRegistry(it)) }.buildChildOf(root)
        regEntryArg().executes { regdump(it, getRegistry(it), getEntry(it)) }.buildChildOf(registry)


        val extra = literal("regdump_extra").buildChildOf(dispatcher.root)

        argument("extra", IdentifierArgumentType.identifier())
            .suggests { ctx, builder ->
                val list = mutableListOf(RegistryKeys.LOOT_TABLE, RegistryKeys.ADVANCEMENT, RegistryKeys.RECIPE)
                    .map { it.value.toString() }
                builder.listSuggestions(list)

            }
            .executes { painAndSuffering(it, IdentifierArgumentType.getIdentifier(it, "extra")) }.buildChildOf(extra)
    }

    val gson = GsonBuilder().setPrettyPrinting().create()

    val REG_LIST = (getDynamicRegistries() + DIMENSION_REGISTRIES).associate { it.key to it.elementCodec }

    fun regdump(
        ctx: CommandContext<ServerCommandSource>, registry: Registry<out Any>, entryId: Identifier? = null
    ): Int {
        val src = ctx.source ?: return 0
        val world = src.world ?: return 0
        val dynReg = world.registryManager ?: return 0
        val codec = (REG_LIST[registry.key]) as Codec<Any>?
        val ops = dynReg.createSerializationContext(JsonOps.INSTANCE)
        val id = registry.key.value
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
            registry.entries.forEach { obj.add(it.key.value.toString()) }
            with(world.dumpFolder(id.fileFormat()).resolve("${id.fileFormat()}.json")) {
                parentFile.mkdirs()
                createNewFile()
                writeText(gson.toJson(obj))
                src.openMessage(" Registry dumped to file: ${this.name}", this.toString())
            }
            return 1
        }
        val list = registry.entries.associate { it.key.value to codec.encodeStart(ops, it.value) }
        src.dumpResources(world, id, list)

        return 1
    }

    fun painAndSuffering(
        ctx: CommandContext<ServerCommandSource>,
        extra: Identifier,
        entryId: Identifier? = null
    ): Int {
        val src = ctx.source ?: return 0
        val world = src.world ?: return 0
        val dynReg = world.registryManager ?: return 0
        val server = world.server

        val ops = dynReg.createSerializationContext(JsonOps.INSTANCE)
        val resource = when (extra) {
            RegistryKeys.LOOT_TABLE.value -> {
                server.method_58576().registryManager.get(RegistryKeys.LOOT_TABLE).entries
                    .associate { it.key.value to LootTable.field_50021.encodeStart(ops, it.value) }
            }

            RegistryKeys.ADVANCEMENT.value ->
                server.advancementLoader.advancements.associate { it.id to Advancement.CODEC.encodeStart(ops, it.data) }

            RegistryKeys.RECIPE.value -> server.recipeManager.recipes
                .associate { it.id to Recipe.CODEC.encodeStart(ops, it.value) }

            else -> {
                src.error("Unknown extra $extra")
                return 0
            }
        }
        src.dumpResources(world, extra, resource)


        return 1
    }

    fun ServerCommandSource.dumpResources(
        world: ServerWorld, name: Identifier, list: Map<Identifier, DataResult<JsonElement>>, toFile: Boolean = true
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
                this.sendSystemMessage(Text.literal(" $output").styled { s -> s.withColor(SECONDARY_COLOR) })
            }
            if (toFile) {
                file.parentFile.mkdirs()
                file.createNewFile()
                file.writeText(output)
            }
        }
    }

    fun ServerWorld.dumpFolder(name: String, deleteOld: Boolean = true): File {
        val server = this.server
        val folder = server.getSavePath(ROOT).toFile().resolve("dump")
        if (folder.isFile || !folder.exists()) folder.mkdirs()
        val regFolder = folder.resolve(name)
        if (deleteOld && regFolder.exists() && regFolder.isDirectory) regFolder.deleteRecursively()
        regFolder.mkdirs()

        return regFolder
    }

    fun Identifier?.fileFormat(): String = this?.toString()?.replace(":", "-") ?: "null"

}

