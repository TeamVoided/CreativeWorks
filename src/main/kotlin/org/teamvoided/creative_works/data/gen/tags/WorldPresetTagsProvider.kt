package org.teamvoided.creative_works.data.gen.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.client.world.GeneratorType
import net.minecraft.item.Items
import net.minecraft.registry.HolderLookup
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.WorldPresetTags
import org.teamvoided.creative_works.data.tags.CWItemTags
import org.teamvoided.creative_works.init.CWWorldTypes
import java.util.concurrent.CompletableFuture

class WorldPresetTagsProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider<GeneratorType>(o, RegistryKeys.GENERATOR_TYPE, r) {
    override fun configure(arg: HolderLookup.Provider) {
        getOrCreateTagBuilder(WorldPresetTags.EXTENDED)
            .add(CWWorldTypes.DEBUG_FILTERED_BLOCK_STATES)
    }
}
