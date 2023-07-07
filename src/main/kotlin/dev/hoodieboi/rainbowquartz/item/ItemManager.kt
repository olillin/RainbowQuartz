package dev.hoodieboi.rainbowquartz.item

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey

class ItemManager {

    private val items: MutableMap<NamespacedKey, Item> = HashMap()

    val itemKeys: List<NamespacedKey>
        get() = items.keys.toList()

    /**
     * Registers an item and its recipes
     */
    @Throws(ItemAlreadyRegisteredException::class)
    fun registerItem(item: Item) {
        if (items.containsKey(item.key)) {
            throw ItemAlreadyRegisteredException()
        }

        items[item.key] = item
        // Add recipes
        for (recipe in item.recipes) {
            Bukkit.addRecipe(recipe.toBukkitRecipe(item))
        }
    }

    fun getItem(key: NamespacedKey): Item? {
        return items[key]
    }

    fun containsItem(key: NamespacedKey): Boolean {
        return getItem(key) != null
    }

    fun clear() {
        items.clear()
    }

    class ItemAlreadyRegisteredException : Exception() {}
}