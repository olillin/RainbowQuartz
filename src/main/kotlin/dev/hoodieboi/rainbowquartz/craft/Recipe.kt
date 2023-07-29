package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.NamespacedKey
import org.bukkit.configuration.serialization.ConfigurationSerializable

abstract class Recipe : ConfigurationSerializable {
    abstract val suffix: String
    abstract fun asBukkitRecipe(item: Item): org.bukkit.inventory.Recipe
    fun key(item: Item): NamespacedKey = key(item.key)
    fun key(itemKey: NamespacedKey): NamespacedKey = NamespacedKey.fromString("$itemKey.$suffix")!!
}