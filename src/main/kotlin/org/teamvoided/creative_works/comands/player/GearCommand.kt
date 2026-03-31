package org.teamvoided.creative_works.comands.player

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType.getBool
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items.*
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.item.equipment.ArmorType
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.message

object GearCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("gear").executes(GearCommand::exe).buildChildOf(dispatcher.root)
        val type = argument("type", word())
            .suggests { _, builder -> builder.listSuggestions(GearType.entries.map { it.serializedName }) }
            .executes { exe(it, GearType.get(getString(it, "type")), false) }
            .buildChildOf(root)
        val enchanted = argument("enchanted", BoolArgumentType.bool())
            .executes { exe(it, GearType.get(getString(it, "type")), getBool(it, "enchanted")) }
            .buildChildOf(type)
        argument("unbreakable", BoolArgumentType.bool()).executes {
            exe(it, GearType.get(getString(it, "type")), getBool(it, "enchanted"), getBool(it, "unbreakable"))
        }.buildChildOf(enchanted)
    }

    fun exe(
        ctx: CommandContext<CommandSourceStack>,
        type: GearType? = null, enchanted: Boolean = false, unbreakable: Boolean = false,
    ): Int {
        val src = ctx.source ?: return 0
        val world = src.level ?: return 0
        val player = src.player ?: return 0


        val doEverything = (type == null || type == GearType.ALL)
        var message = ""

        val map = mutableMapOf<ArmorType, ItemStack>()
        val items = mutableListOf<ItemStack>()

        if (type == GearType.ARMOR || doEverything) {
            message = "Armor"
            val helmet = NETHERITE_HELMET.defaultInstance
            val chestplate = NETHERITE_CHESTPLATE.defaultInstance
            val leggings = NETHERITE_LEGGINGS.defaultInstance
            val boots = NETHERITE_BOOTS.defaultInstance
            if (enchanted) {
                helmet.addEnchantment(world, Enchantments.AQUA_AFFINITY, 1)
                    .addEnchantment(world, Enchantments.RESPIRATION, 3)
                    .addEnchantment(world, Enchantments.PROTECTION, 4)
                chestplate.addEnchantment(world, Enchantments.PROTECTION, 4)
                leggings.addEnchantment(world, Enchantments.PROTECTION, 4)
                    .addEnchantment(world, Enchantments.SWIFT_SNEAK, 3)
                boots.addEnchantment(world, Enchantments.PROTECTION, 4)
                    .addEnchantment(world, Enchantments.FEATHER_FALLING, 3)
                    .addEnchantment(world, Enchantments.DEPTH_STRIDER, 3)
            }
            map[ArmorType.HELMET] = helmet
            map[ArmorType.CHESTPLATE] = chestplate
            map[ArmorType.LEGGINGS] = leggings
            map[ArmorType.BOOTS] = boots

        }
        if (type == GearType.WEAPONS || doEverything) {
            message = "Weapons"
            val sword = NETHERITE_SWORD.defaultInstance
            val bow = BOW.defaultInstance
            if (enchanted) {
                sword.addEnchantment(world, Enchantments.SHARPNESS, 5)
                    .addEnchantment(world, Enchantments.KNOCKBACK, 2)
                    .addEnchantment(world, Enchantments.SWEEPING_EDGE, 3)
                    .addEnchantment(world, Enchantments.LOOTING, 3)
                bow.addEnchantment(world, Enchantments.POWER, 5)
                    .addEnchantment(world, Enchantments.PUNCH, 2)
                    .addEnchantment(world, Enchantments.LOOTING, 3)
            }
            items.addAll(listOf(sword, bow))
        }
        if (type == GearType.TOOLS || doEverything) {
            message = "Tools"
            val pickaxe = NETHERITE_PICKAXE.defaultInstance
            val pickaxe2 = NETHERITE_PICKAXE.defaultInstance
            val axe = NETHERITE_AXE.defaultInstance
            val shovel = NETHERITE_SHOVEL.defaultInstance
            val hoe = NETHERITE_HOE.defaultInstance
            items.addAll(
                if (enchanted) {
                    pickaxe.addEnchantment(world, Enchantments.EFFICIENCY, 5)
                        .addEnchantment(world, Enchantments.SILK_TOUCH, 1)
                    pickaxe2.addEnchantment(world, Enchantments.EFFICIENCY, 5)
                        .addEnchantment(world, Enchantments.FORTUNE, 3)
                    axe.addEnchantment(world, Enchantments.EFFICIENCY, 5)
                    shovel.addEnchantment(world, Enchantments.EFFICIENCY, 5)
                    hoe.addEnchantment(world, Enchantments.EFFICIENCY, 5)
                    listOf(pickaxe, pickaxe2, axe, shovel, hoe)
                } else listOf(pickaxe, axe, shovel, hoe)
            )
        }
        if (type == GearType.ALT_WEAPONS || doEverything) {
            message = "Alternative Weapons"
            val crossbow = CROSSBOW.defaultInstance
            val crossbow2 = CROSSBOW.defaultInstance
            val trident = TRIDENT.defaultInstance
            val trident2 = TRIDENT.defaultInstance
            val mace = MACE.defaultInstance
            val mace2 = MACE.defaultInstance
            items.addAll(
                if (enchanted) {
                    crossbow.addEnchantment(world, Enchantments.QUICK_CHARGE, 3)
                        .addEnchantment(world, Enchantments.MULTISHOT, 1)
                    crossbow2.addEnchantment(world, Enchantments.QUICK_CHARGE, 3)
                        .addEnchantment(world, Enchantments.PIERCING, 4)
                    trident.addEnchantment(world, Enchantments.IMPALING, 5)
                        .addEnchantment(world, Enchantments.LOOTING, 3)
                        .addEnchantment(world, Enchantments.LOYALTY, 3)
                    trident2.addEnchantment(world, Enchantments.IMPALING, 5)
                        .addEnchantment(world, Enchantments.LOOTING, 3)
                        .addEnchantment(world, Enchantments.CHANNELING, 1)
                    mace.addEnchantment(world, Enchantments.LOOTING, 3)
                        .addEnchantment(world, Enchantments.DENSITY, 5)
                    mace2.addEnchantment(world, Enchantments.LOOTING, 3)
                        .addEnchantment(world, Enchantments.DENSITY, 5)
                        .addEnchantment(world, Enchantments.WIND_BURST, 3)
                    listOf(crossbow, crossbow2, trident, trident2, mace, mace2)
                } else listOf(crossbow, trident, mace)
            )
        }
        if (unbreakable) {
            map.mapValues { it.value.unbreakable() }
            items.map { it.unbreakable() }
        }
        if (enchanted) {
            map.mapValues { it.value.addEnchantment(world, Enchantments.UNBREAKING, 3) }
            items.map { it.addEnchantment(world, Enchantments.UNBREAKING, 3) }
        }

        if (doEverything) message = "Everything"

        items.forEach {
            val entity = ItemEntity(world, player.x, player.y, player.z, it, .0, .0, .0)
            world.addFreshEntity(entity)
        }

        map.forEach { (slot, stack) ->
            val real = player.getItemBySlot(slot.slot)
            if (real.isEmpty) player.setItemSlot(slot.slot, stack)
        }

        src.message("Equipped $message!")

        return 1
    }

    enum class GearType : StringRepresentable {
        ALL, ARMOR, TOOLS, WEAPONS, ALT_WEAPONS;

        override fun getSerializedName(): String = name.lowercase()

        companion object {
            fun get(name: String) = GearType.valueOf(name.uppercase())
        }
    }

    fun ItemStack.unbreakable(showInToolTip: Boolean = true) =
        this.set(DataComponents.UNBREAKABLE, net.minecraft.util.Unit.INSTANCE)

    fun ItemStack.addEnchantment(dyn: ServerLevel, enchantment: ResourceKey<Enchantment>, level: Int): ItemStack =
        this.addEnchantment(
            dyn.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment), level
        )

    fun ItemStack.addEnchantment(enchantment: Holder<Enchantment>, level: Int): ItemStack {
        val builder = ItemEnchantments.Mutable(getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY))
        builder.set(enchantment, level)
        this.set(DataComponents.ENCHANTMENTS, builder.toImmutable())
        return this
    }
}
