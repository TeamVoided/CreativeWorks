package org.teamvoided.creative_works.data.gen.prov

import net.minecraft.client.world.GeneratorType
import net.minecraft.registry.BootstrapContext
import net.minecraft.registry.HolderProvider
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.Biomes
import net.minecraft.world.dimension.DimensionOptions
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.dimension.DimensionTypes
import net.minecraft.world.gen.chunk.ChunkGenerator
import org.teamvoided.creative_works.init.CWWorldTypes.DEBUG_FILTERED_BLOCK_STATES
import org.teamvoided.creative_works.world.gen.FilteredDebugChunkGenerator

object GeneratorTypes {
    private lateinit var biomes: HolderProvider<Biome>
    private lateinit var dimType: HolderProvider<DimensionType>
    private lateinit var gen: ChunkGenerator

    fun bootstrap(c: BootstrapContext<GeneratorType>) {
        biomes = c.getRegistryLookup(RegistryKeys.BIOME)
        gen = FilteredDebugChunkGenerator(biomes.getHolderOrThrow(Biomes.THE_VOID))
        dimType = c.getRegistryLookup(RegistryKeys.DIMENSION_TYPE)
        val overworld = dimType.getHolderOrThrow(DimensionTypes.OVERWORLD)

        c.addDimensionGenerator(DEBUG_FILTERED_BLOCK_STATES, DimensionOptions(overworld, gen))
    }

    fun BootstrapContext<GeneratorType>.addDimensionGenerator(
        generator: RegistryKey<GeneratorType>, dimension: DimensionOptions
    ) = this.register(generator, createAllType(dimension))

    fun createAllType(dimension: DimensionOptions): GeneratorType {
        val nether = dimType.getHolderOrThrow(DimensionTypes.THE_NETHER)
        val end = dimType.getHolderOrThrow(DimensionTypes.THE_END)
        return GeneratorType(
            mapOf(
                DimensionOptions.OVERWORLD to dimension,
                DimensionOptions.NETHER to DimensionOptions(nether, gen),
                DimensionOptions.END to DimensionOptions(end, gen)
            )
        )
    }
}