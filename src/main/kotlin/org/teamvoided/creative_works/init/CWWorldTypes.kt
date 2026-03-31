package org.teamvoided.creative_works.init

import net.minecraft.world.level.levelgen.presets.WorldPreset
import net.minecraft.core.registries.BuiltInRegistries.CHUNK_GENERATOR
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import org.teamvoided.creative_works.CreativeWorks.id
import org.teamvoided.creative_works.world.gen.FilteredDebugChunkGenerator

object CWWorldTypes {
    val DEBUG_FILTERED_BLOCK_STATES = key("debug_filtered_block_states")

    fun init() {
        Registry.register(CHUNK_GENERATOR, id("filtered_debug"), FilteredDebugChunkGenerator.CODEC)
    }

    fun key(path: String): ResourceKey<WorldPreset> = ResourceKey.create(Registries.WORLD_PRESET, id(path))
}