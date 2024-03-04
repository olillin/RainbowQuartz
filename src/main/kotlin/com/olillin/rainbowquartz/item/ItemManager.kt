@file:Suppress("MemberVisibilityCanBePrivate")

package com.olillin.rainbowquartz.item

import com.olillin.rainbowquartz.ItemConfigManager
import com.olillin.rainbowquartz.RainbowQuartz
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin
import javax.naming.OperationNotSupportedException

public class ItemManager(private val plugin: Plugin) {
    private var items: MutableMap<NamespacedKey, Item> = mutableMapOf()
    private val itemConfig = ItemConfigManager("items.yml", plugin)
    private var oldConfigHash: Int = itemConfig.hashCode()

    /**
     * Registers an [item], its recipes and its event handlers.
     * @throws ItemAlreadyRegisteredException if an item by the same ID is already registered.
     */
    public fun registerItem(item: Item) {
        plugin.logger.info("Registering item ${item.id}")
        if (items.containsKey(item.id)) {
            throw ItemAlreadyRegisteredException("${item.id} is already registered")
        }

        items[item.id] = item
        // Add recipes
        for (recipe in item.recipes) {
            Bukkit.addRecipe(recipe.asBukkitRecipe(item))
        }
        // Add event handlers
        for (eventType in item.getEventTypes()) {
            RainbowQuartz.itemEventDispatcher.listen(eventType)
        }

        saveToConfig()
    }

    /**
     * Registers an [item], its recipes and its event handlers.
     * If an item by the same ID is already registered it is replaced.
     */
    public fun registerItemOverride(item: Item) {
        if (items.containsKey(item.id)) {
            unregisterItem(item.id)
        }
        registerItem(item)
    }

    /**
     * Registers an [item], its recipes and its event handlers.
     * If an item by the same ID is already registered the call is ignored.
     */
    public fun registerDefault(item: Item) {
        if (containsItem(item.id)) {
            return
        }
        registerItem(item)
    }

    /**
     * Removes an item that matches [id] together with its recipes and event handlers.
     * @return `true` if the item was successfully removed.
     */
    public fun unregisterItem(id: NamespacedKey): Boolean {
        val item = items[id] ?: return false
        for (recipe in item.recipes) {
            Bukkit.removeRecipe(recipe.key(item))
        }
        items.remove(id)
        saveToConfig()
        return true
    }

    /** Returns a copy of the item that matches [id]. */
    public fun getItem(id: NamespacedKey): Item? {
        return items[id]?.clone()
    }

    /** Returns `true` if item manager contains item with the same [id]. */
    public fun containsItem(id: NamespacedKey): Boolean = getItem(id) != null

    /** Returns a copy of the items registered in this item manager. */
    public fun getItems(): List<Item> = items.values.map { it.clone() }

    internal fun clear() {
        items.clear()
    }

    internal fun reload() {
        itemConfig.reload()
        itemConfig.getItems().forEach {
            registerItem(it)
        }
    }

    internal fun saveToConfig() {
        if (items.hashCode() == oldConfigHash) return
        oldConfigHash = items.hashCode()

        plugin.logger.info("Saving items to config...")
        itemConfig.saveItems(items.values.filterIsInstance<GuiItem>())
    }

    /** Thrown to indicate that a new [Item] cannot be registered because it has already been registered. */
    public class ItemAlreadyRegisteredException(message: String) : OperationNotSupportedException(message)
}