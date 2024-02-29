package com.olillin.rainbowquartz.event

import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

public fun interface EventPredicate<in T : Event> {
    /** Returns the item to compare against a registered item */
    public fun getItem(event: T): ItemStack?
}