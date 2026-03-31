package org.teamvoided.creative_works.data.tags

import net.minecraft.world.item.Item
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import org.teamvoided.creative_works.CreativeWorks.id

object CWItemTags {
    @JvmField
    val HAS_BLOCK_PARTICLE = create("has_block_particle")


    fun create(path: String): TagKey<Item> = TagKey.create(Registries.ITEM, id(path))
}