package org.teamvoided.creative_works.client.tooltip

import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.decoration.Painting
import net.minecraft.world.item.ItemStack
import org.teamvoided.creative_works.client.init.TooltipExtensions.ENTITY_TYPE_FIELD_CODEC
import org.teamvoided.creative_works.client.init.TooltipExtensions.addAllTags
import org.teamvoided.creative_works.util.mc.getSortedTags
import org.teamvoided.creative_works.util.mc.textWarning

object TooltipTags {
    data class Context(
        val registries: HolderLookup.Provider?,
        val isEgg: Boolean,
    ) {
        fun nbtOps() = registries?.createSerializationContext(NbtOps.INSTANCE)
    }

    fun interface TooltipDataAppender<T : Any> {
        fun append(data: T, ctx: Context, tooltip: MutableList<Component>)
    }

    internal val MAP = mutableMapOf<DataComponentType<*>, TooltipDataAppender<*>>()

    fun <T : Any> getAppender(dataType: DataComponentType<T>): TooltipDataAppender<T>? {
        @Suppress("UNCHECKED_CAST")
        return MAP[dataType] as? TooltipDataAppender<T>
    }

    fun <T : Any> registerAppender(data: DataComponentType<T>, appender: TooltipDataAppender<T>) {
        MAP[data] = appender
    }

    @Suppress("DEPRECATION")
    fun initDefaults() {
        registerAppender(DataComponents.STORED_ENCHANTMENTS) { data, _, tooltip ->
            val enchantments = data.keySet()
            if (enchantments.size > 1) {
                tooltip.addLast(textWarning("Item has more then 1 stored enchantment"))
            } else if (enchantments.isEmpty()) {
                tooltip.addLast(textWarning("No stored enchantments"))
            } else {
                tooltip.addAllTags("Enchantment", enchantments.first()!!, enchantments.first().getSortedTags())
            }
        }

        registerAppender(DataComponents.ENCHANTMENTS) { data, _, tooltip ->
            val enchantments = data.keySet()
            if (enchantments.size > 1) {
                tooltip.addLast(textWarning("Item has more then 1 enchantment"))
            } else if (!enchantments.isEmpty()) {
                tooltip.addAllTags("Enchantment", enchantments.first()!!, enchantments.first().getSortedTags())
            }
        }

        registerAppender(DataComponents.INSTRUMENT) { data, _, tooltip ->
            tooltip.addAllTags("Instrument", data, data.getSortedTags())
        }

        registerAppender(DataComponents.ENTITY_DATA) { data, ctx, tooltip ->
            if (!data.isEmpty) {
                if (!ctx.isEgg) {
                    val type = data.read(ENTITY_TYPE_FIELD_CODEC).result().orElse(null)
                    if (type != null) {
                        val entityHolder = type.builtInRegistryHolder()
                        tooltip.addAllTags("Entity", entityHolder, entityHolder.getSortedTags())
                    }
                }

                ctx.nbtOps()?.let { ops ->
                    data.read(ops, Painting.VARIANT_MAP_CODEC).result().ifPresent { holder ->
                        tooltip.addAllTags("Painting", holder, holder.getSortedTags())
                    }
                }
            }
        }

//        registerAppender(DataComponents.) { data, ctx, tooltip -> }

    }

    fun <T : Any> gatherTags(
        component: DataComponentType<T>,
        stack: ItemStack,
        tooltip: MutableList<Component>,
        context: Context,
    ) {
        val data = stack.get(component) ?: return
        getAppender(component)?.append(data, context, tooltip)
    }
}
