package dev.hoodieboi.rainbowquartz.item

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

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
            throw ItemAlreadyRegisteredException("${item.key} is already registered.")
        }

        items[item.key] = item
        // Add recipes
        for (recipe in item.recipes) {
            Bukkit.addRecipe(recipe.toBukkitRecipe(item))
        }
    }

    fun unregisterItem(key: NamespacedKey) {
        val item = items[key] ?: return
        for (recipe in item.recipes) {
            Bukkit.removeRecipe(recipe.key(item))
        }
        items.remove(key)
    }

    fun getItem(key: NamespacedKey): Item? {
        return items[key]
    }

    fun getItem(itemStack: ItemStack): Item? {
        val key: NamespacedKey = itemStack.itemMeta.rainbowQuartzId ?: return null
        return getItem(key)
    }

    fun getItems(): Set<Item> {
        return items.values.toSet()
    }

    fun containsItem(key: NamespacedKey): Boolean {
        return getItem(key) != null
    }

    fun clear() {
        items.clear()
    }

    class ItemAlreadyRegisteredException(message: String) : Exception(message)
}