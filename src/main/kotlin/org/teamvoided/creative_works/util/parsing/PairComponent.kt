package org.teamvoided.creative_works.util.parsing

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.teamvoided.creative_works.CreativeWorksClient.clientConfig
import org.teamvoided.creative_works.util.mc.text
import java.util.function.Consumer

class PairComponent(
    val first: WrappedComponent,
    val second: WrappedComponent,
) : WrappedComponent {


    override fun get(): MutableComponent = first.get().append(second.get())

    override fun length(): Int = get().string.length

    override fun append(textConsumer: Consumer<Component>, depth: Int, allowCollapse: Boolean) {
        if (this.length() <= clientConfig.maxLengthBeforeWrap.get()) {
            super.append(textConsumer, depth, allowCollapse)
        } else {
            val dataList = mutableListOf<Component>()
            second.append(dataList::addLast, depth + 1, allowCollapse)
            textConsumer.accept(first.get().append(dataList.first()))

            for (component in dataList.drop(1)) {
                textConsumer.accept(text(" ").append(component))
            }
        }
    }

}