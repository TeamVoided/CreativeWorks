package org.teamvoided.creative_works.world.gen

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.RegistryOps
import net.minecraft.world.level.StructureManager
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.util.Mth
import net.minecraft.server.level.WorldGenRegion
import net.minecraft.world.level.LevelHeightAccessor
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.BiomeManager
import net.minecraft.world.level.biome.FixedBiomeSource
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.RandomState
import net.minecraft.world.level.levelgen.blending.Blender
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.NoiseColumn
import java.util.concurrent.CompletableFuture

class FilteredDebugChunkGenerator(biome: Holder.Reference<Biome>) : ChunkGenerator(FixedBiomeSource(biome)) {

    init {
        BLOCK_STATES = BuiltInRegistries.BLOCK.toList().filter { BuiltInRegistries.BLOCK.getKey(it).namespace == MOD_ID }
            .flatMap { block: Block -> block.stateDefinition.possibleStates.stream().toList() }


        X_SIDE_LENGTH = Mth.ceil(Mth.sqrt(BLOCK_STATES.size.toFloat()))
        Z_SIDE_LENGTH = Mth.ceil(BLOCK_STATES.size.toFloat() / X_SIDE_LENGTH.toFloat())
    }

    companion object {
        val CODEC: MapCodec<FilteredDebugChunkGenerator> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(RegistryOps.retrieveElement(Biomes.THE_VOID)).apply(instance, ::FilteredDebugChunkGenerator)
        }

        val MOD_ID = "cinderscapes"
        var BLOCK_STATES = listOf(Blocks.NETHERITE_BLOCK.defaultBlockState())

        var X_SIDE_LENGTH: Int = Mth.ceil(Mth.sqrt(BLOCK_STATES.size.toFloat()))
        var Z_SIDE_LENGTH: Int = Mth.ceil(BLOCK_STATES.size.toFloat() / X_SIDE_LENGTH.toFloat())
        val AIR = Blocks.AIR.defaultBlockState()
        val BARRIER = Blocks.BARRIER.defaultBlockState()
        const val BLOCK_MARGIN: Int = 2
        const val HEIGHT: Int = 70
        const val BARRIER_HEIGHT: Int = 60
    }

    override fun codec(): MapCodec<out ChunkGenerator?> = CODEC
    override fun buildSurface(
        region: WorldGenRegion, structureManager: StructureManager, randomState: RandomState, chunk: ChunkAccess
    ) = Unit

    override fun applyBiomeDecoration(world: WorldGenLevel, chunk: ChunkAccess, structureManager: StructureManager?) {
        val mutable = BlockPos.MutableBlockPos()
        for (k in 0..15) {
            for (l in 0..15) {
                val m = SectionPos.sectionToBlockCoord(chunk.pos.x, k)
                val n = SectionPos.sectionToBlockCoord(chunk.pos.z, l)
                world.setBlock(mutable.set(m, BARRIER_HEIGHT, n), BARRIER, Block.UPDATE_CLIENTS)
                world.setBlock(mutable.set(m, HEIGHT, n), getBlockState(m, n), Block.UPDATE_CLIENTS)
            }
        }
    }

    override fun fillFromNoise(
        blender: Blender, randomState: RandomState, structureManager: StructureManager, chunk: ChunkAccess
    ) = CompletableFuture.completedFuture(chunk)

    override fun getBaseHeight(
        x: Int, z: Int, heightmap: Heightmap.Types, world: LevelHeightAccessor, randomState: RandomState
    ) = 0

    override fun getBaseColumn(x: Int, z: Int, world: LevelHeightAccessor, randomState: RandomState) =
        NoiseColumn(0, arrayOfNulls(0))

    override fun addDebugScreenInfo(lines: List<String>, randomState: RandomState, pos: BlockPos) = Unit

    fun getBlockState(xI: Int, zI: Int): BlockState {
        var x = xI
        var z = zI
        var blockState = AIR
        if (x > 0 && z > 0 && x % BLOCK_MARGIN != 0 && z % BLOCK_MARGIN != 0) {
            x /= BLOCK_MARGIN
            z /= BLOCK_MARGIN
            if (x <= X_SIDE_LENGTH && z <= Z_SIDE_LENGTH) {
                val i = Mth.abs(x * X_SIDE_LENGTH + z)
                if (i < BLOCK_STATES.size) blockState = BLOCK_STATES[i]
            }
        }

        return blockState
    }

    override fun applyCarvers(
        chunkRegion: WorldGenRegion, seed: Long, randomState: RandomState, biomeAccess: BiomeManager,
        structureManager: StructureManager, chunk: ChunkAccess, generationStep: GenerationStep.Carving
    ) = Unit

    override fun spawnOriginalMobs(region: WorldGenRegion) = Unit
    override fun getMinY(): Int = 0
    override fun getGenDepth(): Int = 384
    override fun getSeaLevel(): Int = 63
}
