package org.teamvoided.creative_works.util.parsing

import com.google.gson.*
import com.mojang.serialization.DataResult
import net.minecraft.ChatFormatting
import org.teamvoided.creative_works.util.text
import org.teamvoided.creative_works.util.toJsonString
import kotlin.jvm.optionals.getOrNull


val NULL_COLOR = ChatFormatting.LIGHT_PURPLE
val BOOLEAN_COLOR = ChatFormatting.YELLOW
val NUMBER_COLOR = ChatFormatting.GOLD
val STRING_COLOR = ChatFormatting.GREEN

val KEY_COLOR = ChatFormatting.GRAY

val ERROR_COLOR = ChatFormatting.RED

val NULL = SimpleComponent(text("null", NULL_COLOR))
fun bool(b: Boolean) = SimpleComponent(text(b, BOOLEAN_COLOR))
fun number(n: Number) = SimpleComponent(text(n, NUMBER_COLOR))
fun str(s: String) = SimpleComponent(text(s.toJsonString(), STRING_COLOR))

fun key(str: String) = SimpleComponent(text("$str: ", KEY_COLOR))
fun sign(sign: String) = SimpleComponent(text(sign, KEY_COLOR))

fun error(msg: String) = SimpleComponent(text(msg, ERROR_COLOR))

const val ERROR_KEY = "&$#"
fun createError(result: DataResult<JsonElement>?): JsonPrimitive = JsonPrimitive(
    ERROR_KEY + (result?.error()?.getOrNull()?.message() ?: "Failed to get encoding error!")
)


fun createWrappedComp(json: JsonElement): WrappedComponent = when (json) {
    is JsonNull -> NULL
    is JsonPrimitive -> {
        if (json.isBoolean) bool(json.asBoolean)
        else if (json.isNumber) number(json.asNumber)
        else if (json.isString) {
            val str = json.asString
            if (str.startsWith(ERROR_KEY)) error(json.asString) else str(json.asString)
        } else error("Error parsing JsonPrimitive!")
    }

    is JsonArray -> {
        if (json.isEmpty) sign("[]")
        else CollectionComponent(json.map(::createWrappedComp))
    }

    is JsonObject -> {
        if (json.isEmpty) CollectionComponent.EMPTY_OBJ
        else CollectionComponent.obj(json.asJsonObject.entrySet().map { (key, value) ->
            PairComponent(key(key), createWrappedComp(value))
        })
    }

    else -> error("Error parsing unknown JsonElement: ${json::class.simpleName}")
}