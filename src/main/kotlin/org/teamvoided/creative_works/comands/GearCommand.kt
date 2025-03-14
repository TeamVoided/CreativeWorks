package org.teamvoided.creative_works.comands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType.getBool
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.context.CommandContext
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ItemEnchantmentsComponent
import net.minecraft.component.type.UnbreakableComponent
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ArmorItem.ArmorSlot
import net.minecraft.item.ItemStack
import net.minecraft.item.Items.*
import net.minecraft.registry.Holder
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.StringIdentifiable
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions
import org.teamvoided.creative_works.util.buildChildOf
import org.teamvoided.creative_works.util.message

object GearCommand {
    fun init(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = literal("gear").executes(::exe).buildChildOf(dispatcher.root)
        val type = argument("type", word())
            .suggests { _, builder -> builder.listSuggestions(GearType.entries.map { it.asString() }) }
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
        ctx: CommandContext<ServerCommandSource>,
        type: GearType? = null, enchanted: Boolean = false, unbreakable: Boolean = false
    ): Int {
        val src = ctx.source ?: return 0
        val world = src.world ?: return 0
        val player = src.player ?: return 0


        val doEverything = (type == null || type == GearType.ALL)
        var message = ""

        val map = mutableMapOf<ArmorSlot, ItemStack>()
        val items = mutableListOf<ItemStack>()

        if (type == GearType.ARMOR || doEverything) {
            message = "Armor"
            val helmet = NETHERITE_HELMET.defaultStack
            val chestplate = NETHERITE_CHESTPLATE.defaultStack
            val leggings = NETHERITE_LEGGINGS.defaultStack
            val boots = NETHERITE_BOOTS.defaultStack
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
            map[ArmorSlot.HELMET] = helmet
            map[ArmorSlot.CHESTPLATE] = chestplate
            map[ArmorSlot.LEGGINGS] = leggings
            map[ArmorSlot.BOOTS] = boots

        }
        if (type == GearType.WEAPONS || doEverything) {
            message = "Weapons"
            val sword = NETHERITE_SWORD.defaultStack
            val bow = BOW.defaultStack
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
            val pickaxe = NETHERITE_PICKAXE.defaultStack
            val pickaxe2 = NETHERITE_PICKAXE.defaultStack
            val axe = NETHERITE_AXE.defaultStack
            val shovel = NETHERITE_SHOVEL.defaultStack
            val hoe = NETHERITE_HOE.defaultStack
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
            val crossbow = CROSSBOW.defaultStack
            val crossbow2 = CROSSBOW.defaultStack
            val trident = TRIDENT.defaultStack
            val trident2 = TRIDENT.defaultStack
            val mace = MACE.defaultStack
            val mace2 = MACE.defaultStack
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
            world.spawnEntity(entity)
        }

        map.forEach { (slot, stack) ->
            val real = player.getEquippedStack(slot.equipmentSlot)
            if (real.isEmpty) player.equipStack(slot.equipmentSlot, stack)
        }

        src.message("Equipped $message!")

        return 1
    }

    enum class GearType : StringIdentifiable {
        ALL, ARMOR, TOOLS, WEAPONS, ALT_WEAPONS;

        override fun asString(): String = name.lowercase()

        companion object {
            fun get(name: String) = GearType.valueOf(name.uppercase())
        }
    }

    fun ItemStack.unbreakable(showInToolTip: Boolean = true) =
        this.set(DataComponentTypes.UNBREAKABLE, UnbreakableComponent(showInToolTip))

    fun ItemStack.addEnchantment(dyn: ServerWorld, enchantment: RegistryKey<Enchantment>, level: Int): ItemStack =
        this.addEnchantment(
            dyn.registryManager.getLookupOrThrow(RegistryKeys.ENCHANTMENT).getHolderOrThrow(enchantment), level
        )

    fun ItemStack.addEnchantment(enchantment: Holder<Enchantment>, level: Int): ItemStack {
        val builder = ItemEnchantmentsComponent.Builder(this.get(DataComponentTypes.ENCHANTMENTS))
        builder.set(enchantment, level)
        this.set(DataComponentTypes.ENCHANTMENTS, builder.build())
        return this
    }
}
