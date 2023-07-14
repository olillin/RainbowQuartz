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

class Item (val key: NamespacedKey, val result: ItemStack, val recipes: List<Recipe>) : Keyed {
    val handlers : MutableMap<Class<out Event>, HandlerList> = HashMap()

    init {
        // Set id
        val meta = result.itemMeta
        meta.rainbowQuartzId = key
        result.itemMeta = meta
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

        getEventListeners(event).register(RegisteredListener(listener, timedExecutor, priority, plugin, ignoreCancelled))
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
                    && Event::class.java.isAssignableFrom(clazz.superclass)) {
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
        return "Item($key){material=${result.type}}"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Item) return false
        return key.equals(other.key)
                && result.equals(other.result)
                && recipes.equals(other.recipes)
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + result.hashCode()
        result = 31 * result + recipes.hashCode()
        return result
    }

    class ItemBuilder(val key: NamespacedKey, private val result: ItemStack) {
        private val recipes: MutableList<Recipe>

        constructor(key: NamespacedKey, material: Material, amount: Int): this(key, ItemStack(material, amount))
        constructor(key: NamespacedKey, material: Material) : this(key, ItemStack(material))

        fun getMaterial() = result.type
        fun setMaterial(material: Material): ItemBuilder {
            result.type = material
            return this
        }
        fun getAmount() = result.amount
        fun setAmount(amount: Int): ItemBuilder {
            result.amount = amount
            return this
        }
        fun setName(name: Component): ItemBuilder {
            // Non-italic by default
            val displayName: Component = if (name.hasDecoration(TextDecoration.ITALIC)) {
                name
            } else {
                name.decoration(TextDecoration.ITALIC, false)
            }
            // Modify item
            val itemMeta = result.itemMeta
            itemMeta.displayName(displayName)
            result.itemMeta = itemMeta
            return this
        }
        fun setName(name: String): ItemBuilder {
            return setName(Component.text(name).color(NamedTextColor.WHITE))
        }

        fun addEnchant(enchantment: Enchantment, level: Int): ItemBuilder {
            val itemMeta = result.itemMeta
            itemMeta.addEnchant(enchantment, level, true)
            result.itemMeta = itemMeta
            return this
        }

        fun addEnchant(enchantment: Enchantment): ItemBuilder {
            return addEnchant(enchantment, 1)
        }

        fun removeEnchant(enchantment: Enchantment): ItemBuilder {
            val itemMeta = result.itemMeta
            itemMeta.removeEnchant(enchantment)
            result.itemMeta = itemMeta
            return this
        }

        fun addAttributeModifier(attribute: Attribute, modifier: AttributeModifier): ItemBuilder {
            val itemMeta = result.itemMeta
            itemMeta.addAttributeModifier(attribute, modifier)
            result.itemMeta = itemMeta
            return this
        }

        fun removeAttributeModifier(attribute: Attribute, modifier: AttributeModifier): ItemBuilder {
            val itemMeta = result.itemMeta
            itemMeta.removeAttributeModifier(attribute, modifier)
            result.itemMeta = itemMeta
            return this
        }

        fun removeAttributeModifier(attribute: Attribute): ItemBuilder {
            val itemMeta = result.itemMeta
            itemMeta.removeAttributeModifier(attribute)
            result.itemMeta = itemMeta
            return this
        }

        fun addItemFlags(vararg itemFlags: ItemFlag): ItemBuilder {
            val itemMeta = result.itemMeta
            itemMeta.addItemFlags(*itemFlags)
            result.itemMeta = itemMeta
            return this
        }

        fun removeItemFlags(vararg itemFlags: ItemFlag): ItemBuilder {
            val itemMeta = result.itemMeta
            itemMeta.removeItemFlags(*itemFlags)
            result.itemMeta = itemMeta
            return this
        }

        fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
            val itemMeta = result.itemMeta
            itemMeta.isUnbreakable = unbreakable
            result.itemMeta = itemMeta
            return this
        }

        fun addRecipe(recipe: Recipe): ItemBuilder {
            recipes.add(recipe)
            return this
        }

        fun removeRecipe(recipeType: Class<out Recipe>): ItemBuilder {
            val iterator = recipes.iterator()

            while (iterator.hasNext()) {
                val recipe = iterator.next()

                if (recipeType.isAssignableFrom(recipe.javaClass)) {
                    iterator.remove()
                }
            }

            return this
        }

        init {
            recipes = ArrayList()
        }
        fun build(): Item {
            return Item(key, result, recipes)
        }
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