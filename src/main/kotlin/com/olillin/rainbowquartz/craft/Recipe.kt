package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.NamespacedKey
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice

abstract class Recipe : ConfigurationSerializable {
    abstract val suffix: String
    abstract fun asBukkitRecipe(item: Item): org.bukkit.inventory.Recipe
    fun key(item: Item): NamespacedKey = key(item.key)
    fun key(itemKey: NamespacedKey): NamespacedKey = NamespacedKey.fromString("$itemKey/$suffix-${hashCode()}")!!

    companion object {
        @JvmStatic
        fun asItemStack(ingredient: RecipeChoice): ItemStack {
            return if (ingredient is RecipeChoice.ExactChoice) {
                ingredient.itemStack
            } else if (ingredient is RecipeChoice.MaterialChoice) {
                ingredient.itemStack
            } else {
                throw IllegalArgumentException("Unsupported RecipeChoice type")
            }.also {
                it.amount = 1
            }
        }
    }
}