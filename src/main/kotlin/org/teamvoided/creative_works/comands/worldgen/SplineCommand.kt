package org.teamvoided.creative_works.comands.worldgen

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.commands.CommandBuildContext
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.CommandSourceStack
import net.minecraft.world.level.levelgen.DensityFunction
import net.minecraft.world.level.levelgen.Beardifier
import net.minecraft.world.level.levelgen.Aquifer
import net.minecraft.world.level.levelgen.blending.Blender
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.levelgen.NoiseChunk
import org.teamvoided.creative_works.comands.args.RegistryEntryArgumentType.getEntry
import org.teamvoided.creative_works.comands.args.RegistryEntryArgumentType.registryEntryArg
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.message
import kotlin.jvm.optionals.getOrNull

object SplineCommand {

//    val limit = DebugWidgetRegistry.addDouble(": Limit", 1.0)
//    val posLabel = DebugWidgetRegistry.addString("Noise:", "")
//    val breakButton = DebugWidgetRegistry.addButton("Break")
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>, ctx: CommandBuildContext) {
        val root = literal("spline").buildChildOf(dispatcher.root)

        registryEntryArg("id", Registries.DENSITY_FUNCTION).executes {
            exe(it, getEntry(it, "id", Registries.DENSITY_FUNCTION))
        }.buildChildOf(root)
    }

    fun exe(ctx: CommandContext<CommandSourceStack>, entry: Holder.Reference<DensityFunction>): Int {
        val src = ctx.source ?: return 0
        val world = src.level ?: return 0

        val denseFn = entry.value()
        base@
        for (chunkX in -1..1) {
            for (chunkZ in -1..1) {
                val chunk = world.getChunk(chunkX, chunkZ)
                val set = NoiseChunk.forChunk(
                    chunk,
                    world.chunkSource.randomState(),
                    Beardifier.forStructuresInChunk(world.structureManager(), chunk.getPos()),
                    world.registryAccess().registryOrThrow(Registries.NOISE_SETTINGS)
                        .getOrThrow(NoiseGeneratorSettings.OVERWORLD),
                    { x, z, t -> Aquifer.FluidStatus(0, Blocks.AIR.defaultBlockState()) },
                    Blender.empty()
                )
                for (x in 0..16) {
                    for (y in world.dimensionType().minY..(128)) {
                        for (z in 0..16) {
                            val pos = chunk.pos.getBlockAt(x, y, z)
                            world.chunkSource.generator
                            val value = denseFn.compute(set)
//                            posLabel.set(pos.toString())
//                            if (breakButton.get()) break@base
                            val state = if (value > 0) colorLis[0] else glassList[0]
                            world.setBlockAndUpdate(pos, state.defaultBlockState())
                        }
                    }
                }
            }
        }
        src.message("${entry.unwrapKey().getOrNull()?.identifier()?.path}")
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
