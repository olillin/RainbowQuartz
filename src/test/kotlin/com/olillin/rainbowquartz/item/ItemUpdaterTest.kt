package com.olillin.rainbowquartz.item

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.olillin.rainbowquartz.RainbowQuartz
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import org.bukkit.Bukkit
import org.bukkit.Material.BLAZE_POWDER
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ItemUpdaterTest {
    private lateinit var itemBefore: Item
    private lateinit var itemAfter: Item
    private lateinit var outdatedStack: ItemStack
    private lateinit var updatedStack: ItemStack
    private val currentItem get() = RainbowQuartz.itemManager.getItem(id)!!

    @BeforeEach
    fun setUpEach() {
        RainbowQuartz.itemManager.unregisterItem(id)

        // Register item
        itemBefore = ItemBuilder(id, BLAZE_POWDER)
            .setName(Component.text("Gold Powder").color(GOLD))
            .build()
        RainbowQuartz.itemManager.registerItem(itemBefore)
        outdatedStack = currentItem.getItem()

        // Update item
        itemAfter = ItemBuilder(id, BLAZE_POWDER)
            .setName(Component.text("Bronze Powder").color(GOLD))
            .addEnchant(PROTECTION_ENVIRONMENTAL)
            .build()
        RainbowQuartz.itemManager.unregisterItem(id)
        RainbowQuartz.itemManager.registerItem(itemAfter)
        updatedStack = currentItem.getItem()
    }

    @Test
    fun updatedItemKeepsId() {
        assertEquals(outdatedStack.rainbowQuartzId, updatedStack.rainbowQuartzId)
    }

    @Test
    fun updatedItemKeepsAmount() {
        assertEquals(outdatedStack.amount, updatedStack.amount)
    }

    @Test
    fun updatedInventoryChangesAllItems() {
        val inventory = Bukkit.createInventory(null, 27)
        inventory.setItem(5, outdatedStack)
        inventory.setItem(7, outdatedStack)

        RainbowQuartz.itemUpdater.updateInventory(inventory)

        assertNotEquals(outdatedStack, inventory.getItem(5))
        assertNotEquals(outdatedStack, inventory.getItem(7))
    }

    companion object {
        lateinit var server: ServerMock
        lateinit var plugin: RainbowQuartz
        val id: NamespacedKey = NamespacedKey("foo", "example")

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