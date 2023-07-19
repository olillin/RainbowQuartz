package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.item.Item
import dev.hoodieboi.rainbowquartz.item.rainbowQuartzId
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.Plugin

class MainMenu(val viewer: HumanEntity, private val plugin: Plugin) : Menu() {
    override val inventory: Inventory
    init {
        inventory = Bukkit.createInventory(viewer, 9, Component.text("RainbowQuartz Menu"))

        if (viewer.hasPermission("rainbowquartz.editor")) {
            inventory.setItem(0, Item.ItemBuilder(NamespacedKey(plugin, "editor"), Material.IRON_SWORD)
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

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        if (event.currentItem == null) return
        // Ignore player inventory
        if (event.clickedInventory is PlayerInventory) {
            // Allow player inventory manipulation
            if (!event.isShiftClick) {
                event.isCancelled = false
            }
            return
        }

        val id = event.currentItem?.itemMeta?.rainbowQuartzId?.key ?: return
        event.whoClicked.sendMessage(Component.text("ID: $id"))

        if (id == "editor") {
            ItemEditorMenu(event.whoClicked, plugin)
        } else if (id == "close") {
            event.whoClicked.closeInventory(InventoryCloseEvent.Reason.PLAYER)
        }
    }

    @EventHandler
    fun onDrag(event: InventoryDragEvent) {
        // Cancel if changed slots include top inventory
        if (event.inventorySlots.intersect(0 until event.view.topInventory.size).isNotEmpty()) {
            event.isCancelled = true
        }
    }
}