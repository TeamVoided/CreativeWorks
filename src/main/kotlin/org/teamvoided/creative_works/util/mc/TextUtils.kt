package org.teamvoided.creative_works.util.mc

import net.minecraft.client.KeyMapping
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

fun literal(message: String): MutableComponent = Component.literal(message)
fun literal(any: Any) = literal(any.toString())

fun translatable(key: String): MutableComponent = Component.translatable(key)
fun translatable(key: String, vararg args: Any): MutableComponent = Component.translatable(key, *args)

fun empty(): MutableComponent = Component.empty()

fun keybind(bind: String): MutableComponent = Component.keybind(bind)
fun keybind(key: KeyMapping) = keybind(key.name)

