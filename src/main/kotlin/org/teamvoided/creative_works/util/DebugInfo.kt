package org.teamvoided.creative_works.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.KeyMapping
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.levelgen.DensityFunction.SinglePointContext
import org.lwjgl.glfw.GLFW
import org.teamvoided.creative_works.util.DebugRenderer.addToRenderer
import org.teamvoided.creative_works.util.DebugRenderer.getCords
import java.text.DecimalFormat

object DebugInfo {
    val debugKey = keybind("custom key", GLFW.GLFW_KEY_R)
    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register { _ ->
            cords()
           worldgenInfo()
            if (debugKey.consumeClick()) DebugRenderer.toggle()
        }
    }

    fun cords() {
        val cords = getCords() ?: return
        "x=${cords.x},y=${cords.y},z=${cords.z}".addToRenderer("Cords")
    }

    private fun worldgenInfo() {
        val serverWorld: ServerLevel = getServerWorld() ?: return
        val serverChunkManager: ServerChunkCache = serverWorld.chunkSource
        val randomState = serverChunkManager.randomState()
        val pos = getCords() ?: return
        val decimalFormat = DecimalFormat("0.000")
        val noiseRouter = randomState.router()
        val singlePointContext = SinglePointContext(pos.x, pos.y, pos.z)
//        val d = noiseRouter.weirdness().compute(singlePointContext)
//        "-------".addToRenderer("NoiseRouter")
//        decimalFormat.format(noiseRouter.temperature().compute(singlePointContext)).addToRenderer("Temperature")
//        decimalFormat.format(noiseRouter.vegetation().compute(singlePointContext)).addToRenderer("Vegetation")
//        decimalFormat.format(noiseRouter.continentalness().compute(singlePointContext)).addToRenderer("Continentalness")
//        decimalFormat.format(noiseRouter.erosion().compute(singlePointContext)).addToRenderer("Erosion")
//        decimalFormat.format(noiseRouter.depth().compute(singlePointContext)).addToRenderer("Depth")
//        decimalFormat.format(d).addToRenderer("Weirdness")
//        decimalFormat.format(NoiseRouterData.method_41546(d.toFloat()).toDouble()).addToRenderer("PV")
//        decimalFormat.format(noiseRouter.initialNonJaggedDensity().compute(singlePointContext))
//            .addToRenderer("InitialNonJaggedDensity")
//        decimalFormat.format(noiseRouter.fullNoise().compute(singlePointContext)).addToRenderer("Noise")
//        "-----".addToRenderer("----------")
    }

    fun keybind(translationKey: String, keyCode: Int, category: String = "") =
        KeyBindingHelper.registerKeyBinding(KeyMapping(translationKey, keyCode, category))


    private fun getServerWorld(): ServerLevel? {
        return Minecraft.getInstance().singleplayerServer?.getLevel(Minecraft.getInstance().level?.dimension())
    }
}
