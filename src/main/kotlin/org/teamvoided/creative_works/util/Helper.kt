package org.teamvoided.creative_works.util

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.text.Text

fun <S> CommandNode<S>.childOf(node: CommandNode<S>): CommandNode<S> {
    node.addChild(this)
    return this
}
fun <S> LiteralArgumentBuilder<S>.buildChildOf(node: CommandNode<S>): CommandNode<S> {
    return this.build().childOf(node)
}

fun ltxt(s: String)= Text.literal(s)
