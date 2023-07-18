package dev.hoodieboi.rainbowquartz.item

import co.aikar.timings.TimedEventExecutor
import dev.hoodieboi.rainbowquartz.craft.Recipe
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

class Item (val key: NamespacedKey, val item: ItemStack, val recipes: List<Recipe>) : Keyed, ConfigurationSerializable {
    val handlers : MutableMap<Class<out Event>, HandlerList> = HashMap()

    init {
        // Set id
        val meta = item.itemMeta
        meta.rainbowQuartzId = key
        item.itemMeta = meta
    }

    companion object {
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
        return key.equals(other.key)
                && item.equals(other.item)
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
            var displayName: Component = if (name.hasDecoration(TextDecoration.ITALIC)) {
                name
            } else {
                name.decoration(TextDecoration.ITALIC, false)
            }
            // White by default
            if (displayName.color() == null) {
                displayName = displayName.color(NamedTextColor.WHITE)
            }
            // Modify item
            val itemMeta = result.itemMeta
            itemMeta.displayName(displayName)
            result.itemMeta = itemMeta
            return this
        }
        fun setName(name: String): ItemBuilder {
            return setName(Component.text(name))
        }

        fun addLore(vararg lore: Component): ItemBuilder {
            val itemMeta = result.itemMeta
            if (!itemMeta.hasLore()) {
                itemMeta.lore(ArrayList())
            }
            val metaLore = itemMeta.lore()!!
            val iterator = lore.iterator()
            while (iterator.hasNext()) {
                var component = iterator.next()
                // Non-italic by default
                if (!component.hasDecoration(TextDecoration.ITALIC)) {
                    component = component.decoration(TextDecoration.ITALIC, false)
                }
                // Gray by default
                if (component.color() == null) {
                    component = component.color(NamedTextColor.GRAY)
                }
                metaLore.add(component)
            }
            itemMeta.lore(metaLore)
            result.itemMeta = itemMeta
            return this
        }

        fun addLore(lore: String): ItemBuilder {
            return addLore(Component.text(lore).color(NamedTextColor.GRAY))
        }

        fun insertLore(index: Int, lore: Component): ItemBuilder {
            val itemMeta = result.itemMeta
            if (!itemMeta.hasLore()) {
                itemMeta.lore(ArrayList())
            }
            val metaLore = itemMeta.lore()!!
            metaLore.add(index, lore)
            itemMeta.lore(metaLore)
            result.itemMeta = itemMeta
            return this
        }

        fun insertLore(index: Int, lore: String): ItemBuilder {
            return insertLore(index, Component.text(lore))
        }

        fun removeLore(): ItemBuilder {
            val itemMeta = result.itemMeta
            itemMeta.lore(null)
            result.itemMeta = itemMeta
            return this
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