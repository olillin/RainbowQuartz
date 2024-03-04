package com.olillin.rainbowquartz.gui

import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType.SlotType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

/**
 * This event occurs when a player clicks on a [LinkItem]
 */
public class InventoryClickLinkEvent(
    view: InventoryView,
    type: SlotType,
    slot: Int,
    click: ClickType,
    action: InventoryAction
) : InventoryClickEvent(view, type, slot, click, action) {

    public val linkKey: String
        get() = currentItem!!.itemMeta.linkKey!!

    init {
        if (currentItem?.itemMeta?.linkKey == null) {
            throw IllegalArgumentException("Event must have a clicked item with a link item key")
        }
    }

    override fun setCurrentItem(stack: ItemStack?) {
        if (stack == null) {
            throw IllegalArgumentException("Stack must not be null")
        }
        if (stack.itemMeta.linkKey == null) {
            throw IllegalArgumentException("Stack must have a link item key")
        }
        super.setCurrentItem(stack)
    }

    public companion object {
        public fun fromClickEvent(clickEvent: InventoryClickEvent): InventoryClickLinkEvent =
            InventoryClickLinkEvent(
                clickEvent.view,
                clickEvent.slotType,
                clickEvent.slot,
                clickEvent.click,
                clickEvent.action
            )
        public fun isLinkClick(clickEvent: InventoryClickEvent): Boolean {
            if (clickEvent.slotType == SlotType.OUTSIDE
                || clickEvent.currentItem == null
                || clickEvent.clickedInventory is PlayerInventory
            ) return false
            // Only normal left and right click allowed
            return when (clickEvent.click) {
                ClickType.LEFT, ClickType.RIGHT, ClickType.MIDDLE -> true
                else -> false
            }
        }
    }
}