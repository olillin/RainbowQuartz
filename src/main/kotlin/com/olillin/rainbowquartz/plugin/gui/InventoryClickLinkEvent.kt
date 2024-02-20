package com.olillin.rainbowquartz.plugin.gui

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
class InventoryClickLinkEvent(view: InventoryView, type: SlotType, slot: Int, click: ClickType, action: InventoryAction) : InventoryClickEvent(view, type, slot, click, action) {
    constructor(clickEvent: InventoryClickEvent) : this(clickEvent.view, clickEvent.slotType, clickEvent.slot, clickEvent.click, clickEvent.action)

    init {
        if (currentItem == null || LinkItem.getLinkKey(currentItem!!) == null) {
            throw IllegalArgumentException("Event must have a clicked item with a link item key")
        }
    }

    val linkKey: String
        get() = LinkItem.getLinkKey(currentItem!!)!!

    override fun setCurrentItem(stack: ItemStack?) {
        if (stack == null) {
            throw IllegalArgumentException("Stack must not be null")
        }
        if (LinkItem.getLinkKey(stack) == null) {
            throw IllegalArgumentException("Stack must have a link item key")
        }
        super.setCurrentItem(stack)
    }

    companion object {
        @JvmStatic
        fun isLinkClick(clickEvent: InventoryClickEvent): Boolean {
            if (clickEvent.slotType == SlotType.OUTSIDE
                    || clickEvent.currentItem == null
                    || clickEvent.clickedInventory is PlayerInventory) return false
            // Only normal left and right click allowed
            return when (clickEvent.click) {
                ClickType.LEFT, ClickType.RIGHT, ClickType.MIDDLE -> true
                else -> false
            }
        }
    }
}