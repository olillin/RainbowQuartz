package dev.hoodieboi.rainbowquartz.item

import co.aikar.timings.TimedEventExecutor
import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.craft.Recipe
import dev.hoodieboi.rainbowquartz.event.EventContext
import dev.hoodieboi.rainbowquartz.event.EventDispatcher
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.Keyed
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.*
import java.lang.reflect.Method
import java.util.function.Function

class Item(val key: NamespacedKey, val item: ItemStack, val recipes: List<Recipe>) : Keyed, ConfigurationSerializable {
    val handlers: MutableMap<Class<out Event>, HandlerList> = HashMap()

    init {
        // Set id
        val meta = item.itemMeta
        meta.rainbowQuartzId = key
        item.itemMeta = meta
    }

    companion object {
        fun formatName(name: Component?): Component? {
            return name
                ?.color(name.color() ?: NamedTextColor.WHITE)
                ?.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        }

//        fun deserialize(args: Map<String, Any>): Item {
//            val key = NamespacedKey.fromString(args["id"] as String)!!
//            val itemStack = ItemStack.deserialize(args["item"] as Map<String, Any>)
//            val builder = Item.ItemBuilder(key,  itemStack)
//            for (recipeArgs in args["recipes"] as List<Map<String, Any>>) {
//                val type = recipeArgs["type"]
//                val recipeType = recipeTypes.first{ t -> t.suffix == type }
//                val recipe = recipeType.deserialize()
//                builder.addRecipe(recipe)
//            }
//            return builder.build()
//        }

        private val recipeTypes: List<Class<out Recipe>>
            get() = listOf(
                dev.hoodieboi.rainbowquartz.craft.BlastingRecipe::class.java,
                dev.hoodieboi.rainbowquartz.craft.CampfireRecipe::class.java,
                dev.hoodieboi.rainbowquartz.craft.FurnaceRecipe::class.java,
                dev.hoodieboi.rainbowquartz.craft.ShapedRecipe::class.java,
                dev.hoodieboi.rainbowquartz.craft.ShapelessRecipe::class.java,
                dev.hoodieboi.rainbowquartz.craft.SmithingTransformRecipe::class.java,
                dev.hoodieboi.rainbowquartz.craft.SmokingRecipe::class.java,
                dev.hoodieboi.rainbowquartz.craft.StonecuttingRecipe::class.java
            )
    }

    @Throws(IllegalPluginAccessException::class)
    fun <T: Event> listen(context: EventContext<T>, listener: Function<Event, Unit>) {
        RainbowQuartz.eventDispatcher.startListening(TODO("EVENT_TYPE"))
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
        val out = HashMap<String, Any>()

        out["item"] = item

        if (recipes.isNotEmpty()) {
            val recipesMap = HashMap<String, Any>()

            for (recipe in recipes) {
                recipesMap[recipe.key(this).toString()] = recipe
            }

            out["recipes"] = recipesMap
        }

        return out
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