package com.olillin.rainbowquartz.item

import com.olillin.rainbowquartz.craft.Recipe
import com.olillin.rainbowquartz.event.EventHandlerGroup
import com.olillin.rainbowquartz.event.GuiEventHandlerGroup
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

public open class GuiItem(id: NamespacedKey, item: ItemStack) : Item(id, item), ConfigurationSerializable {
    override fun <T : Event> addEventHandler(eventHandlerGroup: EventHandlerGroup<T>) {
        if (eventHandlerGroup !is GuiEventHandlerGroup<*>) throw IllegalArgumentException("Only GuiEventHandler can be added to GuiItem")
        super.addEventHandler(eventHandlerGroup)
    }

    override fun serialize(): MutableMap<String, Any> {
        val result = mutableMapOf(
            "id" to id.toString(),
            "recipes" to recipes,
            "events" to eventHandlerGroups.map { it as GuiEventHandlerGroup },
        )

        val stack = ItemStack(getItem()).apply {
            val meta = itemMeta
            meta.rainbowQuartzId = null
            itemMeta = meta
        }
        result["item"] = stack

        return result
    }

    public companion object {
        public fun fromItem(item: Item): GuiItem =
            GuiItem(item.id, item.getItem())

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item
         * @see ConfigurationSerializable
         */
        public fun deserialize(args: Map<String, Any>): Item {
            val section = MemoryConfiguration()
            for ((key, value) in args.entries) {
                section[key] = value
            }

            // Create key
            val idString = section.getString("id")
                ?: throw IllegalArgumentException("Missing required property 'id' of type String")
            val id = NamespacedKey.fromString(idString)!!

            // Initialize builder
            val itemStack = section.getItemStack("item")
                ?: throw IllegalArgumentException("Missing required property 'item' of type ItemStack")
            val builder = ItemBuilder(id, itemStack)

            // Add recipes
            val recipes: Any = section.get("recipes") ?: listOf<Recipe<*, *>>()
            if (recipes !is List<*>) {
                throw IllegalArgumentException("Invalid property 'recipes'")
            }
            for (recipe in recipes) {
                if (recipe !is Recipe<*, *>) {
                    Bukkit.getLogger().warning("Unable to add recipe to ${builder.build()}, invalid format")
                    continue
                }
                builder.addRecipe(recipe)
            }

            // Add event handlers
            val eventHandlers: Any = section.get("events") ?: listOf<EventHandlerGroup<*>>()
            if (eventHandlers !is List<*>) {
                throw IllegalArgumentException("Invalid property 'events'")
            }
            for (handler in eventHandlers) {
                if (handler !is GuiEventHandlerGroup<*>) {
                    Bukkit.getLogger().warning("Unable to add event handler to ${builder.build()}, invalid format")
                    continue
                }
                builder.addEventHandler(handler)
            }

            return GuiItem.fromItem(builder.build())
        }
    }
}