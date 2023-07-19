package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.item.Item
import dev.hoodieboi.rainbowquartz.item.rainbowQuartzId
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.*
import org.bukkit.event.inventory.InventoryCloseEvent.Reason
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class RenameItemMenu(val viewer: HumanEntity, val plugin: Plugin, private val builder: Item.ItemBuilder) : Menu() {
    override val inventory: Inventory
    init {
        inventory = Bukkit.createInventory(viewer, InventoryType.ANVIL, Component.text("Rename item"))
        inventory.setItem(0, ItemStack(builder.build().item))
    }

    @EventHandler
    fun onPrepareAnvil(event: PrepareAnvilEvent) {
        val name = parseName(event.inventory) ?: return
        event.inventory.result = builder.setName(name).build().item
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.slot == 2 && event.currentItem?.itemMeta?.rainbowQuartzId != null) {
            event.isCancelled = true
            val inventory = event.inventory as? AnvilInventory
            if (inventory == null) {
                event.whoClicked.sendMessage(Component.text("An unexpected error occurred when renaming item").color(NamedTextColor.RED))
                return
            }
            val name: Component? = parseName(inventory)
            if (name == null) {
                event.whoClicked.sendMessage(Component.text("An unexpected error occurred when renaming item").color(NamedTextColor.RED))
                return
            }
            EditItemMenu(event.whoClicked, plugin, builder.setName(name))
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.reason == Reason.OPEN_NEW) return
        EditItemMenu.sendCancelMessage(event.player)
    }

    private fun parseName(inventory: AnvilInventory): Component? {
        val text = inventory.renameText
        if (text == null) {
            val itemStack = ItemStack(Material.BARRIER)
            val meta = itemStack.itemMeta
            meta.displayName(Component.text("Must be different than current name").color(NamedTextColor.RED))
            itemStack.itemMeta = meta
            inventory.result = itemStack
            return null
        }
        return LegacyComponentSerializer.legacy('&').deserialize(text)
    }
}