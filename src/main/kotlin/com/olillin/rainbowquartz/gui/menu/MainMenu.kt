package com.olillin.rainbowquartz.gui.menu

import com.olillin.rainbowquartz.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.gui.LinkItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory

internal class MainMenu(override val viewer: HumanEntity) : ImmutableMenu() {
    override var inventory: Inventory = Bukkit.createInventory(viewer, 9, Component.text("RainbowQuartz Menu"))
    override val previousMenu = null

    @Suppress("UNUSED_PARAMETER")
    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        if (viewer.hasPermission("rainbowquartz.editor")) {
            inventory.addItem(
                LinkItem.makeLink(
                    "editor",
                    Material.IRON_SWORD,
                    Component.text("Item Editor").color(NamedTextColor.AQUA),
                    listOf(Component.text("Create and edit items"))
                )
            )
        }
        inventory.setItem(8, LinkItem.CLOSE)
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        val key = event.linkKey

        if (key == "editor") {
            viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
            ItemEditorMenu(event.whoClicked, this).open()
        } else if (key == "close") {
            close()
        }
    }
}