package org.teamvoided.creative_works

import net.fabricmc.api.ClientModInitializer
import org.teamvoided.creative_works.client.Clint
import org.teamvoided.creative_works.client.DebugWidgetRegistry
import org.teamvoided.creative_works.client.TooltipExtensions
import org.teamvoided.creative_works.network.CWNet

@Suppress("unused")
object CreativeWorksClient : ClientModInitializer {
//    @JvmField
//    var config = ConfigApi.registerAndLoadConfig(::CWConfig)
    override fun onInitializeClient() {
        Clint.init()
        CWNet.clientInit()
        TooltipExtensions.renderTooltip()
        DebugWidgetRegistry.init()
//        TestRenderer.init()
    }

}