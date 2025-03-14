package org.teamvoided.creative_works.data.tags

import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import org.teamvoided.creative_works.CreativeWorks.id

object CWItemTags {
    @JvmField
    val HAS_BLOCK_PARTICLE = create("has_block_particle")


    fun create(path: String): TagKey<Item> = TagKey.of(RegistryKeys.ITEM, id(path))
}