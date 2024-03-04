package com.olillin.rainbowquartz.gui.menu

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.*
import org.bukkit.inventory.PlayerInventory

public abstract class ImmutableMenu : Menu() {
    @EventHandler(priority = EventPriority.LOWEST)
    public fun onClickImmutable(event: InventoryClickEvent) {
        event.isCancelled = event.clickedInventory != null && event.clickedInventory !is PlayerInventory
                || event.action == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || event.action == InventoryAction.COLLECT_TO_CURSOR
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public fun onDragImmutable(event: InventoryDragEvent) {
        if (event.rawSlots.intersect(0 until event.view.topInventory.size).isNotEmpty()) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public fun onCraftImmutable(event: CraftItemEvent) {
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public fun onEnchantItem(event: EnchantItemEvent) {
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public fun onSmithItem(event: SmithItemEvent) {
        event.isCancelled = true
    }
}