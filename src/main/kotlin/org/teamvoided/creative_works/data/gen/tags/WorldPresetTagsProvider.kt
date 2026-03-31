package org.teamvoided.creative_works.data.gen.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.world.level.levelgen.presets.WorldPreset
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.tags.WorldPresetTags
import org.teamvoided.creative_works.init.CWWorldTypes
import java.util.concurrent.CompletableFuture

class WorldPresetTagsProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider<WorldPreset>(o, Registries.WORLD_PRESET, r) {
    override fun addTags(arg: HolderLookup.Provider) {
        tag(WorldPresetTags.EXTENDED)
            .add(CWWorldTypes.DEBUG_FILTERED_BLOCK_STATES)
    }
}
