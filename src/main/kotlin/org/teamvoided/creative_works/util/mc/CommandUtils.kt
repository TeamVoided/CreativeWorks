package org.teamvoided.creative_works.util.mc

import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component

fun CommandSourceStack.message(text: Component) = sendSystemMessage(text)
fun CommandSourceStack.message(text: String) = message(translatable(text))
fun CommandSourceStack.message(text: String, vararg args: Any) = message(translatable(text, args))
fun CommandSourceStack.message(any: Any) = message(literal(any))
fun CommandSourceStack.message(text: String, component: Component) = message(translatable(text).append(component))

fun CommandSourceStack.error(text: Component) = sendFailure(text)
fun CommandSourceStack.error(text: String) = error(translatable(text))
fun CommandSourceStack.error(text: String, vararg args: Any) = error(translatable(text, args))
fun CommandSourceStack.error(any: Any) = error(literal(any))
fun CommandSourceStack.error(text: String, component: Component) = error(translatable(text).append(component))

