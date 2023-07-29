package dev.hoodieboi.rainbowquartz.item

import co.aikar.timings.TimedEventExecutor
import dev.hoodieboi.rainbowquartz.craft.Recipe
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.Keyed
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.*

class Item(val key: NamespacedKey, val item: ItemStack, val recipes: List<Recipe>) : Keyed, ConfigurationSerializable {
    val handlers: MutableMap<Class<out Event>, HandlerList> = HashMap()

    init {
        // Set id
        val meta = item.itemMeta
        meta.rainbowQuartzId = key
        item.itemMeta = meta
    }

    companion object {
        @JvmStatic
        fun formatName(name: Component?): Component? {
            return name
                ?.color(name.color() ?: NamedTextColor.WHITE)
                ?.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        }

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
            val id = section.getString("id") ?: throw IllegalArgumentException("Missing required property 'id' of type String")
            val key = NamespacedKey.fromString(id)!!

            // Initialize builder
            val itemStack = section.getItemStack("item") ?: throw IllegalArgumentException("Missing required property 'item' of type ItemStack")
            val builder = ItemBuilder(key,  itemStack)

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

    /**
     * Registers all the events in the given listener class
     *
     * @param listener Listener to register
     * @param plugin Plugin to register
     */
    @Throws(IllegalPluginAccessException::class)
    fun registerEvents(listener: Listener, plugin: Plugin): Item {
        if (!plugin.isEnabled) {
            throw IllegalPluginAccessException("Plugin attempted to register $listener while not enabled");
        }

        for ((key, value) in plugin.pluginLoader.createRegisteredListeners(listener, plugin)) {
            getEventListeners(getRegistrationClass(key)).registerAll(value)
        }

        return this
    }

    /**
     * Registers the specified executor to the given event class
     *
     * @param event Event type to register
     * @param listener Listener to register
     * @param priority Priority to register this event at
     * @param executor EventExecutor to register
     * @param plugin Plugin to register
     */
    @Throws(IllegalPluginAccessException::class)
    fun registerEvent(
        event: Class<out Event?>,
        listener: Listener,
        priority: EventPriority,
        executor: EventExecutor,
        plugin: Plugin
    ): Item {
        return registerEvent(event, listener, priority, executor, plugin, false)
    }

    /**
     * Registers the specified executor to the given event class
     *
     * @param event Event type to register
     * @param listener Listener to register
     * @param priority Priority to register this event at
     * @param executor EventExecutor to register
     * @param plugin Plugin to register
     */
    @Throws(IllegalPluginAccessException::class)
    fun registerEvent(
        event: Class<out Event?>,
        listener: Listener,
        priority: EventPriority,
        executor: EventExecutor,
        plugin: Plugin,
        ignoreCancelled: Boolean
    ): Item {
        if (!plugin.isEnabled) {
            throw IllegalPluginAccessException("Plugin attempted to register $event while not enabled")
        }
        val timedExecutor = TimedEventExecutor(executor, plugin, null, event) // Paper

        getEventListeners(event).register(
            RegisteredListener(
                listener,
                timedExecutor,
                priority,
                plugin,
                ignoreCancelled
            )
        )
        return this
    }

    fun getEventListeners(type: Class<out Event>): HandlerList {
        val handler = handlers[type]
        if (handler != null) {
            return handler
        } else {
            val newHandler = HandlerList()
            handlers[type] = newHandler
            return newHandler
        }
    }

    private fun getRegistrationClass(clazz: Class<out Event>): Class<out Event> {
        try {
            clazz.getDeclaredMethod("getHandlerList")
            return clazz
        } catch (e: NoSuchMethodException) {
            if (clazz.superclass != null
                && !clazz.superclass.equals(Event::javaClass)
                && Event::class.java.isAssignableFrom(clazz.superclass)
            ) {
                return getRegistrationClass(clazz.superclass.asSubclass(Event::class.java))
            } else {
                throw IllegalPluginAccessException("Unable to find handler list for event ${clazz.getName()}. Static getHandlerList method required!")
            }
        }
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
        return key == other.key
                && item == other.item
                && recipes == other.recipes
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + result.hashCode()
        result = 31 * result + recipes.hashCode()
        return result
    }
}

var ItemMeta.rainbowQuartzId: NamespacedKey?
    get() {
        val id = persistentDataContainer.get(
            NamespacedKey.fromString("rainbowquartz:id")!!,
            PersistentDataType.STRING
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
                NamespacedKey.fromString("rainbowquartz:id")!!,
                PersistentDataType.STRING,
                value.toString()
            )
        }
    }