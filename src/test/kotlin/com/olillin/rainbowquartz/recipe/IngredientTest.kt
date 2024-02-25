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
    private val material: Material = Material.IRON_INGOT
    private val materialIngredient: Ingredient = Ingredient(material)

    private val stack: ItemStack = ItemStack(material).apply {
        val meta = itemMeta
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

        itemMeta = meta
    }
    private val stackIngredient: Ingredient = Ingredient.fromItemStack(stack)

    private val item: Item = ItemBuilder(
        NamespacedKey("foo", "giant_steel_ingot"), Material.IRON_INGOT
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
    private val itemIngredient: Ingredient = Ingredient.fromItem(item)

    @Test
    fun stackIngredientMatchesStack() {
        val ingredient = stackIngredient
        val stack = stack

        assertTrue(ingredient.test(stack))
    }

    @Test
    fun materialIngredientMatchesStackWithoutMeta() {
        val ingredient = materialIngredient
        val stack = ItemStack(material, 7)

        assertTrue(ingredient.test(stack))
    }

    @Test
    fun materialIngredientDoesNotMatchStackWithMeta() {
        val ingredient = materialIngredient
        val stack = stack

        assertFalse(ingredient.test(stack))
    }

    @Test
    fun stackIngredientEqualsSelf() {
        val a = stackIngredient.clone()
        val b = stackIngredient.clone()

        assertEquals(a, b)
    }

    @Test
    fun itemIngredientEqualsSelf() {
        val a = itemIngredient.clone()
        val b = itemIngredient.clone()

        assertEquals(a, b)
    }

    @Test
    fun materialIngredientEqualsSelf() {
        val a = materialIngredient.clone()
        val b = materialIngredient.clone()

        assertEquals(a, b)
    }

    @Test
    fun itemIngredientSerialization() {
        val ingredient = itemIngredient
        val deserialized = Ingredient.deserialize(ingredient.serialize())

        assertEquals(ingredient, deserialized)
    }

    @Test
    fun stackIngredientSerialization() {
        val ingredient = stackIngredient
        val deserialized = Ingredient.deserialize(ingredient.serialize())

        assertEquals(ingredient, deserialized)
    }

    @Test
    fun materialIngredientSerialization() {
        val ingredient = materialIngredient
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