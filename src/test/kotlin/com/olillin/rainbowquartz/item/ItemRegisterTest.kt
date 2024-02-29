package com.olillin.rainbowquartz.item

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.olillin.rainbowquartz.RainbowQuartz
import com.olillin.rainbowquartz.craft.Ingredient
import com.olillin.rainbowquartz.craft.ShapedRecipe
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ItemRegisterTest {
    private lateinit var server: ServerMock
    private lateinit var plugin: RainbowQuartz
    private val id: NamespacedKey = NamespacedKey("foo", "example")

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
    fun registerItem() {
        val item = ItemBuilder(id, Material.STICK).build()

        RainbowQuartz.itemManager.registerItem(item)

        assertEquals(item, RainbowQuartz.itemManager.getItem(id))
    }

    @Test
    fun registerRecipe() {
        val recipe = ShapedRecipe(" G ", "BGB", " R ")
            .setIngredient('G', Ingredient(Material.GOLD_INGOT))
            .setIngredient('B', Ingredient(Material.BLAZE_POWDER))
            .setIngredient('R', Ingredient(Material.BLAZE_ROD))
        val item = ItemBuilder(id, Material.GOLDEN_SWORD)
            .addRecipe(recipe)
            .build()
        RainbowQuartz.itemManager.registerItem(item)

        assertNotNull(server.getRecipe(recipe.key(item)))
    }
}