package org.teamvoided.creative_works.util.parsing

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.teamvoided.creative_works.util.text
import java.util.function.Consumer

interface WrappedComponent {

    fun get(): MutableComponent

    fun length(): Int

    fun append(textConsumer: Consumer<Component>, depth: Int, allowCollapse: Boolean) {
        textConsumer.accept(text(createStr(depth, " ")).append(get()))
    }

    fun createStr(times: Int, string: String): String {
        if (times <= 0) return ""

        var str = ""
        repeat(times) {
            str += string
        }

        return str
    }

}

