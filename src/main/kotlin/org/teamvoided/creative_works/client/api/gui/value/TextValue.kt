package org.teamvoided.creative_works.client.api.gui.value

import org.teamvoided.creative_works.client.api.gui.DebugWidgetType
import org.teamvoided.creative_works.client.api.gui.DebugWidgetTypes

class TextValue(var text: String = "") : GuiValue<String> {

    override fun get(): String = text

    override fun set(value: String) {
        text = value
    }

    override fun type(): DebugWidgetType<TextValue> = DebugWidgetTypes.TEXT

}