package com.olillin.rainbowquartz.gui.menu.edititem

import com.olillin.rainbowquartz.item.ItemBuilder
import com.olillin.rainbowquartz.gui.LinkItem
import com.olillin.rainbowquartz.gui.enchanted
import com.olillin.rainbowquartz.gui.menu.Menu
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent

internal class EditItemActionsMenu(viewer: HumanEntity, builder: ItemBuilder, override val previousMenu: Menu?) :
    EditItemMenu(viewer, builder) {
    @Suppress("UNUSED_PARAMETER")
    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(ACTIONS_SLOT, inventory.getItem(ACTIONS_SLOT)?.enchanted())

        inventory.setItem(
            3, LinkItem.makeLink(
                "event",
                Material.BEDROCK,
                "Event"
            )
        )
    }
}