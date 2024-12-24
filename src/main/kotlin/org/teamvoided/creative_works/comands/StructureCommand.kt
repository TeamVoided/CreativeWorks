package org.teamvoided.creative_works.comands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.JsonOps
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.StructureBlockBlockEntity
import net.minecraft.block.enums.StructureBlockMode
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.ResourceKeyArgument.*
import net.minecraft.registry.Holder
import net.minecraft.registry.RegistryKeys.*
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.pool.*
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.gen.feature.JigsawFeature
import net.minecraft.world.gen.feature.StructureFeature
import org.teamvoided.creative_works.CreativeWorks.log
import org.teamvoided.creative_works.util.childOf

object StructureCommand {
    private const val gap = 3
    private const val loopDepth = 64

    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val struct = CommandManager.literal("structure").build()
        dispatcher.root.addChild(struct)

        val id = argument("id", key(STRUCTURE_FEATURE))
            .executes { struct(it, getStructure(it, "id")) }
            .build()
            .childOf(struct)

        argument("pos", BlockPosArgumentType.blockPos())
            .executes { struct(it, getStructure(it, "id"), BlockPosArgumentType.getBlockPos(it, "pos")) }
            .build()
            .childOf(id)
    }

    private fun struct(
        c: CommandContext<ServerCommandSource>,
        structureHolder: Holder.Reference<StructureFeature>,
        inPos: BlockPos? = null
    ): Int {
        val src = c.source
        val world: ServerWorld = src.world
        val originPos = (inPos ?: src.player?.blockPos ?: return 0).offset(Direction.SOUTH)
        val structure = structureHolder.value()
        try {
            when (structure) {
                is JigsawFeature -> {
                    val poolReg = world.registryManager.get(STRUCTURE_POOL)
                    val didntPlace = mutableSetOf<String>()

                    val idn = structure.startPool.key.get().value
                    log.info("ORIGIN_POOL - [{}]", idn)

                    val placedPools = mutableSetOf<Identifier>()
                    val toPalace = mutableSetOf(idn)

                    var xOffset = 0
                    var maxSize = 0

                    loop@ while (toPalace.isNotEmpty()) {

                        if (toPalace.isEmpty()) break@loop
                        val id = toPalace.first()
                        log.info("TEMPLATE_POOL - [{}]", id)

                        val pool = poolReg.get(id)
                        if (pool == null) {
                            toPalace.remove(id)
                            continue@loop
                        }

                        xOffset += maxSize + gap
                        maxSize = place(pool, world, xOffset, toPalace, placedPools, originPos, didntPlace)

                        placedPools.add(id)
                        toPalace.remove(id)
                    }
                    log.info("Could Not Place : ")
                    didntPlace.forEach { log.info("\t\t- $it") }
                }

                else -> src.sendError(Text.literal("Structure was not a JigsawFeature type"))
            }
            return 1
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun place(
        pool: StructurePool,
        world: ServerWorld,
        xOffset: Int,
        toPalace: MutableSet<Identifier>,
        placedPools: MutableSet<Identifier>,
        originPos: BlockPos,
        didntPlace: MutableSet<String>
    ): Int {
        var counter = 0
        var zOffset = 0

        var maxSize = 0
        pool.elements.stream().toList().toSet().forEach {
            if (counter > loopDepth) {
                log.info("Left Loop!")
                return maxSize
            }
            when (it) {
                is EmptyPoolElement -> {}

                is SinglePoolElement -> {
                    val offsets = placeSPE(it, world, zOffset, xOffset, toPalace, placedPools, originPos)
                    zOffset += offsets.first
                    if (maxSize < offsets.second) maxSize = offsets.second
                    counter++
                }

                is FeaturePoolElement -> {
                    val x = FeaturePoolElement.CODEC.codec().encodeStart(JsonOps.INSTANCE, it)
                    didntPlace.add(
                        if (x.isError) x.error().get().message()
                        else x.getOrThrow().asJsonObject.get("element_type").asString
                    )
                }

                is ListPoolElement -> {
                    val x = StructurePoolElement.CODEC.encodeStart(JsonOps.INSTANCE, it)
                    didntPlace.add(
                        if (x.isError) x.error().get().message()
                        else x.getOrThrow().asJsonObject.get("element_type").asString
                    )
                }

                else -> {
                    val x = StructurePoolElement.CODEC.encodeStart(JsonOps.INSTANCE, it)
                    didntPlace.add(
                        if (x.isError) x.error().get().message()
                        else x.getOrThrow().toString()
                    )
                }
            }
        }
        return maxSize
    }

    private fun placeSPE(
        sPoolEle: SinglePoolElement,
        world: ServerWorld,
        zOffset: Int,
        xOffset: Int,
        toPalace: MutableSet<Identifier>,
        placedPools: MutableSet<Identifier>,
        originPos: BlockPos
    ): Pair<Int, Int> {
        val struct = sPoolEle.getStructure(world.structureTemplateManager)
        struct.blockInfoLists.forEach { i ->
            for (it in i.infos) {
                val pool = Identifier.parse(it.nbt()?.getString("pool") ?: continue)
                if (pool == Identifier.ofDefault("empty")) continue
                if (!placedPools.contains(pool)) toPalace.add(pool)
            }
        }
        val template = sPoolEle.template.left().get()
        val pos = originPos.offset(Direction.SOUTH, zOffset).offset(Direction.EAST, xOffset)
        world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.defaultState)
        val be = world.getBlockEntity(pos, BlockEntityType.STRUCTURE_BLOCK).get()
        be.mode = StructureBlockMode.SAVE
        be.size = struct.size
        be.structureName = template.toString()
        be.offset = BlockPos(1, 1, 1)

        val strPos = pos.offset(Direction.EAST).offset(Direction.SOUTH).offset(Direction.UP)
        BlockPos.stream(
            strPos.offset(Direction.DOWN),
            strPos.offset(Direction.DOWN).offset(Direction.EAST, struct.size.x).offset(Direction.SOUTH, struct.size.z)
        ).forEach { bs -> world.setBlockState(bs, Blocks.BARRIER.defaultState) }

        struct.place(
            world, strPos, strPos, StructurePlacementData(), StructureBlockBlockEntity.createRandom(0), 2
        )
        log.info("\t- Placed [{}]", template)
        return Pair(struct.size.z + gap, struct.size.x)
    }
}