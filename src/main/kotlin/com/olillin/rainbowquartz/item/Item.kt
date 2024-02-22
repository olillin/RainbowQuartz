package com.olillin.rainbowquartz.item

import com.olillin.rainbowquartz.craft.Recipe
import com.olillin.rainbowquartz.event.*
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.Keyed
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

@Suppress("UNUSED")
class Item(
    val key: NamespacedKey, item: ItemStack, recipes: List<Recipe>
) : Keyed, ConfigurationSerializable {
    private val item: ItemStack = ItemStack(item).apply {
        itemMeta = itemMeta.apply {
            rainbowQuartzId = key
        }
        amount = 1
    }
    private val eventHandlerGroups: MutableList<EventHandlerGroup<*>> = mutableListOf()

    val recipes: List<Recipe> = recipes.toList()

    constructor(key: NamespacedKey, item: ItemStack) : this(key, item, listOf())

    fun getItem(): ItemStack = ItemStack(item)

    /**
     * Register an [EventHandlerGroup]
     */
    fun <T : Event> addEventHandler(eventHandlerGroup: EventHandlerGroup<T>) {
        eventHandlerGroups.add(eventHandlerGroup)
    }

    /**
     * Register an [EventHandler] to be executed when an [EventPredicate] is successful
     *
     * @param eventType The class of the event to listen for
     * @param predicate The predicate to check against the event before the handler is called
     * @param handler What should happen when the predicate is successful
     */
    fun <T : Event> addEventHandler(eventType: Class<out T>, predicate: EventPredicate<T>, handler: EventHandler<T>) {
        addEventHandler(
            EventHandlerGroup(
                eventType, predicate, handler
            )
        )
    }

    /**
     * Get all event handler groups that are registered for [eventType]
     *
     * @see EventHandlerGroup
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Event> getEventHandlers(eventType: Class<out T>): List<EventHandlerGroup<in T>> {
        return eventHandlerGroups.filter { it.eventType == eventType } as List<EventHandlerGroup<in T>>
    }

    /**
     * Get all event handler groups that are registered for [eventType] with [predicate]
     *
     * @see EventHandlerGroup
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Event> getEventHandlers(
        eventType: Class<out T>, predicate: EventPredicate<T>
    ): List<EventHandlerGroup<in T>> {
        return eventHandlerGroups.filter { it.eventType == eventType && it.predicate == predicate } as List<EventHandlerGroup<in T>>
    }

    /**
     * Get all event handler groups that are registered for [eventType] with [predicate] and [handler]
     *
     * @see EventHandlerGroup
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Event> getEventHandlers(
        eventType: Class<out T>, predicate: EventPredicate<T>, handler: EventHandler<T>
    ): List<EventHandlerGroup<in T>> {
        return eventHandlerGroups.filter { it.eventType == eventType && it.predicate == predicate && it.handler == handler } as List<EventHandlerGroup<in T>>
    }

    /**
     * Remove all event handlers for [eventType]
     */
    fun <T : Event> removeEventHandlers(eventType: Class<T>) {
        val handlers = getEventHandlers(eventType)
        handlers.forEach {
            removeEventHandler(it)
        }
    }

    /**
     * Remove all event handlers for [eventType] with [predicate]
     */
    fun <T : Event> removeEventHandlers(eventType: Class<T>, predicate: EventPredicate<T>) {
        val handlers = getEventHandlers(eventType, predicate)
        handlers.forEach {
            removeEventHandler(it)
        }
    }

    /**
     * Remove all event handlers for [eventType] with [predicate] and [handler]
     */
    fun <T : Event> removeEventHandlers(eventType: Class<T>, predicate: EventPredicate<T>, handler: EventHandler<T>) {
        val handlers = getEventHandlers(eventType, predicate, handler)
        handlers.forEach {
            removeEventHandler(it)
        }
    }

    fun <T : Event> removeEventHandler(eventHandlerGroup: EventHandlerGroup<T>) {
        eventHandlerGroups.remove(eventHandlerGroup)
    }

    fun getEventTypes(): Set<Class<out Event>> {
        return eventHandlerGroups.map { it.eventType }.toSet()
    }

    override fun key(): Key {
        return key
    }

    override fun toString(): String {
        return "Item($key){material=${item.type}}"
    }

    override fun serialize(): MutableMap<String, Any> {
        val result = mutableMapOf<String, Any>(
            "id" to key.toString()
        )

        val stack = ItemStack(item)
        val meta = stack.itemMeta
        meta.rainbowQuartzId = null
        stack.setItemMeta(meta)
        result["item"] = stack

        result["recipes"] = recipes

        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Item) return false
        return key == other.key && item == other.item && recipes == other.recipes
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + result.hashCode()
        result = 31 * result + recipes.hashCode()
        return result
    }

    fun displayNameComponent(): Component? {
        return (item.displayName() as? TranslatableComponent)?.args()?.get(0)
    }

    companion object {
        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item
         * @see ConfigurationSerializable
         */
        @JvmStatic
        fun deserialize(args: Map<String, Any>): Item {

            val section = MemoryConfiguration()
            section.addDefaults(args)

            // Create key
            val id = section.getString("id")
                ?: throw IllegalArgumentException("Missing required property 'id' of type String")
            val key = NamespacedKey.fromString(id)!!

            // Initialize builder
            val itemStack = section.getItemStack("item")
                ?: throw IllegalArgumentException("Missing required property 'item' of type ItemStack")
            val builder = ItemBuilder(key, itemStack)

            // Add recipes
            val recipes: Any = section.get("recipes") ?: listOf<Recipe>()
            if (recipes !is List<*>) {
                throw IllegalArgumentException("Invalid property 'recipes'")
            }
            for (recipe in recipes) {
                if (recipe !is Recipe) {
                    Bukkit.getLogger().warning("Unable to register recipe for Rainbow Quartz item $key, invalid format")
                    continue
                }
                builder.addRecipe(recipe)
            }
            return builder.build()
        }
    }
}

var ItemMeta.rainbowQuartzId: NamespacedKey?
    get() {
        val id = persistentDataContainer.get(
            NamespacedKey.fromString("rainbowquartz:id")!!, PersistentDataType.STRING
        ) ?: return null
        return NamespacedKey.fromString(id)

    }
    set(value) {
        if (value == null) {
            persistentDataContainer.remove(
                NamespacedKey.fromString("rainbowquartz:id")!!
            )
        } else {
            persistentDataContainer.set(
                NamespacedKey.fromString("rainbowquartz:id")!!, PersistentDataType.STRING, value.toString()
            )
        }
    }