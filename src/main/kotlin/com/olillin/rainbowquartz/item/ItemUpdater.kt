@file:Suppress("MemberVisibilityCanBePrivate")

package com.olillin.rainbowquartz.item

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

public class ItemUpdater internal constructor(private val itemManager: ItemManager) {
    public fun updateInventory(inventory: Inventory) {
        for (slot in 0 until inventory.size) {
            val stack = inventory.getItem(slot) ?: continue

            if (isItemOutdated(stack)) {
                val newStack = updateItem(stack)
                inventory.setItem(slot, newStack)
            }
        }
    }

    public fun isItemOutdated(stack: ItemStack): Boolean {
        val id = stack.rainbowQuartzId
            ?: throw IllegalArgumentException("Cannot check if item is outdated because it does not have an id")
        val item = itemManager.getItem(id)
            ?: throw IllegalStateException("Cannot check if item is outdated because it does not exist in the item manager. Item id is $id")
        return stack.itemHash != item.hashCode()
    }

    public fun updateItem(stack: ItemStack): ItemStack {
        val id = stack.rainbowQuartzId
            ?: throw IllegalArgumentException("Cannot update item because it does not have an id")
        val item = itemManager.getItem(id)
            ?: throw IllegalStateException("Cannot update item because it does not exist in the item manager. Item id is $id")
        return applyItem(stack, item)
    }

    public fun applyItem(stack: ItemStack, item: Item): ItemStack {
        val newStack = item.getItem()
        newStack.amount = stack.amount
        return newStack
    }
}