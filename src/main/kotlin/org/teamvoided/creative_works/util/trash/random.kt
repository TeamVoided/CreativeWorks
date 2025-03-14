package org.teamvoided.creative_works.util.trash

import net.minecraft.text.Text.literal
import java.io.StringWriter

class TextWriter : StringWriter() {
    fun toText() = this.toString().split("\n").map { literal(it) }.toList()
}