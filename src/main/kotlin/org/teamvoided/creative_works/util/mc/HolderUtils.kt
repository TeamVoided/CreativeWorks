package org.teamvoided.creative_works.util.mc

import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey

fun sortIdentifier(a: ResourceLocation, b: ResourceLocation) = a.path.compareTo(b.path)

fun <T> sortTags(a: TagKey<T>, b: TagKey<T>) = sortIdentifier(a.location, b.location)

fun <T> Holder<T>.getSortedTags(): List<TagKey<T>> {
    return tags()
        .sorted(::sortTags)
        .toList()
}
