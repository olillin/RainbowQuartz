package com.olillin.rainbowquartz.recipe

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.olillin.rainbowquartz.RainbowQuartz
import com.olillin.rainbowquartz.craft.Ingredient
import com.olillin.rainbowquartz.item.Item
import com.olillin.rainbowquartz.item.ItemBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.TextDecoration.ITALIC
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IngredientTest {
    fun getMaterial(): Material = Material.IRON_INGOT
    fun getStack(): ItemStack {
        val stack = ItemStack(Material.IRON_INGOT)
        val meta = stack.itemMeta
        meta.displayName(Component.text("Giant Steel Ingot").color(GRAY).decoration(ITALIC, false))
        meta.lore(
            listOf(
                Component.text("Hello world")
            )
        )
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
        meta.addAttributeModifier(
            Attribute.GENERIC_ATTACK_SPEED, AttributeModifier(
                UUID.fromString("af7c1fe6-d669-414e-b066-e9733f0de7a8"),
                "heavy",
                -0.1,
                AttributeModifier.Operation.ADD_NUMBER,
            )
        )
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)

        stack.itemMeta = meta
        return stack
    }

    fun getItem(): Item = ItemBuilder(
        NamespacedKey("foo", "giant_steel_ingot"),
        Material.IRON_INGOT
    ).setName(Component.text("Giant Steel Ingot").color(GRAY).decoration(ITALIC, false)).addLore(
        Component.text("Hello world")
    ).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).addAttributeModifier(
        Attribute.GENERIC_ATTACK_SPEED, AttributeModifier(
            UUID.fromString("af7c1fe6-d669-414e-b066-e9733f0de7a8"),
            "heavy",
            -0.1,
            AttributeModifier.Operation.ADD_NUMBER,
        )
    ).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES).build()

    @Test
    fun stackIngredientMatchesStack() {
        val ingredient = Ingredient.fromItemStack(getStack())
        val stack = getStack()

        assertTrue(ingredient.test(stack))
    }

    @Test
    fun stackIngredientDoesNotMatchItem() {
        val ingredient = Ingredient.fromItemStack(getStack())
        val item = getItem()

        assertFalse(ingredient.test(item))
    }

    @Test
    fun stackIngredientDoesNotMatchMaterial() {
        val ingredient = Ingredient.fromItemStack(getStack())
        val material = getMaterial()

        assertFalse(ingredient.test(material))
    }

    @Test
    fun itemIngredientMatchesItem() {
        val ingredient = Ingredient.fromItem(getItem())
        val item = getItem()

        assertTrue(ingredient.test(item))
    }

    @Test
    fun itemIngredientDoesNotMatchStack() {
        val ingredient = Ingredient.fromItem(getItem())
        val stack = getStack()

        assertFalse(ingredient.test(stack))
    }

    @Test
    fun itemIngredientDoesNotMatchMaterial() {
        val ingredient = Ingredient.fromItem(getItem())
        val material = getMaterial()

        assertFalse(ingredient.test(material))
    }

    @Test
    fun materialIngredientMatchesMaterial() {
        val ingredient = Ingredient(getMaterial())
        val material = getMaterial()

        assertTrue(ingredient.test(material))
    }

    @Test
    fun materialIngredientMatchesBlankStack() {
        val ingredient = Ingredient(getMaterial())
        val stack = ItemStack(getMaterial(), 7)

        assertTrue(ingredient.test(stack))
    }

    @Test
    fun materialIngredientDoesNotMatchStack() {
        val ingredient = Ingredient(getMaterial())
        val stack = getStack()

        assertFalse(ingredient.test(stack))
    }


    @Test
    fun materialIngredientDoesNotMatchBlankItem() {
        val ingredient = Ingredient(getMaterial())
        val item = Item(NamespacedKey("foo", "iron"), ItemStack(getMaterial(), 7))

        assertFalse(ingredient.test(item))
    }

    @Test
    fun materialIngredientDoesNotMatchItem() {
        val ingredient = Ingredient(getMaterial())
        val item = getItem()

        assertFalse(ingredient.test(item))
    }

    @Test
    fun stackIngredientEqualsSelf() {
        val a = Ingredient.fromItemStack(getStack())
        val b = Ingredient.fromItemStack(getStack())

        assertEquals(a, b)
    }

    @Test
    fun itemIngredientEqualsSelf() {
        val a = Ingredient.fromItem(getItem())
        val b = Ingredient.fromItem(getItem())

        assertEquals(a, b)
    }

    @Test
    fun materialIngredientEqualsSelf() {
        val a = Ingredient(getMaterial())
        val b = Ingredient(getMaterial())

        assertEquals(a, b)
    }

    @Test
    fun ingredientSerialization() {
        val ingredient = Ingredient.fromItem(getItem())
        val deserialized = Ingredient.deserialize(ingredient.serialize())

        assertEquals(ingredient, deserialized)
    }

    companion object {
        private lateinit var server: ServerMock
        private lateinit var plugin: RainbowQuartz

        @JvmStatic
        @BeforeAll
        fun setUp() {
            server = MockBukkit.mock()
            plugin = MockBukkit.load(RainbowQuartz::class.java)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            MockBukkit.unmock()
        }
    }
}