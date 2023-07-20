package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin

class MainMenu(override val viewer: HumanEntity, private val plugin: Plugin) : ImmutableMenu() {
    override val inventory: Inventory = Bukkit.createInventory(viewer, 9, Component.text("RainbowQuartz Menu"))

    init {
        if (viewer.hasPermission("rainbowquartz.editor")) {
            inventory.addItem(LinkItem.makeLink(
                "editor",
                Material.IRON_SWORD,
                Component.text("Item Editor").color(NamedTextColor.AQUA),
                listOf(Component.text("Create and edit items"))
            ))
        }
        inventory.setItem(8, LinkItem.CLOSE)
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        val key = event.linkKey
        event.whoClicked.sendMessage(Component.text("ID: $key"))

        if (key == "editor") {
            viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
            ItemEditorMenu(event.whoClicked, plugin).show()
        } else if (key == "close") {
            viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
            event.whoClicked.closeInventory(InventoryCloseEvent.Reason.PLAYER)
        }
    }
}