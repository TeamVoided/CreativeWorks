package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.context.CommandContext
import net.minecraft.block.*
import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.block.entity.SignText
import net.minecraft.block.enums.BedPart
import net.minecraft.block.enums.DoubleBlockHalf
import net.minecraft.block.sign.AbstractSignBlock
import net.minecraft.block.sign.SignBlock
import net.minecraft.command.argument.BlockPosArgumentType.blockPos
import net.minecraft.command.argument.BlockPosArgumentType.getBlockPos
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.DisplayEntity.BlockDisplayEntity
import net.minecraft.registry.Registries
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message
import org.teamvoided.creative_works.util.text
import kotlin.math.roundToInt
import kotlin.math.sqrt

object PlaceAllCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("place_all").executes { exe(it, null, null) }.buildChildOf(dispatcher.root)
        val id = argument("id", word())
            .suggests { _, builder ->
                builder.listSuggestions(Registries.BLOCK.map { it.translationKey.split(".")[1] }.toSet())
            }
            .executes { exe(it, getString(it, "id"), null) }
            .buildChildOf(root)
        argument("pos", blockPos()).executes { exe(it, getString(it, "id"), getBlockPos(it, "pos")) }
            .buildChildOf(id)
//        literal("place_all_kill").executes(::killAll).buildChildOf(dispatcher.root)
    }

    fun killAll(ctx: CommandContext<ServerCommandSource>): Int {
        val src = ctx.source ?: return 0
        val world = src.world ?: return 0
        world.getEntitiesByType(EntityType.BLOCK_DISPLAY) {
            it.scoreboardTags.contains("place_all")
        }.forEach { it.discard() }
        return Command.SINGLE_SUCCESS
    }

    fun exe(ctx: CommandContext<ServerCommandSource>, id: String?, pos: BlockPos?): Int {
        val src = ctx.source ?: return 0
        val world = src.world
        if (world == null) {
            src.error("Command has no world!")
            return 0
        }
        val cmdPos = src.position
        val startPos = pos ?: BlockPos(cmdPos.x.toInt(), cmdPos.y.toInt(), cmdPos.z.toInt())

        val reg = Registries.BLOCK
        val blocks =
            (if (id != null) reg.filter { getId(it).namespace == id } else reg.toList()) //.sortedBy { getId(it) }
        if (blocks.isEmpty()) {
            src.error("No blocks found!")
            return 0
        }

        val maxCol = calcMacColumns(blocks.size)
        for ((row, list) in blocks.chunked(maxCol + (maxCol / 8)).withIndex()) {
            for ((col, block) in list.withIndex()) {
                val newPos = startPos.add(col * 2, row * 2, row * 2)
                world.setBlockState(newPos.offset(Direction.DOWN), Blocks.BARRIER.defaultState)
                world.setBlockState(newPos.offset(Direction.SOUTH), Blocks.BARRIER.defaultState)
                world.setBlockState(newPos, getState(block))
                placeAdditional(world, newPos, block)

                placeSign(world, newPos, block)
            }
        }

        src.message("Placed all Blocks!")
        return Command.SINGLE_SUCCESS
    }

    private fun calcMacColumns(size: Int): Int {
        if (size <= 10) return size
        return sqrt(size.toFloat()).roundToInt()
    }

    private fun placeSign(world: ServerWorld, pos: BlockPos, block: Block) {
        var newPos = pos
        if (world.getBlockState(pos) != Blocks.OAK_SIGN.defaultState) {
            val pso2 = pos.offset(Direction.DOWN).offset(Direction.NORTH)
            world.setBlockState(pso2, Blocks.OAK_WALL_SIGN.defaultState)
            newPos = pso2

        }
        world.getBlockEntity(newPos)?.let {
            if (it is SignBlockEntity) {
                val id = getId(block)
                var text = SignText().withMessage(0, text(id.namespace + ":"))

                id.path.toList().chunked(16)
                    .forEachIndexed { i, s ->
                        if (i + 1 < 4) {

                            text = text.withMessage(i + 1, text(s.joinToString("")))
                        }
                    }
                it.setText(text, true)
            }
        }

    }

    private fun summonBlockDisplay(world: ServerWorld, pos: BlockPos, block: Block) {
        val display = BlockDisplayEntity(EntityType.BLOCK_DISPLAY, world)
        display.setPosition(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        display.state = block.defaultState
        display.customName = text(getId(block))
        display.scoreboardTags.add("place_all")
        world.spawnEntity(display)
    }

    private fun getState(block: Block): BlockState {
        var state = block.defaultState
        if (state.contains(Properties.WATERLOGGED))
            state = state.with(Properties.WATERLOGGED, false)
        if (!state.fluidState.isEmpty) state = Blocks.OAK_SIGN.defaultState

        if (state.contains(Properties.DOUBLE_BLOCK_HALF)) state =
            state.with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
        if (block is StairsBlock) state = state.with(StairsBlock.FACING, Direction.SOUTH)
        if (block is BannerBlock || block is SignBlock) state = state.with(Properties.ROTATION, 8)

        return state
    }

    private fun placeAdditional(world: World, pos: BlockPos, block: Block) {
        if (block.defaultState.contains(Properties.DOUBLE_BLOCK_HALF)) world.setBlockState(
            pos.offset(Direction.UP),
            block.defaultState.with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER)
        )
        if (block is BedBlock) world.setBlockState(
            pos.offset(Direction.NORTH),
            block.defaultState.with(BedBlock.PART, BedPart.HEAD)
        )
        if (!block.defaultState.canPlaceAt(world, pos)
            && block !is HorizontalFacingBlock
            && block !is AbstractSignBlock
            && block !is CoralParentBlock
            && block !is WallTorchBlock
        ) world.setBlockState(
            pos.offset(Direction.DOWN),
            (if (block is CropBlock || block is StemBlock || block is AttachedStemBlock) Blocks.FARMLAND else Blocks.GRASS_BLOCK).defaultState
        )
        if (block is CoralParentBlock || block is CoralBlockBlock) world.setBlockState(
            pos.offset(Direction.DOWN),
            Blocks.MANGROVE_ROOTS.defaultState.with(CoralParentBlock.WATERLOGGED, true)
        )
    }

    private fun getId(block: Block): Identifier = Registries.BLOCK.getId(block)
}
