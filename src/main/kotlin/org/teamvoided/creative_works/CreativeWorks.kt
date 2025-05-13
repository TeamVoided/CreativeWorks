package org.teamvoided.creative_works

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.teamvoided.creative_works.client.Clint
import org.teamvoided.creative_works.client.DebugWidgetRegistry
import org.teamvoided.creative_works.client.TooltipExtensions
import org.teamvoided.creative_works.init.CWCommands
import org.teamvoided.creative_works.init.CWWorldTypes
import org.teamvoided.creative_works.network.CWNet

@Suppress("unused")
object CreativeWorks {
    const val MODID = "creative_works"

    const val MAIN_COLOR = 0xDDDDDD
    const val SECONDARY_COLOR = 0xAAAAAA
    const val WARNING_COLOR = 0xeb6666

    @JvmField
    val log: Logger = LoggerFactory.getLogger(MODID)

    @JvmField
    var config = ConfigApi.registerAndLoadConfig(::CWConfig)

    fun commonInit() {
        CWWorldTypes.init()
        CWNet.init()
        CWCommands.init()
    }

    fun clientInit() {
        Clint.init()
        CWNet.clientInit()
        TooltipExtensions.renderTooltip()
        DebugWidgetRegistry.init()
//        TestRenderer.init()
    }


    fun id(path: String) = Identifier.of(MODID, path)
}
