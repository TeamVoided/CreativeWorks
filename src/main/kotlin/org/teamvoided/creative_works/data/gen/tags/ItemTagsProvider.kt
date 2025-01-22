package org.teamvoided.creative_works.data.gen.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.item.Items
import net.minecraft.registry.HolderLookup
import org.teamvoided.creative_works.data.tags.CWItemTags
import java.util.concurrent.CompletableFuture

class ItemTagsProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>,
//    blockTags: BlockTagsProvider
) :
    FabricTagProvider.ItemTagProvider(output, registriesFuture) {
    override fun configure(arg: HolderLookup.Provider) {
        getOrCreateTagBuilder(CWItemTags.HAVE_BLOCK_PARTICLE)
            .add(Items.BARRIER)
            .add(Items.STRUCTURE_VOID)
    }

}
