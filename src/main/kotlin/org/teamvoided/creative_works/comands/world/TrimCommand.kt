package org.teamvoided.creative_works.comands.world

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.coordinates.BlockPosArgument
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.item.equipment.trim.ArmorTrim
import net.minecraft.world.item.equipment.trim.TrimMaterial
import net.minecraft.world.item.equipment.trim.TrimPattern
import org.teamvoided.creative_works.comands.args.MaterialArgumentType
import org.teamvoided.creative_works.comands.args.MaterialArgumentType.materialArg
import org.teamvoided.creative_works.comands.args.PatterArgumentType
import org.teamvoided.creative_works.comands.args.PatterArgumentType.patternArg
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.childOf
import org.teamvoided.creative_works.util.toHSL
import java.awt.Color

object TrimCommand {
    private var items = false
    private var grid = false

    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val trimNode = literal("trim").buildChildOf(dispatcher.root)

        literal("items").executes(TrimCommand::toggleItems).buildChildOf(trimNode)
        literal("grid").executes(TrimCommand::toggleGrid).buildChildOf(trimNode)


        val trimNodeBlockPodArg = argument("pos", BlockPosArgument.blockPos()).build()
        trimNode.addChild(trimNodeBlockPodArg)

        literal("all")
            .executes { all(it, BlockPosArgument.getSpawnablePos(it, "pos")) }
            .buildChildOf(trimNodeBlockPodArg)


        val patNode = literal("pattern").build()
        trimNodeBlockPodArg.addChild(patNode)
        val patNodePatArg = patternArg("pattern").executes {
            pat(
                it, PatterArgumentType.getPattern(it, "pattern"),
                BlockPosArgument.getSpawnablePos(it, "pos")
            )
        }.build()
        patNode.addChild(patNodePatArg)

        val matNode = literal("material").build()
        trimNodeBlockPodArg.addChild(matNode)
        materialArg("material").executes {
            mat(it, MaterialArgumentType.getMaterial(it, "material"), BlockPosArgument.getSpawnablePos(it, "pos"))
        }.build().childOf(matNode)


        val bothNode = literal("both").build()
        trimNodeBlockPodArg.addChild(bothNode)
        val bothNodeMatArg =
            materialArg("material").build()
        bothNode.addChild(bothNodeMatArg)
        val bothNodePatArg = patternArg("pattern")
            .executes {
                both(
                    it, MaterialArgumentType.getMaterial(it, "material"),
                    PatterArgumentType.getPattern(it, "pattern"),
                    BlockPosArgument.getSpawnablePos(it, "pos")
                )
            }
            .build()
        bothNodeMatArg.addChild(bothNodePatArg)
    }

    private fun toggleItems(c: CommandContext<CommandSourceStack>): Int {
        val src = c.source
        items = !items
        src.sendSystemMessage(Component.translatable("Items toggled! [%s]", items))
        return 1
    }

    private fun toggleGrid(c: CommandContext<CommandSourceStack>): Int {
        val src = c.source
        grid = !grid
        src.sendSystemMessage(Component.translatable("Grid toggled! [%s]", grid))
        return 1
    }

    private fun all(c: CommandContext<CommandSourceStack>, pos: BlockPos): Int =
        spawnArmorTrims(c.source, { true }, { true }, pos, false)

    private fun pat(c: CommandContext<CommandSourceStack>, pat: TrimPattern, pos: BlockPos): Int =
        spawnArmorTrims(c.source, { it == pat }, { true }, pos, false)


    private fun mat(c: CommandContext<CommandSourceStack>, mat: TrimMaterial, pos: BlockPos): Int =
        spawnArmorTrims(c.source, { true }, { it == mat }, pos, false)

    private fun both(
        c: CommandContext<CommandSourceStack>, mat: TrimMaterial, pat: TrimPattern, pos: BlockPos
    ): Int =
        spawnArmorTrims(c.source, { it == pat }, { it == mat }, pos, true)


    private fun spawnArmorTrims(
        s: CommandSourceStack, patPred: (TrimPattern) -> Boolean, matPred: (TrimMaterial) -> Boolean,
        blockPos: BlockPos, row: Boolean
    ): Int {
//        val world = s.level
//        val permList = mutableListOf<ArmorTrim>()
//        val armorReg = BuiltInRegistries.ARMOR_MATERIAL.filter { it != ArmorMaterials.ARMADILLO.value() }
//        val patternReg = world.registryAccess().registryOrThrow(Registries.TRIM_PATTERN)
//        val materialReg = world.registryAccess().registryOrThrow(Registries.TRIM_MATERIAL)
//
//        val patterns = patternReg.filter(patPred)
//        val materials = materialReg.filter(matPred)
//
//        patterns.forEach { pattern ->
//            materials.forEach { material ->
//                permList.add(ArmorTrim(materialReg.wrapAsHolder(material), patternReg.wrapAsHolder(pattern)))
//            }
//        }
//
//        var j = 0
//        var k = 0
////        permList.sortByDescending { it.getColor()?.third }
//        permList.forEach { entry ->
//            armorReg.reversed().forEach { material ->
//                val x = blockPos.x + 0.5 - (if (row) k else (if (grid) j % patterns.size else j)) * 2.0
//                val y = blockPos.y + (if (row) 0.0 else (k % armorReg.size) * 3.0)
//                val z = blockPos.z + 0.5 + (if (grid) (j / patterns.size) * 5 else 0)
//                val stand = ArmorStand(world, x, y, z)
//                stand.yRot = 180.0f
//                stand.isNoBasePlate = true
//                stand.isNoGravity = true
//                stand.addTag("placed_with_trim_command")
//                if (items) {
//                    stand.setItemSlot(EquipmentSlot.MAINHAND, entry.material().value().ingredient.value().defaultInstance)
//                    stand.setItemSlot(EquipmentSlot.OFFHAND, entry.pattern().value().templateItem.value().defaultInstance)
//                }
//
//                EquipmentSlot.entries.forEach { slot ->
//                    ARMOR_TYPES[Pair.of(BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(material), slot)]?.let {
//                        val itemStack = ItemStack(it)
//                        itemStack.set(DataComponents.TRIM, entry)
//                        stand.setItemSlot(slot, itemStack)
//                    }
//                }
//                world.addFreshEntity(stand)
//                ++k
//            }
//            ++j
//        }
//
//        s.sendSuccess({ Component.literal("Armorstands with trimmed armor spawned around you") }, true)
        return 1
    }

    private fun ArmorTrim.getColor(): Triple<Int, Int, Int>? {
        val textColor = this.material().value().description.style.color ?: return null
        return Color(textColor.value).toHSL()
    }


 /*   private val ARMOR_TYPES = Util.make(Maps.newHashMap<Pair<Holder<ArmorMaterial>, EquipmentSlot>, Item>()) { map ->
        BuiltInRegistries.ITEM.filterIsInstance<ArmorItem>().filter { it.type.hasTrims() }
            .forEach { map[Pair.of(it.material, it.equipmentSlot)] = it }
    }*/
}
