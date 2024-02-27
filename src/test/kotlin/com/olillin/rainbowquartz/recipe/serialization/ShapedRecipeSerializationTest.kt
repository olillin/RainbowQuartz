package com.olillin.rainbowquartz.recipe.serialization

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.olillin.rainbowquartz.RainbowQuartz
import com.olillin.rainbowquartz.craft.Ingredient
import com.olillin.rainbowquartz.craft.ShapedRecipe
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ShapedRecipeSerializationTest {
    @Test
    fun fullGrid() {
        val recipe = ShapedRecipe("III", " S ", " S ")
            .setGroup("pickaxes")
            .setIngredient('I', Ingredient(Material.IRON_INGOT))
            .setIngredient('S', Ingredient(Material.STICK))
        val serialized = recipe.serialize()
        val deserialized = ShapedRecipe.deserialize(serialized)
        assertEquals(recipe, deserialized)
    }

    @Test
    fun smallGrid() {
        val recipe = ShapedRecipe("PC", "C ")
            .setGroup("FOOD")
            .setAmount(8)
            .setIngredient('P', Ingredient(Material.PAPER))
            .setIngredient('C', Ingredient.fromItemStack(ItemStack(Material.CHICKEN)))
        val serialized = recipe.serialize()
        val deserialized = ShapedRecipe.deserialize(serialized)

        val expected = ShapedRecipe("PC ", "C  ", "   ")
            .setGroup("FOOD")
            .setAmount(8)
            .setIngredient('P', Ingredient(Material.PAPER))
            .setIngredient('C', Ingredient.fromItemStack(ItemStack(Material.CHICKEN)))
        assertEquals(expected, deserialized)
    }

    @Test
    fun singleItem() {
        val recipe = ShapedRecipe("B")
            .setAmount(9)
            .setIngredient('B', Ingredient(Material.IRON_BLOCK))
        val serialized = recipe.serialize()
        val deserialized = ShapedRecipe.deserialize(serialized)

        val expected = ShapedRecipe("B  ", "   ", "   ")
            .setAmount(9)
            .setIngredient('B', Ingredient(Material.IRON_BLOCK))
        assertEquals(expected, deserialized)
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