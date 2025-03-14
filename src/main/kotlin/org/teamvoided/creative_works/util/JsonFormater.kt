package org.teamvoided.creative_works.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.Text.literal
import net.minecraft.util.Formatting

val NULL_COLOR = Formatting.LIGHT_PURPLE
val BOOLEAN_COLOR = Formatting.YELLOW
val NUMBER_COLOR = Formatting.GOLD
val STRING_COLOR = Formatting.GREEN

val KEY_COLOR = Formatting.GRAY

val ERROR_COLOR = Formatting.RED

const val MAX_LENGTH = 51

val NULL = text("null", NULL_COLOR)
fun bool(b: Boolean) = text(b, BOOLEAN_COLOR)
fun number(n: Number) = text(n, NUMBER_COLOR)
fun str(s: String) = text(s.toJsonString(), STRING_COLOR)

fun key(str: String) = text("$str: ", KEY_COLOR)
fun sign(sign: String) = text(sign, KEY_COLOR)

fun error(msg: String) = text(msg, ERROR_COLOR)

fun basicJsonToText(json: JsonElement): List<Text> = buildList {
    when (json) {
        is JsonNull -> add(NULL)
        is JsonPrimitive -> {
            if (json.isBoolean) add(bool(json.asBoolean))
            else if (json.isNumber) add(number(json.asNumber))
            else if (json.isString) add(str(json.asString))
            else add(error("Error parsing JsonPrimitive!"))
        }

        is JsonArray -> {
            if (json.isEmpty) add(sign("[]"))
            else {
                add(sign("[ "))
                for (value in json) {
                    addAll(basicJsonToText(value))
                    add(sign(", "))
                }
                removeLast()
                add(sign(" ]"))
            }
        }

        is JsonObject -> {
            if (json.isEmpty) add(sign("{}"))
            else {
                add(sign("{ "))
                for ((objKey, value) in json.asJsonObject.entrySet()) {
                    val key = key(objKey)
                    val subElement = basicJsonToText(value)

                    val x = subElement.toText().string
                    if (subElement.size <= 1 || x.length < MAX_LENGTH) {
                        add(key.append(subElement.toText()))
                    } else {
                        val sign = subElement.first()
                        add(key.append(sign))
                        addAll(subElement.drop(1))
                    }
                    add(sign(", "))
                }
                removeLast()
                add(sign(" }"))
            }
        }

        else -> add(error("Error parsing unknown JsonElement: ${json::class.simpleName}"))
    }
}

fun jsonToText(json: JsonElement): List<Text> = buildList {
    when (json) {
        is JsonNull -> add(NULL)
        is JsonPrimitive -> {
            if (json.isBoolean) add(bool(json.asBoolean))
            else if (json.isNumber) add(number(json.asNumber))
            else if (json.isString) add(str(json.asString))
            else add(error("Error parsing JsonPrimitive!"))
        }

        is JsonArray -> parseArray(json)
        is JsonObject -> {
            if (json.isEmpty) add(sign("{}"))
            else {
                add(sign("{ "))
                for ((objKey, value) in json.asJsonObject.entrySet()) {
                    val key = key(objKey)
                    val subElement = basicJsonToText(value)

                    val x = subElement.toText().string
                    if (subElement.size <= 1 || x.length < MAX_LENGTH) {
                        add(key.append(subElement.toText()))
                    } else {
                        val sign = subElement.first()
                        add(key.append(sign))
                        addAll(subElement.drop(1))
                    }
                    add(sign(","))
                }
                removeLast()
                add(sign(" }"))
            }
        }

        else -> add(error("Error parsing unknown JsonElement: ${json::class.simpleName}"))
    }
}

private fun MutableList<Text>.parseArray(array: JsonArray) {
    if (array.isEmpty) {
        add(sign("[]"))
        return
    }
    val text = array.map { basicJsonToText(it) }
    val map = text.associateWith { list -> list.sumOf { it.string.length } }


    if (map.size < 10 && map.map { it.value }.sum() < MAX_LENGTH) {
        val rep = sign("[ ")
        rep.append(error("Hello there!"))
        text.forEach { list ->
            list.forEach { rep.append(it) }
            rep.append(sign(", "))
        }
        rep.append(sign(" ]"))
        add(rep)
        return
    }

    add(sign("[ "))
    for (value in text) {
        addAll(value)
        add(sign(", "))
    }
    removeLast()
    add(sign(" ]"))
}

fun Collection<Text>.toText() = this.reduce { acc, text -> acc.copy().append(text) }

fun text(obj: Any): MutableText = literal(obj.toString())
fun MutableText.color(color: Formatting): MutableText = this.formatted(color)
fun text(obj: Any, color: Formatting): MutableText = text(obj).color(color)
