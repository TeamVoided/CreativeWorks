package org.teamvoided.creative_works.init

import net.minecraft.client.world.GeneratorType
import net.minecraft.registry.Registries.CHUNK_GENERATOR
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import org.teamvoided.creative_works.CreativeWorks.id
import org.teamvoided.creative_works.world.gen.FilteredDebugChunkGenerator

object CWWorldTypes {
    val DEBUG_FILTERED_BLOCK_STATES = key("debug_filtered_block_states")

    fun init() {
        Registry.register(CHUNK_GENERATOR, id("filtered_debug"), FilteredDebugChunkGenerator.CODEC)
    }

    fun key(path: String): RegistryKey<GeneratorType> = RegistryKey.of(RegistryKeys.GENERATOR_TYPE, id(path))
}