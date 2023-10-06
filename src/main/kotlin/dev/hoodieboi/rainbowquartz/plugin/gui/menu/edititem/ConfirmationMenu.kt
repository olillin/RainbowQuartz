package dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem

import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.ImmutableMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory

class ConfirmationMenu(
    override val viewer: HumanEntity,
    private val callback: (Boolean) -> Unit,
    private val message: String = "Are you sure?",
    override val previousMenu: Menu?
) : ImmutableMenu() {
    override var inventory: Inventory = Bukkit.createInventory(viewer, 9, Component.text(message))

    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(4, LinkItem.makeLink(
            "yes",
            Material.LIME_CONCRETE,
            Component.text("Yes").color(NamedTextColor.GREEN),
        ))
        inventory.setItem(6, LinkItem.makeLink(
            "no",
            Material.RED_CONCRETE,
            Component.text("No").color(NamedTextColor.RED),
        ))
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "yes" -> {
                callback(true)
                back()
                inventory.close()
            }
            "no" -> {
                callback(false)
                back()
                inventory.close()
            }
        }
    }

    @EventHandler
    fun onExit(event: InventoryCloseEvent) {
        callback(false)
        inventory.close()
    }
}