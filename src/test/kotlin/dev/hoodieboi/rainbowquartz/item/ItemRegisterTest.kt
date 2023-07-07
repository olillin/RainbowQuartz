package dev.hoodieboi.rainbowquartz.item

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import dev.hoodieboi.rainbowquartz.RainbowQuartz
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class ItemRegisterTest {
    lateinit private var server: ServerMock
    lateinit private var plugin: RainbowQuartz
    lateinit private var key: NamespacedKey

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
        val item = Item.ItemBuilder(key, Material.STICK).build()

        RainbowQuartz.itemManager.registerItem(item)

        assertEquals(RainbowQuartz.itemManager.getItem(key), item)
    }
}