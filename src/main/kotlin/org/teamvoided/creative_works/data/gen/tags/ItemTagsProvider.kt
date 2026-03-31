package org.teamvoided.creative_works.data.gen.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.world.item.Items
import net.minecraft.core.HolderLookup
import org.teamvoided.creative_works.data.tags.CWItemTags
import java.util.concurrent.CompletableFuture

class ItemTagsProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>,
//    blockTags: BlockTagsProvider
) : FabricTagProvider.ItemTagProvider(output, registriesFuture) {
    override fun addTags(arg: HolderLookup.Provider) {
        getOrCreateTagBuilder(CWItemTags.HAS_BLOCK_PARTICLE)
            .add(Items.BARRIER, Items.STRUCTURE_VOID)
    }
}
