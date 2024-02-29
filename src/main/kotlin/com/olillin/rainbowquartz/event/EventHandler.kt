package com.olillin.rainbowquartz.event

import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

public fun interface EventHandler<in T : Event> {
    public fun onEvent(item: ItemStack, event: T)
}