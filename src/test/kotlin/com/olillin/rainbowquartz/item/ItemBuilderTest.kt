package com.olillin.rainbowquartz.item

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.olillin.rainbowquartz.RainbowQuartz
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
    private lateinit var server: ServerMock
    private lateinit var plugin: RainbowQuartz
    private lateinit var key: NamespacedKey

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
        val item = ItemBuilder(key, Material.IRON_SWORD).build()
        assertEquals(Item(key, ItemStack(Material.IRON_SWORD, 1), ArrayList()), item)
    }

    @Test
    fun createItemFromItemStack() {
        val item = ItemBuilder(key, ItemStack(Material.POTATO, 8)).build()
        assertEquals(Item(key, ItemStack(Material.POTATO, 8), ArrayList()), item)
    }

    @Test
    fun rainbowQuartzKey() {
        val item = ItemStack(Material.QUARTZ)
        val meta = item.itemMeta
        meta.rainbowQuartzId = key

        assertEquals(key, meta.rainbowQuartzId)
    }

    @Test
    fun setNameComponent() {
        val item = ItemBuilder(key, Material.IRON_SWORD)
                .setName(Component.text("Quartz Sword"))
                .build()

        val expected = ItemStack(Material.IRON_SWORD).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("Quartz Sword").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                rainbowQuartzId = key
            }
        }

        assertEquals(expected.itemMeta.displayName(), item.item.itemMeta.displayName())
    }

    @Test
    fun setNameComponentItalic() {
        val item = ItemBuilder(key, Material.LEATHER_LEGGINGS)
                .setName(Component.text("Fancy Pants").decorate(TextDecoration.ITALIC))
                .build()

        val expected = ItemStack(Material.LEATHER_LEGGINGS).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("Fancy Pants").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, true))
                rainbowQuartzId = key
            }
        }

        assertEquals(expected.itemMeta.displayName(), item.item.itemMeta.displayName())
    }

    @Test
    fun setNameString() {
        val name = "Quartz Sword"
        val item = ItemBuilder(key, Material.IRON_SWORD)
            .setName(name)
            .build()

        val expected = ItemStack(Material.IRON_SWORD).apply {
            itemMeta = itemMeta.apply {
                displayName(ItemBuilder.formatName(Component.text(name)))
                rainbowQuartzId = key
            }
        }

        assertEquals(expected.itemMeta.displayName(), item.item.itemMeta.displayName())
    }

    @Test
    fun setLore() {
        val firstLore = listOf(Component.text("Line 1"), Component.text("Line 2"), Component.text("Line 3"))
        val secondLore = listOf(Component.text("LoreB"), Component.text("LoreB"))
        val item = ItemBuilder(key, Material.IRON_SWORD)
                .setLore(firstLore)
                .setLore(secondLore)

        val expectedLore = secondLore.map { ItemBuilder.formatLore(it)!! }
        assertEquals(expectedLore, item.getLore())
    }

    @Test
    fun addLore() {
        val firstLore = listOf(Component.text("Line 1"))
        val item = ItemBuilder(key, Material.IRON_SWORD)
                .setLore(firstLore)
                .addLore("Line 2")

        val expectedLore = listOf("Line 1", "Line 2").map { ItemBuilder.formatLore(Component.text(it))!! }
        assertEquals(expectedLore, item.getLore())
    }

    @Test
    fun addLoreAtIndex() {
        val firstLore = listOf(Component.text("Line 2"))
        val item = ItemBuilder(key, Material.IRON_SWORD)
                .setLore(firstLore)
                .addLore(0, "Line 1")

        val expectedLore = listOf("Line 1", "Line 2").map { ItemBuilder.formatLore(Component.text(it))!! }
        assertEquals(expectedLore, item.getLore())
    }

    @Test
    fun addEnchantDefaultLevel() {
        val item = ItemBuilder(key, Material.DIAMOND_SWORD)
            .addEnchant(Enchantment.FIRE_ASPECT)
            .build()

        val itemStack = ItemStack(Material.DIAMOND_SWORD)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addEnchant(Enchantment.FIRE_ASPECT, 1, true)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(itemStack, item.item)
    }

    @Test
    fun addEnchantSuppliedLevel() {
        val item = ItemBuilder(key, Material.DIAMOND_SWORD)
            .addEnchant(Enchantment.FIRE_ASPECT, 5)
            .build()

        val itemStack = ItemStack(Material.DIAMOND_SWORD)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addEnchant(Enchantment.FIRE_ASPECT, 5, true)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(itemStack, item.item)
    }

    @Test
    fun addEnchantUnsupportedItem() {
        val item = ItemBuilder(key, Material.STICK)
            .addEnchant(Enchantment.KNOCKBACK, 5)
            .build()

        val itemStack = ItemStack(Material.STICK)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addEnchant(Enchantment.KNOCKBACK, 5, true)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(itemStack, item.item)
    }

    @Test
    fun removeEnchant() {
        val item = ItemBuilder(key, Material.DIAMOND_SWORD)
            .addEnchant(Enchantment.FIRE_ASPECT)
            .addEnchant(Enchantment.DAMAGE_ALL, 2)
            .removeEnchant(Enchantment.FIRE_ASPECT)
            .build()

        val itemStack = ItemStack(Material.DIAMOND_SWORD)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addEnchant(Enchantment.DAMAGE_ALL, 2, true)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(itemStack, item.item)
    }

    @Test
    fun addAttributeModifier() {
        val modifier = AttributeModifier(UUID.randomUUID().toString(), 2.0, AttributeModifier.Operation.ADD_NUMBER)
        val item = ItemBuilder(key, Material.GOLDEN_CHESTPLATE)
            .addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier)
            .build()

        val itemStack = ItemStack(Material.GOLDEN_CHESTPLATE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(itemStack, item.item)
    }

    @Test
    fun removeSpecificAttributeModifier() {
        val modifier1 = AttributeModifier(UUID.randomUUID().toString(), 2.0, AttributeModifier.Operation.ADD_NUMBER)
        val modifier2 = AttributeModifier(UUID.randomUUID().toString(), 3.0, AttributeModifier.Operation.ADD_NUMBER)
        val item = ItemBuilder(key, Material.NETHERITE_CHESTPLATE)
            .addAttributeModifier(Attribute.GENERIC_ARMOR, modifier1)
            .addAttributeModifier(Attribute.GENERIC_ARMOR, modifier2)
            .removeAttributeModifier(Attribute.GENERIC_ARMOR, modifier1)
            .build()

        val itemStack = ItemStack(Material.NETHERITE_CHESTPLATE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier2)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(itemStack, item.item)
    }

    @Test
    fun removeAttributeModifier() {
        val modifier1 = AttributeModifier(UUID.randomUUID().toString(), 2.0, AttributeModifier.Operation.ADD_NUMBER)
        val modifier2 = AttributeModifier(UUID.randomUUID().toString(), 3.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1)
        val modifier3 = AttributeModifier(UUID.randomUUID().toString(), 1.0, AttributeModifier.Operation.ADD_NUMBER)
        val item = ItemBuilder(key, Material.NETHERITE_CHESTPLATE)
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

        assertEquals(itemStack, item.item)
    }

    @Test
    fun addItemFlags() {
        val item = ItemBuilder(key, Material.GOLDEN_CHESTPLATE)
            .addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
            .build()

        val itemStack = ItemStack(Material.GOLDEN_CHESTPLATE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(itemStack, item.item)
    }

    @Test
    fun removeItemFlags() {
        val item = ItemBuilder(key, Material.GOLDEN_CHESTPLATE)
            .addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
            .removeItemFlags(ItemFlag.HIDE_ENCHANTS)
            .build()

        val itemStack = ItemStack(Material.GOLDEN_CHESTPLATE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
        itemStackMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS)
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(itemStack, item.item)
    }

    @Test
    fun makeUnbreakable() {
        val item = ItemBuilder(key, Material.GOLDEN_HOE)
            .setUnbreakable(true)
            .build()

        val itemStack = ItemStack(Material.GOLDEN_HOE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.isUnbreakable = true
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(itemStack, item.item)
    }

    @Test
    fun makeBreakable() {
        val item = ItemBuilder(key, Material.GOLDEN_HOE)
            .setUnbreakable(true)
            .setUnbreakable(false)
            .build()

        val itemStack = ItemStack(Material.GOLDEN_HOE)
        val itemStackMeta = itemStack.itemMeta
        itemStackMeta.isUnbreakable = false
        itemStackMeta.rainbowQuartzId = key
        itemStack.itemMeta = itemStackMeta

        assertEquals(itemStack, item.item)
    }
}