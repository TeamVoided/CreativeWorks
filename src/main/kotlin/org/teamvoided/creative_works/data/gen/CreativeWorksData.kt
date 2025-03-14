package org.teamvoided.creative_works.data.gen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.HolderLookup
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistrySetBuilder
import org.teamvoided.creative_works.data.gen.prov.GeneratorTypes
import org.teamvoided.creative_works.data.gen.tags.ItemTagsProvider
import org.teamvoided.creative_works.data.gen.tags.WorldPresetTagsProvider
import org.teamvoided.creative_works.data.tags.CWItemTags.HAS_BLOCK_PARTICLE
import java.util.concurrent.CompletableFuture

object CreativeWorksData : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(gen: FabricDataGenerator) {
        val pack = gen.createPack()
        // Data
        pack.addProvider(::DynRegProvider)
//        val blockTags = pack.addProvider(::BlockTagsProvider)
        pack.addProvider { o, r -> ItemTagsProvider(o, r) }
        pack.addProvider(::WorldPresetTagsProvider)
        // Assets
        pack.addProvider(::LangGen)
    }

    override fun buildRegistry(gen: RegistrySetBuilder) {
        gen.add(RegistryKeys.GENERATOR_TYPE, GeneratorTypes::bootstrap)
    }

    class DynRegProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) :
        FabricDynamicRegistryProvider(o, r) {
        override fun getName(): String = "Data Gen"
        override fun configure(reg: HolderLookup.Provider, e: Entries) {
            e.addAll(reg.getLookupOrThrow(RegistryKeys.GENERATOR_TYPE))
        }
    }

    class LangGen(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) : FabricLanguageProvider(o, r) {
        override fun generateTranslations(prov: HolderLookup.Provider, gen: TranslationBuilder) {
            gen.add(HAS_BLOCK_PARTICLE, "Has Block Particles")
        }
    }
}