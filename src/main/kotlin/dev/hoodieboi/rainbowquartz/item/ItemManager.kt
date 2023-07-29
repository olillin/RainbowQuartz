package dev.hoodieboi.rainbowquartz.item

import dev.hoodieboi.rainbowquartz.ItemConfigManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class ItemManager(private val plugin: Plugin) {

    private var items: MutableMap<NamespacedKey, Item> = mutableMapOf()
    private val itemConfig = ItemConfigManager("items.yml", plugin)
    private var lastHash: Int = 0

    val itemKeys: Set<NamespacedKey>
        get() = items.keys.toSet()

    /**
     * Registers an item and its recipes
     */
    @Throws(ItemAlreadyRegisteredException::class)
    fun registerItem(item: Item) {
        plugin.logger.info("Registering item ${item.key}")
        if (items.containsKey(item.key)) {
            throw ItemAlreadyRegisteredException("${item.key} is already registered")
        }

        items[item.key] = item
        // Add recipes
        for (recipe in item.recipes) {
            Bukkit.addRecipe(recipe.asBukkitRecipe(item))
        }
        saveToConfig()
    }

    fun registerDefault(item: Item) {
        if (containsItem(item.key)) {
            return
        }
        registerItem(item)
    }

    fun unregisterItem(key: NamespacedKey) {
        val item = items[key] ?: return
        for (recipe in item.recipes) {
            Bukkit.removeRecipe(recipe.key(item))
        }
        items.remove(key)
        saveToConfig()
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

    fun reload() {
        itemConfig.reload()
        itemConfig.getItems().forEach {
            registerItem(it)
        }
    }

    fun saveToConfig() {
        if (items.hashCode() == lastHash) return
        lastHash = items.hashCode()

        plugin.logger.info("Saving items to config...")
        itemConfig.saveItems(items.values)
    }

    class ItemAlreadyRegisteredException(message: String) : Exception(message)
}