package org.teamvoided.creative_works

import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.teamvoided.creative_works.client.TagTooltips
import org.teamvoided.creative_works.network.CWNet
import org.teamvoided.creative_works.init.CWCommands

@Suppress("unused")
object CreativeWorks {
    const val MODID = "creative_works"

    const val TAG_COLOR = 0xDDDDDD
    const val ENTRY_COLOR = 0xAAAAAA

    @JvmField
    val log: Logger = LoggerFactory.getLogger(CreativeWorks::class.simpleName)

    fun commonInit() {
        CWNet.init()
        CWCommands.init()
    }

    fun clientInit() {
        CWNet.clientInit()
        TagTooltips.renderTagTooltip()
    }


    fun id(path: String) = Identifier.of(MODID, path)
}
