package org.teamvoided.creative_works

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.teamvoided.creative_works.cfg.CWClientConfig
import org.teamvoided.creative_works.client.api.gui.DebugWidgetTypes
import org.teamvoided.creative_works.client.init.CWClientEvents
import org.teamvoided.creative_works.client.init.CWImGui
import org.teamvoided.creative_works.client.init.CWKeyMappings
import org.teamvoided.creative_works.network.CWNet

@Suppress("unused")
object CreativeWorksClient : ClientModInitializer {

    @JvmField
    var clientConfig = ConfigApi.registerAndLoadConfig(::CWClientConfig, RegisterType.CLIENT)

    var hasImGui = FabricLoader.getInstance().isModLoaded("imguimc")

    override fun onInitializeClient() {
        CWKeyMappings.init()
        CWNet.clientInit()
        if (hasImGui) {
            CWImGui.init()
            DebugWidgetTypes.init()
        }
        CWClientEvents.init()
//        TestRenderer.init()
    }

}