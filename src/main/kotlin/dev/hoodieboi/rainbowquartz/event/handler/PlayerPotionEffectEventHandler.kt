package dev.hoodieboi.rainbowquartz.event.handler

import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect

/**
 * Gives a potion effect to the player of the event
 */
class PlayerPotionEffectEventHandler(val effect: PotionEffect) : EventHandler<PlayerEvent> {
    override fun onEvent(item: ItemStack, event: PlayerEvent) {
        event.player.addPotionEffect(effect)
    }
}