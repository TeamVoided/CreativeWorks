package org.teamvoided.creative_works.util.trash

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Component.literal
import net.minecraft.ChatFormatting
import net.minecraft.ChatFormatting.*
import java.util.stream.Stream

class TextOps(val colored: Boolean = true) : DynamicOps<Component> {
    override fun empty(): Component = Component.empty()

    private fun makeNull() = literal("null").colorFormat(colored, LIGHT_PURPLE)

    override fun createNumeric(i: Number?): Component =
        if (i == null) makeNull() else literal(i.toString()).colorFormat(colored, GOLD)

    override fun createString(value: String?): Component =
        if (value == null) makeNull() else literal(value).colorFormat(colored, GREEN)

    override fun remove(input: Component, key: String): Component = input

    override fun createList(input: Stream<Component>?): Component = if (input == null) makeNull() else {
        val list = literal("[")
        input.forEach { list.append(it).append(", ") }
        list.append("]")
    }

    override fun getStream(input: Component?): DataResult<Stream<Component>> = DataResult.success(Stream.of(input))
    override fun createMap(map: Stream<Pair<Component, Component>>?): Component = if (map == null) makeNull() else {
        val list = literal("[")
        map.forEach { list.append(it.first).append(" : ").append(it.second).append(", ") }
        list.append("]")
    }

    override fun getMapValues(input: Component?): DataResult<Stream<Pair<Component, Component>>> =
        DataResult.success(Stream.of(Pair(input, input)))

    override fun mergeToMap(map: Component?, key: Component?, value: Component?): DataResult<Component> = DataResult.success(map)
    override fun mergeToList(list: Component?, value: Component?): DataResult<Component> = DataResult.success(list)
    override fun getStringValue(input: Component?): DataResult<String> =
        if (input != null) DataResult.success(input.string) else DataResult.error { "Input is null" }
    override fun getNumberValue(input: Component?): DataResult<Number> =
        if (input != null) DataResult.success(input.string.toDouble()) else DataResult.error { "Input is null" }
    override fun <U : Any?> convertTo(outOps: DynamicOps<U>, input: Component): U {
        error("TextOps is inteded for conversion to Text not from!")
    }

    companion object {
        val INSTANCE = TextOps()
        val COLORLESS = TextOps(false)

        fun MutableComponent.colorFormat(colored: Boolean, formatting: ChatFormatting): MutableComponent {
            if (colored) this.withStyle(formatting)
            return this
        }
    }
}