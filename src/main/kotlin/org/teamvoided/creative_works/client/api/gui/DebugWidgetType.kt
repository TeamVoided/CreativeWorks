package org.teamvoided.creative_works.client.api.gui

import org.teamvoided.creative_works.client.api.gui.value.GuiValue

interface DebugWidgetType<T : GuiValue<*>> {

    class SimpleWidgetType<T : GuiValue<*>> : DebugWidgetType<T>

}