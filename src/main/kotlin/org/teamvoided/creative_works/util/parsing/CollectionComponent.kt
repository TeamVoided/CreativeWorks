package org.teamvoided.creative_works.util.parsing

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.teamvoided.creative_works.CreativeWorksClient.clientConfig
import org.teamvoided.creative_works.util.mc.text
import java.util.function.Consumer

class CollectionComponent(
    val openSymbol: WrappedComponent,
    val closeSymbol: WrappedComponent,
    val components: List<WrappedComponent>,
) : WrappedComponent {

    constructor(elements: List<WrappedComponent>) : this(sign("["), sign("]"), elements)

    override fun get(): MutableComponent {
        return openSymbol.get()
            .append(" ")
            .append(
                components.map(WrappedComponent::get).reduce { acc, text ->
                    acc.copy().append(sign(", ").get()).append(text)
                })
            .append(text(" ").append(closeSymbol.get()))
    }

    override fun length(): Int = get().string.length

    override fun append(textConsumer: Consumer<Component>, depth: Int, allowCollapse: Boolean) {
        if (length() <= clientConfig.maxLengthBeforeWrap.get()) {
            super.append(textConsumer, depth, allowCollapse)
        } else if (allowCollapse && length() > clientConfig.maxLengthBeforeCollapse.get()) {
            textConsumer.accept(
                openSymbol.get().append(text(" ... ").withStyle(ChatFormatting.DARK_GRAY)).append(closeSymbol.get())
            )
        } else {
            openSymbol.append(textConsumer, depth - 1, allowCollapse)
            components.forEach {
                it.append(textConsumer, depth + 1, allowCollapse)
            }
            closeSymbol.append(textConsumer, depth - 1, allowCollapse)
        }
    }


    companion object {

        val EMPTY_OBJ = sign("{}")

        fun obj(elements: List<PairComponent>): CollectionComponent {
            return CollectionComponent(sign("{"), sign("}"), elements)
        }

    }
}