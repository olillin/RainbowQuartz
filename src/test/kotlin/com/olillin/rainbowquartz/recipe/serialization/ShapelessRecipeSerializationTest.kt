package com.olillin.rainbowquartz.recipe.serialization

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.olillin.rainbowquartz.RainbowQuartz
import com.olillin.rainbowquartz.craft.Ingredient
import com.olillin.rainbowquartz.craft.ShapedRecipe
import com.olillin.rainbowquartz.craft.ShapelessRecipe
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

class ShapelessRecipeSerializationTest {
    @Test
    fun withGroup() {
        val recipe = ShapelessRecipe()
            .setAmount(2)
            .setGroup("foo")
            .addIngredient(Ingredient(Material.IRON_INGOT))
            .addIngredient(Ingredient(Material.STICK))
        val serialized = recipe.serialize()
        val deserialized = ShapelessRecipe.deserialize(serialized)
        assertEquals(recipe, deserialized)
    }

    @Test
    fun withoutGroup() {
        val recipe = ShapelessRecipe()
            .addIngredient(Ingredient(Material.IRON_INGOT))
            .addIngredient(Ingredient(Material.STICK))
        val serialized = recipe.serialize()
        val deserialized = ShapelessRecipe.deserialize(serialized)
        assertEquals(recipe, deserialized)
    }

    @Test
    fun tenIngredients() {
        val recipe = ShapelessRecipe()
            .addIngredient(Ingredient(Material.DIAMOND), 10)
        val serialized = recipe.serialize()
        val deserialized = ShapelessRecipe.deserialize(serialized)
        assertEquals(recipe, deserialized)
    }

    companion object {
        private lateinit var server: ServerMock
        private lateinit var plugin: RainbowQuartz

        @JvmStatic
        @AfterAll
        fun tearDown() {
            MockBukkit.unmock()
        }

        @JvmStatic
        @BeforeAll
        fun setUp() {
            server = MockBukkit.mock()
            plugin = MockBukkit.load(RainbowQuartz::class.java)
        }
    }
}