package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.item.Item
import dev.hoodieboi.rainbowquartz.item.rainbowQuartzId
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.Plugin

class MainMenu(val plugin: Plugin, override val id: Int) : Menu() {
    override fun showMenu(viewer: HumanEntity) {
        val inventory = Bukkit.createInventory(viewer, 9, title("RainbowQuartz Menu"))

        if (viewer.hasPermission("rainbowquartz.create")) {
            inventory.setItem(0, Item.ItemBuilder(NamespacedKey(plugin, "create"), Material.IRON_SWORD)
                .setName(Component.text("Item Editor").color(NamedTextColor.AQUA))
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                .addLore("Create and edit items")
                .build().item)
        }
        inventory.setItem(8, Item.ItemBuilder(NamespacedKey(plugin, "close"), Material.BARRIER)
            .setName(Component.text("Close menu").color(NamedTextColor.RED))
            .build().item)

        viewer.openInventory(inventory)
    }

    override fun onClick(event: InventoryClickEvent) {
        // Ignore player inventory
        if (event.clickedInventory is PlayerInventory) return

        event.whoClicked.sendMessage(Component.text("Clicked on slot ${event.slot}"))
        event.isCancelled = true

        val id = event.inventory.getItem(event.slot)?.itemMeta?.rainbowQuartzId?.key ?: return
        event.whoClicked.sendMessage("ID: $id")

        if (id == "create") {
            event.whoClicked.sendMessage(Component.text("Opening item editor...").color(NamedTextColor.YELLOW))
            RainbowQuartz.menuManager.ITEM_EDITOR.showMenu(event.whoClicked)
        } else if (id == "close") {
            event.whoClicked.closeInventory(InventoryCloseEvent.Reason.PLAYER)
        }
    }
}