package com.olillin.rainbowquartz.item

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.olillin.rainbowquartz.RainbowQuartz
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.MemoryConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class SerializationTest {
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
    fun serializeItem() {
        val config = MemoryConfiguration()
        val item = ItemBuilder(key, Material.IRON_SWORD).build()
        var deserialized: Item? = null
        assertDoesNotThrow {
            config.set("item", item)
            deserialized = config.get("item") as Item
        }
        assertEquals(item, deserialized)
    }
}