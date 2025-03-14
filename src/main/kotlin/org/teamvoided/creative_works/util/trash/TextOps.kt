package org.teamvoided.creative_works.util.trash

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.Text.literal
import net.minecraft.util.Formatting
import net.minecraft.util.Formatting.*
import java.util.stream.Stream

class TextOps(val colored: Boolean = true) : DynamicOps<Text> {
    override fun empty(): Text = Text.empty()

    private fun makeNull() = literal("null").colorFormat(colored, LIGHT_PURPLE)

    override fun createNumeric(i: Number?): Text =
        if (i == null) makeNull() else literal(i.toString()).colorFormat(colored, GOLD)

    override fun createString(value: String?): Text =
        if (value == null) makeNull() else literal(value).colorFormat(colored, GREEN)

    override fun remove(input: Text, key: String): Text = input

    override fun createList(input: Stream<Text>?): Text = if (input == null) makeNull() else {
        val list = literal("[")
        input.forEach { list.append(it).append(", ") }
        list.append("]")
    }

    override fun getStream(input: Text?): DataResult<Stream<Text>> = DataResult.success(Stream.of(input))
    override fun createMap(map: Stream<Pair<Text, Text>>?): Text = if (map == null) makeNull() else {
        val list = literal("[")
        map.forEach { list.append(it.first).append(" : ").append(it.second).append(", ") }
        list.append("]")
    }

    override fun getMapValues(input: Text?): DataResult<Stream<Pair<Text, Text>>> =
        DataResult.success(Stream.of(Pair(input, input)))

    override fun mergeToMap(map: Text?, key: Text?, value: Text?): DataResult<Text> = DataResult.success(map)
    override fun mergeToList(list: Text?, value: Text?): DataResult<Text> = DataResult.success(list)
    override fun getStringValue(input: Text?): DataResult<String> =
        if (input != null) DataResult.success(input.string) else DataResult.error { "Input is null" }
    override fun getNumberValue(input: Text?): DataResult<Number> =
        if (input != null) DataResult.success(input.string.toDouble()) else DataResult.error { "Input is null" }
    override fun <U : Any?> convertTo(outOps: DynamicOps<U>, input: Text): U {
        error("TextOps is inteded for conversion to Text not from!")
    }

    companion object {
        val INSTANCE = TextOps()
        val COLORLESS = TextOps(false)

        fun MutableText.colorFormat(colored: Boolean, formatting: Formatting): MutableText {
            if (colored) this.formatted(formatting)
            return this
        }
    }
}