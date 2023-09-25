package dev.hoodieboi.rainbowquartz.item

import dev.hoodieboi.rainbowquartz.craft.Recipe
import dev.hoodieboi.rainbowquartz.event.EventPredicate
import dev.hoodieboi.rainbowquartz.event.PredicatedEventHandler
import dev.hoodieboi.rainbowquartz.event.EventHandler
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
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.annotations.Contract

class Item(val key: NamespacedKey, item: ItemStack, recipes: List<Recipe>, handlers: Map<Class<out Event>, Set<PredicatedEventHandler<*>>>) : Keyed, ConfigurationSerializable {
    protected val handlers: MutableMap<Class<out Event>, MutableSet<PredicatedEventHandler<*>>>

    val item: ItemStack
    val recipes: List<Recipe>

    constructor(key: NamespacedKey, item: ItemStack) : this(key, item, listOf(), mutableMapOf())
    constructor(key: NamespacedKey, item: ItemStack, recipes: List<Recipe>) : this(key, item, recipes, mutableMapOf())
    constructor(key: NamespacedKey, item: ItemStack, handlers: Map<Class<out Event>, Set<PredicatedEventHandler<*>>>) : this(key, item, listOf(), handlers)

    init {
        // Set id
        this.item = ItemStack(item)
        val meta = this.item.itemMeta
        meta.rainbowQuartzId = key
        this.item.itemMeta = meta
        // Recipes
        this.recipes = recipes.toList()
        // Eventhandler
        this.handlers = handlers.map {
            it.key to it.value.toMutableSet()
        }.toMap().toMutableMap()
    }

    companion object {
        /**
         * Apply default formatting to an item [name].
         *
         * @see unformatName
         */
        @JvmStatic
        @Contract("_ -> this")
        fun formatName(name: Component?): Component? {
            return name
                ?.color(name.color() ?: NamedTextColor.WHITE)
                ?.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        }

        /**
         * Remove formatting from an item name.
         *
         * @see formatName
         */
        @JvmStatic
        @Contract("_ -> this")
        fun unformatName(name: Component?): Component? {
            return name?.color(
                name.color().takeUnless { it == NamedTextColor.WHITE }
            )
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
     * Register an [EventHandler] to be executed when an [EventPredicate] is successful
     *
     * @param eventType The event class
     * @param predicate The predicate to check before calling the handler
     * @param handler What should happen when the predicate is successful
     */
    fun <T : Event> addEventHandler(eventType: Class<T>, predicate: EventPredicate<T>, handler: EventHandler<T>) {
        if (!handlers.containsKey(eventType)) {
            handlers[eventType] = mutableSetOf()
        }
        handlers[eventType]!!.add(PredicatedEventHandler(predicate, handler))
    }

    fun <T : Event> getHandlerPairs(eventType: Class<out T>): Set<PredicatedEventHandler<in T>>? {
        val result: MutableSet<PredicatedEventHandler<in T>> = mutableSetOf()
        val handler: Set<PredicatedEventHandler<*>> = handlers[eventType]
                ?: return null
        handler.forEach {
            @Suppress("UNCHECKED_CAST")
            result.add(it as PredicatedEventHandler<T>)
        }
        return result
    }

    fun getEventTypes(): Set<Class<out Event>> {
        return handlers.keys
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