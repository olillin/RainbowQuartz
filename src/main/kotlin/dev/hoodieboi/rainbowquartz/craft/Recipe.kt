package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item

interface Recipe {
    fun toBukkitRecipe(item: Item): org.bukkit.inventory.Recipe
}