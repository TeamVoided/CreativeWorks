package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos
import net.minecraft.commands.arguments.coordinates.BlockPosArgument.getSpawnablePos
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Display.BlockDisplay
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.SignBlockEntity
import net.minecraft.world.level.block.entity.SignText
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BedPart
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.error
import org.teamvoided.creative_works.util.message
import org.teamvoided.creative_works.util.text
import kotlin.math.roundToInt
import kotlin.math.sqrt

object PlaceAllCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("place_all").executes { exe(it, null, null) }.buildChildOf(dispatcher.root)
        val id = argument("id", word())
            .suggests { _, builder ->
                builder.listSuggestions(BuiltInRegistries.BLOCK.map { it.descriptionId.split(".")[1] }.toSet())
            }
            .executes { exe(it, getString(it, "id"), null) }
            .buildChildOf(root)
        argument("pos", blockPos()).executes { exe(it, getString(it, "id"), getSpawnablePos(it, "pos")) }
            .buildChildOf(id)
//        literal("place_all_kill").executes(::killAll).buildChildOf(dispatcher.root)
    }

    fun killAll(ctx: CommandContext<CommandSourceStack>): Int {
        val src = ctx.source ?: return 0
        val world = src.level ?: return 0
        world.getEntities(EntityType.BLOCK_DISPLAY) {
            it.tags.contains("place_all")
        }.forEach { it.discard() }
        return Command.SINGLE_SUCCESS
    }

    fun exe(ctx: CommandContext<CommandSourceStack>, id: String?, pos: BlockPos?): Int {
        val src = ctx.source ?: return 0
        val world = src.level
        if (world == null) {
            src.error("Command has no world!")
            return 0
        }
        val cmdPos = src.position
        val startPos = pos ?: BlockPos(cmdPos.x.toInt(), cmdPos.y.toInt(), cmdPos.z.toInt())

        val reg = BuiltInRegistries.BLOCK
        val blocks =
            (if (id != null) reg.filter { getId(it).namespace == id } else reg.toList()) //.sortedBy { getId(it) }
        if (blocks.isEmpty()) {
            src.error("No blocks found!")
            return 0
        }

        val maxCol = calcMacColumns(blocks.size)
        for ((row, list) in blocks.chunked(maxCol + (maxCol / 8)).withIndex()) {
            for ((col, block) in list.withIndex()) {
                val newPos = startPos.offset(col * 2, row * 2, row * 2)
                world.setBlockAndUpdate(newPos.relative(Direction.DOWN), Blocks.BARRIER.defaultBlockState())
                world.setBlockAndUpdate(newPos.relative(Direction.SOUTH), Blocks.BARRIER.defaultBlockState())
                try {
                    world.setBlockAndUpdate(newPos, getState(block))
                    placeAdditional(world, newPos, block)
                } catch (e: Exception) {
                    src.error("Error while placing block ${block.descriptionId}")
                }

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

    private fun placeSign(world: ServerLevel, pos: BlockPos, block: Block) {
        var newPos = pos
        if (world.getBlockState(pos) != Blocks.OAK_SIGN.defaultBlockState()) {
            val pso2 = pos.relative(Direction.DOWN).relative(Direction.NORTH)
            world.setBlockAndUpdate(pso2, Blocks.OAK_WALL_SIGN.defaultBlockState())
            newPos = pso2

        }
        world.getBlockEntity(newPos)?.let {
            if (it is SignBlockEntity) {
                val id = getId(block)
                var text = SignText().setMessage(0, text(id.namespace + ":"))

                id.path.toList().chunked(16)
                    .forEachIndexed { i, s ->
                        if (i + 1 < 4) {

                            text = text.setMessage(i + 1, text(s.joinToString("")))
                        }
                    }
                it.setText(text, true)
            }
        }

    }

    private fun summonBlockDisplay(world: ServerLevel, pos: BlockPos, block: Block) {
        val display = BlockDisplay(EntityType.BLOCK_DISPLAY, world)
        display.setPos(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        display.blockState = block.defaultBlockState()
        display.customName = text(getId(block))
        display.tags.add("place_all")
        world.addFreshEntity(display)
    }

    private fun getState(block: Block): BlockState {
        var state = block.defaultBlockState()
        if (state.hasProperty(BlockStateProperties.WATERLOGGED))
            state = state.setValue(BlockStateProperties.WATERLOGGED, false)
        if (!state.fluidState.isEmpty) state = Blocks.OAK_SIGN.defaultBlockState()

        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) state =
            state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
        if (block is StairBlock) state = state.setValue(StairBlock.FACING, Direction.SOUTH)
        if (block is BannerBlock || block is StandingSignBlock) state =
            state.setValue(BlockStateProperties.ROTATION_16, 8)

        return state
    }

    private fun placeAdditional(world: Level, pos: BlockPos, block: Block) {
        if (block.defaultBlockState().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) world.setBlockAndUpdate(
            pos.relative(Direction.UP),
            block.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER)
        )
        if (block is BedBlock) world.setBlockAndUpdate(
            pos.relative(Direction.NORTH),
            block.defaultBlockState().setValue(BedBlock.PART, BedPart.HEAD)
        )
        if (!block.defaultBlockState().canSurvive(world, pos)
            && block !is HorizontalDirectionalBlock
            && block !is SignBlock
            && block !is BaseCoralPlantTypeBlock
            && block !is WallTorchBlock
        ) world.setBlockAndUpdate(
            pos.relative(Direction.DOWN),
            (if (block is CropBlock || block is StemBlock || block is AttachedStemBlock) Blocks.FARMLAND else Blocks.GRASS_BLOCK).defaultBlockState()
        )
        if (block is BaseCoralPlantTypeBlock || block is CoralBlock) world.setBlockAndUpdate(
            pos.relative(Direction.DOWN),
            Blocks.MANGROVE_ROOTS.defaultBlockState().setValue(BaseCoralPlantTypeBlock.WATERLOGGED, true)
        )
    }

    private fun getId(block: Block): Identifier = BuiltInRegistries.BLOCK.getKey(block)
}
