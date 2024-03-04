@file:Suppress("MemberVisibilityCanBePrivate")

package com.olillin.rainbowquartz.item

import com.olillin.rainbowquartz.craft.Recipe
import com.olillin.rainbowquartz.event.EventHandler
import com.olillin.rainbowquartz.event.EventHandlerGroup
import com.olillin.rainbowquartz.event.EventPredicate
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.Keyed
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

public open class Item(
    public val id: NamespacedKey, item: ItemStack, recipes: List<Recipe<*, *>> = listOf()
) : Cloneable, Keyed {

    private val item: ItemStack = ItemStack(item).apply {
        itemMeta = itemMeta.apply {
            rainbowQuartzId = id
        }
        amount = 1
    }
    protected open val eventHandlerGroups: MutableList<EventHandlerGroup<*>> = mutableListOf()

    public open val recipes: List<Recipe<*, *>> = recipes.toList()

    public fun getItem(): ItemStack = ItemStack(item)

    /**
     * Register an [EventHandlerGroup]
     */
    public open fun <T : Event> addEventHandler(eventHandlerGroup: EventHandlerGroup<T>) {
        eventHandlerGroups.add(eventHandlerGroup)
    }

    /**
     * Register an [EventHandler] to be executed when an [EventPredicate] is successful
     *
     * @param eventType The class of the event to listen for
     * @param predicate The predicate to check against the event before the handler is called
     * @param handler What should happen when the predicate is successful
     */
    public fun <T : Event> addEventHandler(
        eventType: Class<out T>, predicate: EventPredicate<T>, handler: EventHandler<T>
    ) {
        return addEventHandler(EventHandlerGroup(eventType, predicate, handler))
    }

    /**
     * Get all event handler groups that are registered for [eventType]
     *
     * @see EventHandlerGroup
     */
    @Suppress("UNCHECKED_CAST")
    public fun <T : Event> getEventHandlers(eventType: Class<out T>): List<EventHandlerGroup<in T>> {
        return eventHandlerGroups.filter { it.eventType == eventType } as List<EventHandlerGroup<in T>>
    }

    /**
     * Get all event handler groups that are registered for [eventType] with [predicate]
     *
     * @see EventHandlerGroup
     */
    @Suppress("UNCHECKED_CAST")
    public fun <T : Event> getEventHandlers(
        eventType: Class<out T>, predicate: EventPredicate<T>
    ): List<EventHandlerGroup<in T>> = eventHandlerGroups.filter {
        it.eventType == eventType && it.predicate == predicate
    } as List<EventHandlerGroup<in T>>

    /**
     * Get all event handler groups that are registered for [eventType] with [predicate] and [handler]
     *
     * @see EventHandlerGroup
     */
    @Suppress("UNCHECKED_CAST")
    public fun <T : Event> getEventHandlers(
        eventType: Class<out T>, predicate: EventPredicate<T>, handler: EventHandler<T>
    ): List<EventHandlerGroup<in T>> = eventHandlerGroups.filter {
        it.eventType == eventType && it.predicate == predicate && it.handler == handler
    } as List<EventHandlerGroup<in T>>

    public fun <T : Event> removeEventHandler(eventHandlerGroup: EventHandlerGroup<T>) {
        eventHandlerGroups.remove(eventHandlerGroup)
    }

    /**
     * Remove all event handlers for [eventType]
     */
    public fun <T : Event> removeEventHandlers(eventType: Class<T>) {
        val handlers = getEventHandlers(eventType)
        handlers.forEach {
            removeEventHandler(it)
        }
    }

    /**
     * Remove all event handlers for [eventType] with [predicate]
     */
    public fun <T : Event> removeEventHandlers(eventType: Class<T>, predicate: EventPredicate<T>) {
        val handlers = getEventHandlers(eventType, predicate)
        handlers.forEach {
            removeEventHandler(it)
        }
    }

    /**
     * Remove all event handlers for [eventType] with [predicate] and [handler]
     */
    public fun <T : Event> removeEventHandlers(
        eventType: Class<T>, predicate: EventPredicate<T>, handler: EventHandler<T>
    ) {
        val handlers = getEventHandlers(eventType, predicate, handler)
        handlers.forEach {
            removeEventHandler(it)
        }
    }

    public fun getEventTypes(): Set<Class<out Event>> = eventHandlerGroups.map { it.eventType }.toSet()

    override fun key(): Key = id

    public fun toComponent(): Component {
        val item: ItemStack = getItem()
        val name: Component = item.itemMeta.displayName() ?: Component.translatable(item.type)
        return name.hoverEvent(item)
    }

    override fun toString(): String = "Item(id=$id,material=${item.type}})"

    public override fun clone(): Item {
        val item = Item(id, item.clone(), recipes)
        for (eventHandlerGroup in eventHandlerGroups) {
            item.addEventHandler(eventHandlerGroup)
        }
        return item
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (id != other.id) return false
        if (item != other.item) return false
        if (!(recipes.toTypedArray() contentEquals other.recipes.toTypedArray())) return false
        if (!(eventHandlerGroups.toTypedArray() contentEquals other.eventHandlerGroups.toTypedArray())) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + result.hashCode()
        result = 31 * result + recipes.hashCode()
        result = 31 * result + eventHandlerGroups.hashCode()
        return result
    }
}

public var ItemMeta.rainbowQuartzId: NamespacedKey?
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
public var ItemStack.rainbowQuartzId: NamespacedKey?
    get() = itemMeta.rainbowQuartzId
    set(value) {
        itemMeta = itemMeta.apply {
            rainbowQuartzId = value
        }
    }