package com.olillin.rainbowquartz.event

import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

fun interface EventHandler<in T : Event> {
    fun onEvent(item: ItemStack, event: T)
}