package dev.hoodieboi.rainbowquartz.event

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.item.rainbowQuartzId
import dev.hoodieboi.rainbowquartz.onlyIf
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ItemEventHandlerTest {
    private lateinit var server: ServerMock
    private lateinit var plugin: Plugin
    private lateinit var itemBuilder: ItemBuilder
    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(RainbowQuartz::class.java)
        itemBuilder = ItemBuilder(NamespacedKey(plugin, "cryo_diamond"), Material.DIAMOND)
                .setName(Component.text("Cryo Diamond").color(NamedTextColor.AQUA))
                .addEnchant(Enchantment.DURABILITY)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS)
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun breakItemTest() {
        val item = itemBuilder.build()
        // Register handler
        val predicate = EventPredicate<PlayerItemBreakEvent> { event ->
            event.brokenItem onlyIf { it.itemMeta.rainbowQuartzId == item.key }
        }
        var hasRun = false
        val handler = EventHandler<PlayerEvent> { _, _ ->
            hasRun = true
        }
        item.addEventHandler(PlayerItemBreakEvent::class.java, predicate, handler)
        RainbowQuartz.itemManager.registerItem(item)

        // Trigger event
        val player = server.addPlayer()
        val event = PlayerItemBreakEvent(player, ItemStack(item.item))
        server.pluginManager.callEvent(event)

        assert(hasRun)
    }

    @Test
    fun breakItemTestWithBuilder() {
        // Register handler
        val predicate = EventPredicate<PlayerItemBreakEvent> { event ->
            event.brokenItem onlyIf { it.itemMeta.rainbowQuartzId == itemBuilder.key }
        }
        var hasRun = false
        val handler = EventHandler<PlayerEvent> { _, _ ->
            hasRun = true
        }
        itemBuilder.addEventHandler(PlayerItemBreakEvent::class.java, predicate, handler)
        val item = itemBuilder.build()
        RainbowQuartz.itemManager.registerItem(item)

        // Trigger event
        val player = server.addPlayer()
        val event = PlayerItemBreakEvent(player, ItemStack(item.item))
        server.pluginManager.callEvent(event)

        assert(hasRun)
    }
}