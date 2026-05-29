package org.teamvoided.creative_works

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.teamvoided.creative_works.cfg.CWConfig
import org.teamvoided.creative_works.init.CWCommands
import org.teamvoided.creative_works.init.CWWorldTypes
import org.teamvoided.creative_works.network.CWNet

@Suppress("unused")
object CreativeWorks : ModInitializer {
    const val MODID = "creative_works"

    const val MAIN_COLOR = 0xDDDDDD
    const val SECONDARY_COLOR = 0xAAAAAA
    const val WARNING_COLOR = 0xeb6666

    @JvmField
    val log: Logger = LoggerFactory.getLogger(MODID)

    @JvmField
    var config = ConfigApi.registerAndLoadConfig(::CWConfig)

    override fun onInitialize() {
        CWWorldTypes.init()
        CWNet.init()
        CWCommands.init()
    }

    fun id(namespace: String, path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(namespace, path)
    fun mc(path: String): ResourceLocation = ResourceLocation.withDefaultNamespace(path)
    fun id(path: String) = id(MODID, path)
}
