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
            throw ItemAlreadyRegisteredException(item)
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

    fun getItem(itemStack: ItemStack): Item? {
        val key: NamespacedKey = itemStack.itemMeta.rainbowQuartzId ?: return null
        return getItem(key)
    }

    fun containsItem(key: NamespacedKey): Boolean {
        return getItem(key) != null
    }

    fun clear() {
        items.clear()
    }

    class ItemAlreadyRegisteredException(item: Item) : Exception("Item ${item.key} has already been registered.")
}