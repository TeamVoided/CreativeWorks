package org.teamvoided.creative_works.client.api.gui.value

import org.teamvoided.creative_works.client.api.gui.DebugWidgetType
import org.teamvoided.creative_works.client.api.gui.DebugWidgetTypes

class ArbitraryValue(val renderUI: (label: String) -> Unit = {}) : GuiValue<Unit> {

    override fun get() = Unit

    override fun set(value: Unit) = Unit

    override fun type(): DebugWidgetType<ArbitraryValue> = DebugWidgetTypes.ARBITRARY

}