package org.teamvoided.creative_works

import net.minecraft.client.world.ClientWorld
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
object CreativeWorks {
    const val MODID = "creative_works"

    @JvmField
    val log: Logger = LoggerFactory.getLogger(CreativeWorks::class.simpleName)

    fun commonInit() {
        log.info("Hello from Common")
    }

    fun clientInit() {
        log.info("Hello from Client")
        ClientWorld.MARKER_PARTICLE_ITEMS = setOf(Items.STRUCTURE_VOID) + ClientWorld.MARKER_PARTICLE_ITEMS
    }

    fun id(path: String) = Identifier(MODID, path)
}
