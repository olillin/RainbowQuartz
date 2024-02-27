package com.olillin.rainbowquartz.recipe.serialization

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.olillin.rainbowquartz.RainbowQuartz
import com.olillin.rainbowquartz.craft.CampfireRecipe
import com.olillin.rainbowquartz.craft.Ingredient
import com.olillin.rainbowquartz.craft.SmithingTransformRecipe
import org.bukkit.Material
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class SmithingTransformRecipeSerializationTest {
    @Test
    fun serialization() {
        val recipe = SmithingTransformRecipe(
            Ingredient(Material.CARROT),
            Ingredient(Material.NETHERITE_INGOT),
            Ingredient(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
        )
            .setAmount(2)
        val serialized = recipe.serialize()
        val deserialized = CampfireRecipe.deserialize(serialized)
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