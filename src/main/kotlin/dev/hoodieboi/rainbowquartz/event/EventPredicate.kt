package dev.hoodieboi.rainbowquartz.event

import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

fun interface EventPredicate<in T : Event> {
    fun getItem(event : T) : ItemStack?
}