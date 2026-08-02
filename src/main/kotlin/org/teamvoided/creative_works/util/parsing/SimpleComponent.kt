package org.teamvoided.creative_works.util.parsing

import net.minecraft.network.chat.MutableComponent

class SimpleComponent(val component: MutableComponent) : WrappedComponent {

    override fun get(): MutableComponent = component.copy()

    override fun length(): Int = component.string.length

}