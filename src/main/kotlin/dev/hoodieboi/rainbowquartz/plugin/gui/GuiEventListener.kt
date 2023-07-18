package dev.hoodieboi.rainbowquartz.plugin.gui

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GuiEventListener : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.slot == -999) return // Clicked outside of inventory

        for (menu in RainbowQuartz.menuManager.menus()) {
            if (menu.inView(event.view)) {
                menu.onClick(event)
                return
            }
        }
    }
}