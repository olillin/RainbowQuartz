package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.inventory.Recipe

interface Recipe {
    fun toBukkitRecipe(item: Item): Recipe
}