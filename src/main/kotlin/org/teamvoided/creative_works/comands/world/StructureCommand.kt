package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.JsonOps
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.StructureBlockEntity
import net.minecraft.world.level.block.state.properties.StructureMode
import net.minecraft.commands.arguments.coordinates.BlockPosArgument
import net.minecraft.commands.arguments.ResourceKeyArgument.getStructure
import net.minecraft.commands.arguments.ResourceKeyArgument.key
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries.STRUCTURE
import net.minecraft.core.registries.Registries.TEMPLATE_POOL
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement
import net.minecraft.world.level.levelgen.structure.pools.FeaturePoolElement
import net.minecraft.world.level.levelgen.structure.pools.ListPoolElement
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
import org.teamvoided.creative_works.CreativeWorks.log
import org.teamvoided.creative_works.util.childOf

object StructureCommand {
    private const val gap = 3
    private const val loopDepth = 64

    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val struct = Commands.literal("structure").build()
        dispatcher.root.addChild(struct)

        val id = argument("id", key(STRUCTURE))
            .executes { struct(it, getStructure(it, "id")) }
            .build()
            .childOf(struct)

        argument("pos", BlockPosArgument.blockPos())
            .executes { struct(it, getStructure(it, "id"), BlockPosArgument.getSpawnablePos(it, "pos")) }
            .build()
            .childOf(id)
    }

    private fun struct(
        c: CommandContext<CommandSourceStack>,
        structureHolder: Holder.Reference<Structure>,
        inPos: BlockPos? = null
    ): Int {
        val src = c.source
        val world: ServerLevel = src.level
        val originPos = (inPos ?: src.player?.blockPosition() ?: return 0).relative(Direction.SOUTH)
        val structure = structureHolder.value()
        try {
            when (structure) {
                is JigsawStructure -> {
                    val poolReg = world.registryAccess().registryOrThrow(TEMPLATE_POOL)
                    val didntPlace = mutableSetOf<String>()

                    val idn = structure.startPool.unwrapKey().get().identifier()
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

                else -> src.sendFailure(Component.literal("Structure was not a JigsawFeature type"))
            }
            return 1
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun place(
        pool: StructureTemplatePool,
        world: ServerLevel,
        xOffset: Int,
        toPalace: MutableSet<Identifier>,
        placedPools: MutableSet<Identifier>,
        originPos: BlockPos,
        didntPlace: MutableSet<String>
    ): Int {
        var counter = 0
        var zOffset = 0

        var maxSize = 0
        pool.templates.stream().toList().toSet().forEach {
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
        world: ServerLevel,
        zOffset: Int,
        xOffset: Int,
        toPalace: MutableSet<Identifier>,
        placedPools: MutableSet<Identifier>,
        originPos: BlockPos
    ): Pair<Int, Int> {
        val struct = sPoolEle.getTemplate(world.structureManager)
        struct.palettes.forEach { i ->
            for (it in i.blocks) {
                val pool = Identifier.parse(it.nbt()?.getString("pool") ?: continue)
                if (pool == Identifier.withDefaultNamespace("empty")) continue
                if (!placedPools.contains(pool)) toPalace.add(pool)
            }
        }
        val template = sPoolEle.template.left().get()
        val pos = originPos.relative(Direction.SOUTH, zOffset).relative(Direction.EAST, xOffset)
        world.setBlockAndUpdate(pos, Blocks.STRUCTURE_BLOCK.defaultBlockState())
        val be = world.getBlockEntity(pos, BlockEntityType.STRUCTURE_BLOCK).get()
        be.mode = StructureMode.SAVE
        be.structureSize = struct.size
        be.structureName = template.toString()
        be.structurePos = BlockPos(1, 1, 1)

        val strPos = pos.relative(Direction.EAST).relative(Direction.SOUTH).relative(Direction.UP)
        BlockPos.betweenClosedStream(
            strPos.relative(Direction.DOWN),
            strPos.relative(Direction.DOWN).relative(Direction.EAST, struct.size.x).relative(Direction.SOUTH, struct.size.z)
        ).forEach { bs -> world.setBlockAndUpdate(bs, Blocks.BARRIER.defaultBlockState()) }

        struct.placeInWorld(
            world, strPos, strPos, StructurePlaceSettings(), StructureBlockEntity.createRandom(0), 2
        )
        log.info("\t- Placed [{}]", template)
        return Pair(struct.size.z + gap, struct.size.x)
    }
}