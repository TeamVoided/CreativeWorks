package org.teamvoided.creative_works.util

import com.mojang.brigadier.tree.CommandNode

fun <S> CommandNode<S>.childOf(node: CommandNode<S>): CommandNode<S> {
    node.addChild(this)
    return this
}
