package dev.hoodieboi.rainbowquartz.item

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.craft.Recipe
import dev.hoodieboi.rainbowquartz.event.EventPredicate
import dev.hoodieboi.rainbowquartz.event.PredicatedEventHandler
import dev.hoodieboi.rainbowquartz.event.handler.EventHandler
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.Keyed
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.NamespacedKey
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

class Item(val key: NamespacedKey, val item: ItemStack, val recipes: List<Recipe>) : Keyed, ConfigurationSerializable {
    private val handlers: MutableMap<Class<out Event>, MutableSet<PredicatedEventHandler<out Event>>> = HashMap()

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

    /**
     * Register an [EventHandler] to be executed when an [EventPredicate] is successful
     *
     * @param eventType The event class
     * @param predicate The predicate to check before calling the handler
     * @param handler What should happen when the predicate is successful
     */
    fun <E : Event> listen(eventType: Class<E>, predicate: EventPredicate<E>, handler: EventHandler<E>) {
        if (!Event::class.java.isAssignableFrom(eventType)) {
            throw IllegalArgumentException()
        }
        @Suppress("UNCHECKED_CAST")
        RainbowQuartz.itemEventDispatcher.listen(eventType as Class<Event>)
        if (!handlers.containsKey(eventType)) {
            handlers[eventType] = mutableSetOf()
        }
        handlers[eventType]!!.add(PredicatedEventHandler(predicate, handler))
    }

    fun getHandlerPairs(eventType: Class<out Event>): Set<PredicatedEventHandler<out Event>>? {
        return handlers[eventType]
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