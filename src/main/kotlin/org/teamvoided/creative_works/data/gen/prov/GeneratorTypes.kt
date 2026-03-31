package org.teamvoided.creative_works.data.gen.prov

import net.minecraft.world.level.levelgen.presets.WorldPreset
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.core.HolderGetter
import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.chunk.ChunkGenerator
import org.teamvoided.creative_works.init.CWWorldTypes.DEBUG_FILTERED_BLOCK_STATES
import org.teamvoided.creative_works.world.gen.FilteredDebugChunkGenerator

object GeneratorTypes {
    private lateinit var biomes: HolderGetter<Biome>
    private lateinit var dimType: HolderGetter<DimensionType>
    private lateinit var gen: ChunkGenerator

    fun bootstrap(c: BootstrapContext<WorldPreset>) {
        biomes = c.lookup(Registries.BIOME)
        gen = FilteredDebugChunkGenerator(biomes.getOrThrow(Biomes.THE_VOID))
        dimType = c.lookup(Registries.DIMENSION_TYPE)
        val overworld = dimType.getOrThrow(BuiltinDimensionTypes.OVERWORLD)

        c.addDimensionGenerator(DEBUG_FILTERED_BLOCK_STATES, LevelStem(overworld, gen))
    }

    fun BootstrapContext<WorldPreset>.addDimensionGenerator(
        generator: ResourceKey<WorldPreset>, dimension: LevelStem
    ) = this.register(generator, createAllType(dimension))

    fun createAllType(dimension: LevelStem): WorldPreset {
        val nether = dimType.getOrThrow(BuiltinDimensionTypes.NETHER)
        val end = dimType.getOrThrow(BuiltinDimensionTypes.END)
        return WorldPreset(
            mapOf(
                LevelStem.OVERWORLD to dimension,
                LevelStem.NETHER to LevelStem(nether, gen),
                LevelStem.END to LevelStem(end, gen)
            )
        )
    }
}