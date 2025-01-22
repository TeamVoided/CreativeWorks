package org.teamvoided.creative_works.world.gen

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.registry.Holder
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryOps
import net.minecraft.structure.StructureManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.ChunkRegion
import net.minecraft.world.HeightLimitView
import net.minecraft.world.Heightmap
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.Biomes
import net.minecraft.world.biome.source.BiomeAccess
import net.minecraft.world.biome.source.FixedBiomeSource
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.RandomState
import net.minecraft.world.gen.chunk.Blender
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.chunk.VerticalBlockSample
import java.util.concurrent.CompletableFuture

class FilteredDebugChunkGenerator(biome: Holder.Reference<Biome>) : ChunkGenerator(FixedBiomeSource(biome)) {

    init {
        BLOCK_STATES = Registries.BLOCK.toList().filter { Registries.BLOCK.getId(it).namespace == MOD_ID }
            .flatMap { block: Block -> block.stateManager.states.stream().toList() }


        X_SIDE_LENGTH = MathHelper.ceil(MathHelper.sqrt(BLOCK_STATES.size.toFloat()))
        Z_SIDE_LENGTH = MathHelper.ceil(BLOCK_STATES.size.toFloat() / X_SIDE_LENGTH.toFloat())
        println("Block States : $BLOCK_STATES")
    }

    companion object {
        val CODEC: MapCodec<FilteredDebugChunkGenerator> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(RegistryOps.retrieveElement(Biomes.THE_VOID)).apply(instance, ::FilteredDebugChunkGenerator)
        }

        val MOD_ID = "cinderscapes"
        var BLOCK_STATES = listOf(Blocks.NETHERITE_BLOCK.defaultState)

        var X_SIDE_LENGTH: Int = MathHelper.ceil(MathHelper.sqrt(BLOCK_STATES.size.toFloat()))
        var Z_SIDE_LENGTH: Int = MathHelper.ceil(BLOCK_STATES.size.toFloat() / X_SIDE_LENGTH.toFloat())
        val AIR = Blocks.AIR.defaultState
        val BARRIER = Blocks.BARRIER.defaultState
        const val BLOCK_MARGIN: Int = 2
        const val HEIGHT: Int = 70
        const val BARRIER_HEIGHT: Int = 60
    }

    override fun getCodec(): MapCodec<out ChunkGenerator?> = CODEC
    override fun buildSurface(
        region: ChunkRegion, structureManager: StructureManager, randomState: RandomState, chunk: Chunk
    ) = Unit

    override fun generateFeatures(world: StructureWorldAccess, chunk: Chunk, structureManager: StructureManager?) {
        val mutable = BlockPos.Mutable()
        for (k in 0..15) {
            for (l in 0..15) {
                val m = ChunkSectionPos.getOffsetPos(chunk.pos.x, k)
                val n = ChunkSectionPos.getOffsetPos(chunk.pos.z, l)
                world.setBlockState(mutable.set(m, BARRIER_HEIGHT, n), BARRIER, Block.NOTIFY_LISTENERS)
                world.setBlockState(mutable.set(m, HEIGHT, n), getBlockState(m, n), Block.NOTIFY_LISTENERS)
            }
        }
    }

    override fun populateNoise(
        blender: Blender, randomState: RandomState, structureManager: StructureManager, chunk: Chunk
    ) = CompletableFuture.completedFuture(chunk)

    override fun getHeight(
        x: Int, z: Int, heightmap: Heightmap.Type, world: HeightLimitView, randomState: RandomState
    ) = 0

    override fun getColumnSample(x: Int, z: Int, world: HeightLimitView, randomState: RandomState) =
        VerticalBlockSample(0, arrayOfNulls(0))

    override fun addDebugLines(lines: List<String>, randomState: RandomState, pos: BlockPos) = Unit

    fun getBlockState(xI: Int, zI: Int): BlockState {
        var x = xI
        var z = zI
        var blockState = AIR
        if (x > 0 && z > 0 && x % BLOCK_MARGIN != 0 && z % BLOCK_MARGIN != 0) {
            x /= BLOCK_MARGIN
            z /= BLOCK_MARGIN
            if (x <= X_SIDE_LENGTH && z <= Z_SIDE_LENGTH) {
                val i = MathHelper.abs(x * X_SIDE_LENGTH + z)
                if (i < BLOCK_STATES.size) blockState = BLOCK_STATES[i]
            }
        }

        return blockState
    }

    override fun carve(
        chunkRegion: ChunkRegion, seed: Long, randomState: RandomState, biomeAccess: BiomeAccess,
        structureManager: StructureManager, chunk: Chunk, generationStep: GenerationStep.Carver
    ) = Unit

    override fun populateEntities(region: ChunkRegion) = Unit
    override fun getMinimumY(): Int = 0
    override fun getWorldHeight(): Int = 384
    override fun getSeaLevel(): Int = 63
}
