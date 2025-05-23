package org.teamvoided.creative_works.comands.worldgen

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.block.Blocks
import net.minecraft.command.CommandBuildContext
import net.minecraft.registry.Holder
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.world.gen.DensityFunction
import net.minecraft.world.gen.StructureWeightSampler
import net.minecraft.world.gen.chunk.AquiferSampler
import net.minecraft.world.gen.chunk.Blender
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings
import net.minecraft.world.gen.chunk.ChunkNoiseSampler
import org.teamvoided.creative_works.c.DebugWidgetRegistry
import org.teamvoided.creative_works.comands.args.RegistryEntryArgumentType.getEntry
import org.teamvoided.creative_works.comands.args.RegistryEntryArgumentType.registryEntryArg
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.message
import kotlin.jvm.optionals.getOrNull

object SplineCommand {

    val limit = DebugWidgetRegistry.addDouble(": Limit", 1.0)
    val posLabel = DebugWidgetRegistry.addString("Noise:", "")
    val breakButton = DebugWidgetRegistry.addButton("Break")
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>, ctx: CommandBuildContext) {
        val root = literal("spline").buildChildOf(dispatcher.root)

        registryEntryArg("id", RegistryKeys.DENSITY_FUNCTION).executes {
            exe(it, getEntry(it, "id", RegistryKeys.DENSITY_FUNCTION))
        }.buildChildOf(root)
    }

    fun exe(ctx: CommandContext<ServerCommandSource>, entry: Holder.Reference<DensityFunction>): Int {
        val src = ctx.source ?: return 0
        val world = src.world ?: return 0

        val denseFn = entry.value()
        base@
        for (chunkX in -1..1) {
            for (chunkZ in -1..1) {
                val chunk = world.getChunk(chunkX, chunkZ)
                val set = ChunkNoiseSampler.create(
                    chunk,
                    world.chunkManager.randomState,
                    StructureWeightSampler.createSampler(world.structureManager, chunk.getPos()),
                    world.registryManager.get(RegistryKeys.CHUNK_GENERATOR_SETTINGS)
                        .getOrThrow(ChunkGeneratorSettings.OVERWORLD),
                    { x, z, t -> AquiferSampler.FluidStatus(0, Blocks.AIR.defaultState) },
                    Blender.empty()
                )
                for (x in 0..16) {
                    for (y in world.dimension.minY..(128)) {
                        for (z in 0..16) {
                            val pos = chunk.pos.getBlockPos(x, y, z)
                            world.chunkManager.chunkGenerator
                            val value = denseFn.compute(set)
                            posLabel.set(pos.toString())
                            if (breakButton.get()) break@base
                            val state = if (value > 0) colorLis[0] else glassList[0]
                            world.setBlockState(pos, state.defaultState)
                        }
                    }
                }
            }
        }
        src.message("${entry.key.getOrNull()?.value?.path}")
        return Command.SINGLE_SUCCESS
    }

    val colorLis = listOf(
        Blocks.WHITE_CONCRETE,
        Blocks.LIGHT_GRAY_CONCRETE,
        Blocks.GRAY_CONCRETE,
        Blocks.BLACK_CONCRETE,
    )
    val glassList = listOf(
        Blocks.WHITE_STAINED_GLASS,
        Blocks.LIGHT_GRAY_STAINED_GLASS,
        Blocks.GRAY_STAINED_GLASS,
        Blocks.BLACK_STAINED_GLASS,
    )
}
