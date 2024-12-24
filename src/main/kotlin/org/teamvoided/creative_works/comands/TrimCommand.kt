package org.teamvoided.creative_works.comands

import com.google.common.collect.Maps
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.datafixers.util.Pair
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.*
import net.minecraft.item.trim.ArmorTrimMaterial
import net.minecraft.item.trim.ArmorTrimPattern
import net.minecraft.item.trim.ArmorTrimPermutation
import net.minecraft.registry.Holder
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
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

    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val trimNode = literal("trim").buildChildOf(dispatcher.root)

        literal("items").executes(TrimCommand::toggleItems).buildChildOf(trimNode)
        literal("grid").executes(TrimCommand::toggleGrid).buildChildOf(trimNode)


        val trimNodeBlockPodArg = argument("pos", BlockPosArgumentType.blockPos()).build()
        trimNode.addChild(trimNodeBlockPodArg)

        literal("all")
            .executes { all(it, BlockPosArgumentType.getBlockPos(it, "pos")) }
            .buildChildOf(trimNodeBlockPodArg)


        val patNode = literal("pattern").build()
        trimNodeBlockPodArg.addChild(patNode)
        val patNodePatArg = patternArg("pattern").executes {
            pat(
                it, PatterArgumentType.getPattern(it, "pattern"),
                BlockPosArgumentType.getBlockPos(it, "pos")
            )
        }.build()
        patNode.addChild(patNodePatArg)

        val matNode = literal("material").build()
        trimNodeBlockPodArg.addChild(matNode)
        materialArg("material").executes {
            mat(it, MaterialArgumentType.getMaterial(it, "material"), BlockPosArgumentType.getBlockPos(it, "pos"))
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
                    BlockPosArgumentType.getBlockPos(it, "pos")
                )
            }
            .build()
        bothNodeMatArg.addChild(bothNodePatArg)
    }

    private fun toggleItems(c: CommandContext<ServerCommandSource>): Int {
        val src = c.source
        items = !items
        src.sendSystemMessage(Text.translatable("Items toggled! [%s]", items))
        return 1
    }

    private fun toggleGrid(c: CommandContext<ServerCommandSource>): Int {
        val src = c.source
        grid = !grid
        src.sendSystemMessage(Text.translatable("Grid toggled! [%s]", grid))
        return 1
    }

    private fun all(c: CommandContext<ServerCommandSource>, pos: BlockPos): Int =
        spawnArmorTrims(c.source, { true }, { true }, pos, false)

    private fun pat(c: CommandContext<ServerCommandSource>, pat: ArmorTrimPattern, pos: BlockPos): Int =
        spawnArmorTrims(c.source, { it == pat }, { true }, pos, false)


    private fun mat(c: CommandContext<ServerCommandSource>, mat: ArmorTrimMaterial, pos: BlockPos): Int =
        spawnArmorTrims(c.source, { true }, { it == mat }, pos, false)

    private fun both(
        c: CommandContext<ServerCommandSource>, mat: ArmorTrimMaterial, pat: ArmorTrimPattern, pos: BlockPos
    ): Int =
        spawnArmorTrims(c.source, { it == pat }, { it == mat }, pos, true)


    private fun spawnArmorTrims(
        s: ServerCommandSource, patPred: (ArmorTrimPattern) -> Boolean, matPred: (ArmorTrimMaterial) -> Boolean,
        blockPos: BlockPos, row: Boolean
    ): Int {
        val world = s.world
        val permList = mutableListOf<ArmorTrimPermutation>()
        val armorReg = Registries.ARMOR_MATERIAL.filter { it != ArmorMaterials.ARMADILLO.value() }
        val patternReg = world.registryManager.get(RegistryKeys.TRIM_PATTERN)
        val materialReg = world.registryManager.get(RegistryKeys.TRIM_MATERIAL)

        val patterns = patternReg.filter(patPred)
        val materials = materialReg.filter(matPred)

        patterns.forEach { pattern ->
            materials.forEach { material ->
                permList.add(ArmorTrimPermutation(materialReg.wrapAsHolder(material), patternReg.wrapAsHolder(pattern)))
            }
        }

        var j = 0
        var k = 0
//        permList.sortByDescending { it.getColor()?.third }
        permList.forEach { entry ->
            armorReg.reversed().forEach { material ->
                val x = blockPos.x + 0.5 - (if (row) k else (if (grid) j % patterns.size else j)) * 2.0
                val y = blockPos.y + (if (row) 0.0 else (k % armorReg.size) * 3.0)
                val z = blockPos.z + 0.5 + (if (grid) (j / patterns.size) * 5 else 0)
                val stand = ArmorStandEntity(world, x, y, z)
                stand.yaw = 180.0f
                stand.setHideBasePlate(true)
                stand.setNoGravity(true)
                stand.addScoreboardTag("placed_with_trim_command")
                if (items) {
                    stand.equipStack(EquipmentSlot.MAINHAND, entry.material.value().ingredient.value().defaultStack)
                    stand.equipStack(EquipmentSlot.OFFHAND, entry.pattern.value().templateItem.value().defaultStack)
                }

                EquipmentSlot.entries.forEach { slot ->
                    ARMOR_TYPES[Pair.of(Registries.ARMOR_MATERIAL.wrapAsHolder(material), slot)]?.let {
                        val itemStack = ItemStack(it)
                        itemStack.set(DataComponentTypes.TRIM, entry)
                        stand.equipStack(slot, itemStack)
                    }
                }
                world.spawnEntity(stand)
                ++k
            }
            ++j
        }

        s.sendFeedback({ Text.literal("Armorstands with trimmed armor spawned around you") }, true)
        return 1
    }

    private fun ArmorTrimPermutation.getColor(): Triple<Int, Int, Int>? {
        val textColor = this.material.value().description.style.color ?: return null
        return Color(textColor.rgb).toHSL()
    }



    private val ARMOR_TYPES = Util.make(Maps.newHashMap<Pair<Holder<ArmorMaterial>, EquipmentSlot>, Item>()) { map ->
        Registries.ITEM.filterIsInstance<ArmorItem>().filter { it.armorSlot.supportsTrim() }.forEach {
            map[Pair.of(it.material, it.preferredSlot)] = it
        }
    }
}
