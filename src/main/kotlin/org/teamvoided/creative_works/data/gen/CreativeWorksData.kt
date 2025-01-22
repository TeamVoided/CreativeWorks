package org.teamvoided.creative_works.data.gen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.registry.RegistrySetBuilder
import org.teamvoided.creative_works.data.gen.tags.ItemTagsProvider

object CreativeWorksData  : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(gen: FabricDataGenerator) {
        val pack = gen.createPack()
//        val blockTags = pack.addProvider(::BlockTagsProvider)
        pack.addProvider { o, r -> ItemTagsProvider(o, r) }
    }

    override fun buildRegistry(gen: RegistrySetBuilder) {
//        gen.add(RegistryKeys.NOISE_PARAMETERS, NoiseCreator::bootstrap)
    }
}