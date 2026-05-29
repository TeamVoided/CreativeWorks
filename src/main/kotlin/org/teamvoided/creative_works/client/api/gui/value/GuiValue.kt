package org.teamvoided.creative_works.client.api.gui.value

import org.teamvoided.creative_works.client.api.gui.DebugWidgetType

interface GuiValue<T> {

    fun get(): T

    fun set(value: T)

    fun type(): DebugWidgetType<out GuiValue<T>>

}