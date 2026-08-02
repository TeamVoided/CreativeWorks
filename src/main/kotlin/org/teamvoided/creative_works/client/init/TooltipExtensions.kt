package org.teamvoided.creative_works.client.init

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.mojang.serialization.JsonOps
import com.mojang.serialization.MapCodec
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.entity.ai.village.poi.PoiTypes
import net.minecraft.world.item.*
import org.teamvoided.creative_works.CreativeWorksClient.clientConfig
import org.teamvoided.creative_works.client.tooltip.TooltipTags
import org.teamvoided.creative_works.client.tooltip.TooltipTags.MAP
import org.teamvoided.creative_works.mixin.BucketItemAccessor
import org.teamvoided.creative_works.util.mc.*
import org.teamvoided.creative_works.util.parsing.createError
import org.teamvoided.creative_works.util.parsing.createWrappedComp
import java.util.*
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull

object TooltipExtensions {

    fun appendTooltip(stack: ItemStack, ctx: Item.TooltipContext, flag: TooltipFlag, tooltip: MutableList<Component>) {
        if (flag.isAdvanced) {
            if (Screen.hasShiftDown()) tagToolTips(stack, ctx, tooltip)
            if (Screen.hasAltDown()) componentToolTips(stack, tooltip, ctx)
            // Mixin to this to get comp copying and dumping
            // MinecraftClient.getInstance().keyboard
        }
    }

    @Suppress("DEPRECATION")
    fun tagToolTips(stack: ItemStack, ctx: Item.TooltipContext, tooltip: MutableList<Component>) {
        val item = stack.item

        val holder = item.builtInRegistryHolder()
        tooltip.addAllTags("Item", holder, holder.getSortedTags())

        if (item is BlockItem) {
            val blockHolder = item.block.builtInRegistryHolder()
            tooltip.addAllTags("Block", blockHolder, blockHolder.getSortedTags())
            val poi = PoiTypes.forState(item.block.defaultBlockState()).getOrNull() as? Holder.Reference<PoiType>
            if (poi != null) {
                tooltip.addAllTags("POI", poi, poi.getSortedTags())
            }
        }

        if (item is SpawnEggItem) {
            val entityHolder = item.getType(stack).builtInRegistryHolder()
            tooltip.addAllTags("Entity", entityHolder, entityHolder.getSortedTags())
        }

        if (item is BucketItemAccessor) {
            val fluidHolder = item.cw_content().builtInRegistryHolder()
            tooltip.addAllTags("Fluid", fluidHolder, fluidHolder.getSortedTags())
        }

        val context = TooltipTags.Context(ctx.registries(), item is SpawnEggItem)
        for (component in MAP.keys) {
            TooltipTags.gatherTags(component, stack, tooltip, context)
        }
    }

    var lastStack: ItemStack = ItemStack.EMPTY
    var lastTooltip = mutableListOf<Component>()
    @Suppress("UNCHECKED_CAST")
    private fun componentToolTips(stack: ItemStack, text: MutableList<Component>, ctx: Item.TooltipContext) {
        if (ItemStack.isSameItemSameComponents(lastStack, stack)) {
            text.clear()
            text.addAll(lastTooltip)
            return
        }
        lastStack = stack
        lastTooltip.clear()

        val ops = ctx.registries()?.createSerializationContext(JsonOps.INSTANCE) ?: return

        val rawComponents = stack.components
        if (rawComponents !is PatchedDataComponentMap) return

        if (clientConfig.enableBaseComponents) {
            rawComponents.prototype.toList().sortedBy { it.type.toString() }.let { components ->
                if (components.isNotEmpty()) {
                    text.addLast(textMain("Base Components:"))
                    components.forEach {
                        val result = it.encodeValue(ops)
                        val data = if (result.isSuccess) result.getOrThrow() else createError(result)
                        addComponentData(data, text::addLast, textSecond(" ${it.type.toString().removeMc()}: "))
                    }
                }
            }
        }

        rawComponents.patch.toList().sortedBy { it.first.toString() }.let { components ->
            if (components.isNotEmpty()) {
                text.addLast(textMain("Components:"))
                val removed = JsonArray()
                components.forEach comp@{ (rawType, rawData) ->
                    val typeString = rawType.toString().removeMc()
                    if (rawData.isEmpty) removed.add(typeString)
                    else {
                        val type = rawType as DataComponentType<Any>
                        val data = rawData as Optional<Any>
                        val result = type.codec()?.encodeStart(ops, data.get())
                        val resultData =
                            if (result != null && result.isSuccess) result.getOrThrow()
                            else JsonPrimitive(
                                result?.error()?.getOrNull()?.message() ?: "Failed to get encoding error!"
                            )

                        addComponentData(resultData, text::addLast, textSecond(" $typeString: "))
                    }
                }

                if (!removed.isEmpty) {
                    addComponentData(removed, text::addLast, textWarning(" Removed Components: "))
                }
            }
        }

        lastTooltip.addAll(text)
    }


    fun <T : Any> MutableList<Component>.addAllTags(
        name: String, holder: Holder<*>?, tags: List<TagKey<T>>,
    ) {
        if (tags.isEmpty()) return

        addLast(textMain("$name Tags: ").append(text("(${holder?.unwrapKey()?.getOrNull()?.location()})", 5592405)))
        for (tag in tags) {
            addLast(textSecond(" #${tag.location}"))
        }
    }

    val ENTITY_TYPE_FIELD_CODEC: MapCodec<EntityType<*>> = BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("id")

    fun String.removeMc() = removePrefix("minecraft:")
}

fun addComponentData(data: JsonElement, text: Consumer<Component>, label: MutableComponent) {
    val descList = mutableListOf<Component>()
    createWrappedComp(data).append(descList::addLast, 0, clientConfig.allowCollapse)

    text.accept(label.append(descList.first()))

    for (component in descList.drop(1)) {
        text.accept(text(" ").append(component))
    }
}