package com.olillin.rainbowquartz.recipe.serialization

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.olillin.rainbowquartz.RainbowQuartz
import com.olillin.rainbowquartz.craft.ShapedRecipe
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RecipeSerializationTest {
    private lateinit var server: ServerMock
    private lateinit var plugin: RainbowQuartz

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(RainbowQuartz::class.java)
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun shapedRecipeFullGrid() {
        val recipe = ShapedRecipe("III", " S ", " S ")
            .setGroup("pickaxes")
            .setIngredient('I', Material.IRON_INGOT)
            .setIngredient('S', Material.STICK)
        val serialized = recipe.serialize()
        val deserialized = ShapedRecipe.deserialize(serialized)
        assertEquals(recipe, deserialized)
    }

    @Test
    fun shapedRecipeSmallGrid() {
        val recipe = ShapedRecipe("PC", "C ")
            .setGroup("FOOD")
            .setAmount(8)
            .setIngredient('P', Material.PAPER)
            .setIngredient('C', ItemStack(Material.CHICKEN))
        val serialized = recipe.serialize()
        val deserialized = ShapedRecipe.deserialize(serialized)
        assertEquals(recipe, deserialized)
    }

    @Test
    fun shapedRecipeSingleItem() {
        val recipe = ShapedRecipe("B")
            .setAmount(9)
            .setIngredient('B', Material.IRON_BLOCK)
        val serialized = recipe.serialize()
        val deserialized = ShapedRecipe.deserialize(serialized)
        assertEquals(recipe, deserialized)
    }
}