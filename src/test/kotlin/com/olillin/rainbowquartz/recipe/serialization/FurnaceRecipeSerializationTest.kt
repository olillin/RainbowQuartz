package com.olillin.rainbowquartz.recipe.serialization

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.olillin.rainbowquartz.RainbowQuartz
import com.olillin.rainbowquartz.craft.FurnaceRecipe
import com.olillin.rainbowquartz.craft.Ingredient
import org.bukkit.Material
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class FurnaceRecipeSerializationTest {
    @Test
    fun serialization() {
        val recipe = FurnaceRecipe(Ingredient(Material.NETHERITE_BLOCK))
            .setGroup("foo")
            .setExp(3.14f)
            .setCookTime(50)
            .setAmount(3)
        val serialized = recipe.serialize()
        val deserialized = FurnaceRecipe.deserialize(serialized)
        Assertions.assertEquals(recipe, deserialized)
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