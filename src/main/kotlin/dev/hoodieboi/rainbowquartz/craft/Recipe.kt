package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.NamespacedKey
import org.bukkit.configuration.serialization.ConfigurationSerializable

abstract class Recipe : ConfigurationSerializable {
    abstract val suffix: String
    abstract fun toBukkitRecipe(item: Item): org.bukkit.inventory.Recipe
    fun key(item: Item): NamespacedKey = NamespacedKey.fromString(item.key.toString() + '.' + suffix)!!
}

class InvalidTypeException(suffix: String) : Exception("Cannot deserialize recipe of type '$suffix' using this class")