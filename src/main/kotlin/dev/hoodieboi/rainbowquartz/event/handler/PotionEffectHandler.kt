package dev.hoodieboi.rainbowquartz.event.handler

import org.bukkit.event.player.PlayerEvent
import org.bukkit.potion.PotionEffect

class PotionEffectHandler(val effect: PotionEffect) {
    fun onEvent(event: PlayerEvent) {
        event.player.addPotionEffect(effect)
    }
}