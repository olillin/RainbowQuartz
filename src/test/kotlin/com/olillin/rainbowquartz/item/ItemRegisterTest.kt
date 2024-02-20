package com.olillin.rainbowquartz.item

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.olillin.rainbowquartz.RainbowQuartz
import com.olillin.rainbowquartz.craft.ShapedRecipe
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ItemRegisterTest {
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
    fun registerItem() {
        val item = ItemBuilder(key, Material.STICK).build()

        RainbowQuartz.itemManager.registerItem(item)

        assertEquals(item, RainbowQuartz.itemManager.getItem(key))
    }

    @Test
    fun registerRecipe() {
        val recipe = ShapedRecipe(" G ", "BGB", " R ")
                .setIngredient('G', Material.GOLD_INGOT)
                .setIngredient('B', Material.BLAZE_POWDER)
                .setIngredient('R', Material.BLAZE_ROD)
        val item = ItemBuilder(key, Material.GOLDEN_SWORD)
                .addRecipe(recipe)
                .build()
        RainbowQuartz.itemManager.registerItem(item)

        assertNotNull(server.getRecipe(recipe.key(item)))
    }
}