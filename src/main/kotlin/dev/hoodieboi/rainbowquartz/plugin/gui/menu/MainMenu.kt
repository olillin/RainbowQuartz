package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.KeyMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.inventory.Inventory

@KeyMenu
class MainMenu(override val viewer: HumanEntity) : ImmutableMenu() {
    override var inventory: Inventory = Bukkit.createInventory(viewer, 9, Component.text("RainbowQuartz Menu"))
    override val previousMenu = null
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

        if (key == "editor") {
            viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
            ItemEditorMenu(event.whoClicked, this).open()
        } else if (key == "close") {
            close()
        }
    }
}