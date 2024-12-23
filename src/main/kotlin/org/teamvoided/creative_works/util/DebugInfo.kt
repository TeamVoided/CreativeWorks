package org.teamvoided.creative_works.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBind
import net.minecraft.server.world.ServerChunkManager
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.gen.DensityFunction.SinglePointContext
import net.minecraft.world.gen.noise.NoiseRouterData
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
            if (debugKey.wasPressed()) DebugRenderer.toggle()
        }
    }

    fun cords() {
        val cords = getCords() ?: return
        "x=${cords.x},y=${cords.y},z=${cords.z}".addToRenderer("Cords")
    }

    private fun worldgenInfo() {
        val serverWorld: ServerWorld = getServerWorld() ?: return
        val serverChunkManager: ServerChunkManager = serverWorld.chunkManager
        val randomState = serverChunkManager.randomState
        val pos = getCords() ?: return
        val decimalFormat = DecimalFormat("0.000")
        val noiseRouter = randomState.router
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
        KeyBindingHelper.registerKeyBinding(KeyBind(translationKey, keyCode, category))


    private fun getServerWorld(): ServerWorld? {
        return MinecraftClient.getInstance().server?.getWorld(MinecraftClient.getInstance().world?.registryKey)
    }
}
