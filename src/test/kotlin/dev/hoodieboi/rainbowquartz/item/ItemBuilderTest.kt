package dev.hoodieboi.rainbowquartz.item

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import dev.hoodieboi.rainbowquartz.RainbowQuartz
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemBuilderTest {
    lateinit private var server: ServerMock
    lateinit private var plugin: RainbowQuartz
    lateinit private var key: NamespacedKey

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(RainbowQuartz::class.java)
        key = NamespacedKey.fromString("foo:${UUID.randomUUID()}")!!
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun createItemFromMaterial() {
        val item = Item.ItemBuilder(key, Material.IRON_SWORD).build()
        assertEquals(item, Item(key, ItemStack(Material.IRON_SWORD, 1), ArrayList()))
    }

    @Test
    fun createItemFromItemStack() {
        val item = Item.ItemBuilder(key, ItemStack(Material.POTATO, 8)).build()
        assertEquals(item, Item(key, ItemStack(Material.POTATO, 8), ArrayList()))
    }

    @Test
    fun setNameComponent() {
        val item = Item.ItemBuilder(key, Material.IRON_SWORD)
                .setName(Component.text("Quartz Sword"))
                .build()

        val itemStack = ItemStack(Material.IRON_SWORD)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.displayName(Component.text("Quartz Sword").decoration(TextDecoration.ITALIC, false))
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun setNameComponentItalic() {
        val item = Item.ItemBuilder(key, Material.LEATHER_LEGGINGS)
            .setName(Component.text("Fancy Pants").decorate(TextDecoration.ITALIC))
            .build()

        val itemStack = ItemStack(Material.LEATHER_LEGGINGS)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.displayName(Component.text("Fancy Pants").decoration(TextDecoration.ITALIC, true))
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun setNameString() {
        val name = "Quartz Sword"
        val item = Item.ItemBuilder(key, Material.IRON_SWORD)
            .setName(name)
            .build()

        val itemStack = ItemStack(Material.IRON_SWORD)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.displayName(Component.text(name).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun addEnchantDefaultLevel() {
        val item = Item.ItemBuilder(key, Material.DIAMOND_SWORD)
            .addEnchant(Enchantment.FIRE_ASPECT)
            .build()

        val itemStack = ItemStack(Material.DIAMOND_SWORD)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addEnchant(Enchantment.FIRE_ASPECT, 1, true)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun addEnchantSuppliedLevel() {
        val item = Item.ItemBuilder(key, Material.DIAMOND_SWORD)
            .addEnchant(Enchantment.FIRE_ASPECT, 5)
            .build()

        val itemStack = ItemStack(Material.DIAMOND_SWORD)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addEnchant(Enchantment.FIRE_ASPECT, 5, true)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun addEnchantUnsupportedItem() {
        val item = Item.ItemBuilder(key, Material.STICK)
            .addEnchant(Enchantment.KNOCKBACK, 5)
            .build()

        val itemStack = ItemStack(Material.STICK)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addEnchant(Enchantment.KNOCKBACK, 5, true)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun removeEnchant() {
        val item = Item.ItemBuilder(key, Material.DIAMOND_SWORD)
            .addEnchant(Enchantment.FIRE_ASPECT)
            .addEnchant(Enchantment.DAMAGE_ALL, 2)
            .removeEnchant(Enchantment.FIRE_ASPECT)
            .build()

        val itemStack = ItemStack(Material.DIAMOND_SWORD)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addEnchant(Enchantment.DAMAGE_ALL, 2, true)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun addAttributeModifier() {
        val modifier = AttributeModifier(UUID.randomUUID().toString(), 2.0, AttributeModifier.Operation.ADD_NUMBER)
        val item = Item.ItemBuilder(key, Material.GOLDEN_CHESTPLATE)
            .addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier)
            .build()

        val itemStack = ItemStack(Material.GOLDEN_CHESTPLATE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun removeSpecificAttributeModifier() {
        val modifier1 = AttributeModifier(UUID.randomUUID().toString(), 2.0, AttributeModifier.Operation.ADD_NUMBER)
        val modifier2 = AttributeModifier(UUID.randomUUID().toString(), 3.0, AttributeModifier.Operation.ADD_NUMBER)
        val item = Item.ItemBuilder(key, Material.NETHERITE_CHESTPLATE)
            .addAttributeModifier(Attribute.GENERIC_ARMOR, modifier1)
            .addAttributeModifier(Attribute.GENERIC_ARMOR, modifier2)
            .removeAttributeModifier(Attribute.GENERIC_ARMOR, modifier1)
            .build()

        val itemStack = ItemStack(Material.NETHERITE_CHESTPLATE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier2)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun removeAttributeModifier() {
        val modifier1 = AttributeModifier(UUID.randomUUID().toString(), 2.0, AttributeModifier.Operation.ADD_NUMBER)
        val modifier2 = AttributeModifier(UUID.randomUUID().toString(), 3.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1)
        val modifier3 = AttributeModifier(UUID.randomUUID().toString(), 1.0, AttributeModifier.Operation.ADD_NUMBER)
        val item = Item.ItemBuilder(key, Material.NETHERITE_CHESTPLATE)
            .addAttributeModifier(Attribute.GENERIC_ARMOR, modifier1)
            .addAttributeModifier(Attribute.GENERIC_ARMOR, modifier2)
            .addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier3)
            .removeAttributeModifier(Attribute.GENERIC_ARMOR)
            .build()

        val itemStack = ItemStack(Material.NETHERITE_CHESTPLATE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier1)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun addItemFlags() {
        val item = Item.ItemBuilder(key, Material.GOLDEN_CHESTPLATE)
            .addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
            .build()

        val itemStack = ItemStack(Material.GOLDEN_CHESTPLATE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun removeItemFlags() {
        val item = Item.ItemBuilder(key, Material.GOLDEN_CHESTPLATE)
            .addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
            .removeItemFlags(ItemFlag.HIDE_ENCHANTS)
            .build()

        val itemStack = ItemStack(Material.GOLDEN_CHESTPLATE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
        itemStackMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun makeUnbreakable() {
        val item = Item.ItemBuilder(key, Material.GOLDEN_HOE)
            .setUnbreakable(true)
            .build()

        val itemStack = ItemStack(Material.GOLDEN_HOE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.isUnbreakable = true
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }

    @Test
    fun makeBreakable() {
        val item = Item.ItemBuilder(key, Material.GOLDEN_HOE)
            .setUnbreakable(true)
            .setUnbreakable(false)
            .build()

        val itemStack = ItemStack(Material.GOLDEN_HOE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.isUnbreakable = false
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(item.result, itemStack)
    }
}