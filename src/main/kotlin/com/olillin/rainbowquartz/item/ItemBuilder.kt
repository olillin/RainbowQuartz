@file:Suppress("MemberVisibilityCanBePrivate")

package com.olillin.rainbowquartz.item

import com.olillin.rainbowquartz.craft.Recipe
import com.olillin.rainbowquartz.event.EventHandler
import com.olillin.rainbowquartz.event.EventHandlerGroup
import com.olillin.rainbowquartz.event.EventPredicate
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Event
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.Contract

public open class ItemBuilder(
    public val id: NamespacedKey,
    result: ItemStack,
    recipes: List<Recipe<*, *>> = listOf(),
    handlers: List<EventHandlerGroup<*>> = listOf()
) {
    public constructor(id: NamespacedKey, material: Material, recipes: List<Recipe<*, *>> = listOf()) : this(
        id, ItemStack(material), recipes, listOf()
    )

    public constructor(item: Item) : this(item.id, item.getItem(), item.recipes)
    public constructor(builder: ItemBuilder) : this(builder.id, builder.result, builder.recipes)

    protected val result: ItemStack
    protected val recipes: MutableList<Recipe<*, *>>
    protected val eventHandlerGroups: MutableList<EventHandlerGroup<*>>

    init {
        this.result = ItemStack(result).also { it.amount = 1 }
        this.recipes = recipes.toMutableList()
        this.eventHandlerGroups = handlers.toMutableList()
    }

    public open fun getMaterial(): Material = result.type

    public open fun setMaterial(material: Material): ItemBuilder {
        result.type = material
        return this
    }

    public open fun getAmount(): Int = result.amount

    public open fun setAmount(amount: Int): ItemBuilder {
        result.amount = amount
        return this
    }

    public open fun getName(): Component? = result.itemMeta.displayName()

    public open fun hasName(): Boolean = getName() != null

    public open fun setName(name: Component?): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.displayName(formatName(name))
        result.itemMeta = itemMeta
        return this
    }

    public open fun setName(name: String): ItemBuilder = setName(Component.text(name))

    public open fun hasLore(): Boolean = getLore() != null

    public open fun getLore(): List<Component>? = result.itemMeta.lore()

    public open fun setLore(lore: List<Component>?): ItemBuilder {
        result.itemMeta = result.itemMeta.apply {
            lore(lore?.map { formatLore(it) })
        }
        return this
    }

    public open fun addLore(vararg lore: Component): ItemBuilder {
        result.itemMeta = result.itemMeta.apply {
            val currentLore: MutableList<Component> = if (hasLore()) {
                lore()!!
            } else {
                mutableListOf()
            }
            val iterator = lore.iterator()
            while (iterator.hasNext()) {
                val component = iterator.next()
                currentLore.add(formatLore(component)!!)
            }
            lore(currentLore)
        }
        return this
    }

    public open fun addLore(lore: String): ItemBuilder = addLore(formatLore(Component.text(lore))!!)

    public open fun addLore(index: Int, lore: Component): ItemBuilder {
        result.itemMeta = result.itemMeta.apply {

            val currentLore: MutableList<Component> = if (hasLore()) {
                lore()!!
            } else {
                mutableListOf()
            }
            currentLore.add(index, formatLore(lore)!!)
            lore(currentLore)
        }
        return this
    }

    public open fun addLore(index: Int, lore: String): ItemBuilder = addLore(index, Component.text(lore))

    public open fun removeLore(): ItemBuilder = setLore(null)

    public open fun hasEnchantments(): Boolean = getEnchantments().isNotEmpty()

    public open fun getEnchantments(): Map<Enchantment, Int> = result.itemMeta.enchants

    /**
     * Checks for the level of the specified enchantment.
     *
     * @param enchantment The enchantment to check.
     * @return The level the specified enchantment has, or 0 if none.
     */
    public open fun getEnchantLevel(enchantment: Enchantment): Int = result.itemMeta.getEnchantLevel(enchantment)

    public open fun addEnchant(enchantment: Enchantment, level: Int): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.addEnchant(enchantment, level, true)
        result.itemMeta = itemMeta
        return this
    }

    public open fun addEnchant(enchantment: Enchantment): ItemBuilder = addEnchant(enchantment, 1)

    public open fun removeEnchant(enchantment: Enchantment): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.removeEnchant(enchantment)
        result.itemMeta = itemMeta
        return this
    }

    public open fun addAttributeModifier(attribute: Attribute, modifier: AttributeModifier): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.addAttributeModifier(attribute, modifier)
        result.itemMeta = itemMeta
        return this
    }

    public open fun removeAttributeModifier(attribute: Attribute, modifier: AttributeModifier): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.removeAttributeModifier(attribute, modifier)
        result.itemMeta = itemMeta
        return this
    }

    public open fun removeAttributeModifier(attribute: Attribute): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.removeAttributeModifier(attribute)
        result.itemMeta = itemMeta
        return this
    }

    public open fun getItemFlags(): Set<ItemFlag> = result.itemMeta.itemFlags

    public open fun addItemFlags(vararg itemFlags: ItemFlag): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.addItemFlags(*itemFlags)
        result.itemMeta = itemMeta
        return this
    }

    public open fun removeItemFlags(vararg itemFlags: ItemFlag): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.removeItemFlags(*itemFlags)
        result.itemMeta = itemMeta
        return this
    }

    public open fun getUnbreakable(): Boolean = result.itemMeta.isUnbreakable

    public open fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.isUnbreakable = unbreakable
        result.itemMeta = itemMeta
        return this
    }

    public open fun recipes(): List<Recipe<*, *>> {
        return recipes.toList()
    }

    public open fun addRecipe(recipe: Recipe<*, *>): ItemBuilder {
        recipes.add(recipe)
        return this
    }

    public open fun getRecipe(key: NamespacedKey): Recipe<*, *>? {
        val item = build()
        return recipes.firstOrNull { it.key(item) == key }
    }

    public open fun removeRecipe(recipe: Recipe<*, *>): ItemBuilder {
        recipes.remove(recipe)
        return this
    }

    public open fun removeRecipe(recipeType: Class<out Recipe<*, *>>): ItemBuilder {
        val iterator = recipes.iterator()

        while (iterator.hasNext()) {
            val recipe = iterator.next()

            if (recipeType.isAssignableFrom(recipe.javaClass)) {
                iterator.remove()
            }
        }

        return this
    }

    public open fun clearRecipes(): ItemBuilder {
        recipes.clear()
        return this
    }

    /**
     * Register an [EventHandlerGroup]
     */
    public fun <T : Event> addEventHandler(eventHandlerGroup: EventHandlerGroup<T>): ItemBuilder {
        eventHandlerGroups.add(eventHandlerGroup)
        return this
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
    ): ItemBuilder = addEventHandler(
        EventHandlerGroup(
            eventType, predicate, handler
        )
    )

    /**
     * Get all event handler groups that are registered for [eventType]
     *
     * @see EventHandlerGroup
     */
    @Suppress("UNCHECKED_CAST")
    public fun <T : Event> getEventHandlers(eventType: Class<out T>): List<EventHandlerGroup<in T>> =
        eventHandlerGroups.filter { it.eventType == eventType } as List<EventHandlerGroup<in T>>

    /**
     * Get all event handler groups that are registered for [eventType] with [predicate]
     *
     * @see EventHandlerGroup
     */
    @Suppress("UNCHECKED_CAST")
    public fun <T : Event> getEventHandlers(
        eventType: Class<out T>, predicate: EventPredicate<T>
    ): List<EventHandlerGroup<in T>> =
        eventHandlerGroups.filter { it.eventType == eventType && it.predicate == predicate } as List<EventHandlerGroup<in T>>


    /**
     * Get all event handler groups that are registered for [eventType] with [predicate] and [handler]
     *
     * @see EventHandlerGroup
     */
    @Suppress("UNCHECKED_CAST")
    public fun <T : Event> getEventHandlers(
        eventType: Class<out T>, predicate: EventPredicate<T>, handler: EventHandler<T>
    ): List<EventHandlerGroup<in T>> =
        eventHandlerGroups.filter { it.eventType == eventType && it.predicate == predicate && it.handler == handler } as List<EventHandlerGroup<in T>>

    /**
     * Remove all event handlers for [eventType]
     */
    public open fun <T : Event> removeEventHandlers(eventType: Class<T>) {
        val handlers = getEventHandlers(eventType)
        handlers.forEach {
            removeEventHandler(it)
        }
    }

    /**
     * Remove all event handlers for [eventType] with [predicate]
     */
    public open fun <T : Event> removeEventHandlers(eventType: Class<T>, predicate: EventPredicate<T>) {
        val handlers = getEventHandlers(eventType, predicate)
        handlers.forEach {
            removeEventHandler(it)
        }
    }

    /**
     * Remove all event handlers for [eventType] with [predicate] and [handler]
     */
    public open fun <T : Event> removeEventHandlers(
        eventType: Class<T>, predicate: EventPredicate<T>, handler: EventHandler<T>
    ) {
        val handlers = getEventHandlers(eventType, predicate, handler)
        handlers.forEach {
            removeEventHandler(it)
        }
    }

    public open fun <T : Event> removeEventHandler(eventHandlerGroup: EventHandlerGroup<T>) {
        eventHandlerGroups.remove(eventHandlerGroup)
    }

    public fun getEventTypes(): Set<Class<out Event>> = eventHandlerGroups.map { it.eventType }.toSet()

    public open fun build(): Item {
        val item = Item(id, result, recipes)
        eventHandlerGroups.forEach {
            item.addEventHandler(it)
        }
        return item
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ItemBuilder) return false
        return other.build() == this.build()
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + result.hashCode()
        result = 31 * result + recipes.hashCode()
        return result
    }

    public companion object {
        /**
         * Apply default formatting to an item [name].
         *
         * @see unformatName
         */
        @Contract("!null -> !null, null -> null")
        public fun formatName(name: Component?): Component? {
            return name?.color(
                name.color() ?: NamedTextColor.WHITE
            )?.decorationIfAbsent(
                TextDecoration.ITALIC, TextDecoration.State.FALSE
            )
        }

        /**
         * Remove default formatting to an item [name].
         *
         * @see formatName
         */
        @Contract("!null -> !null, null -> null")
        public fun unformatName(name: Component?): Component? {
            return name?.color(name.color().takeIf { it != NamedTextColor.WHITE })?.decoration(
                TextDecoration.ITALIC, if (name.decoration(TextDecoration.ITALIC) == TextDecoration.State.TRUE) {
                    TextDecoration.State.TRUE
                } else {
                    TextDecoration.State.NOT_SET
                }
            )
        }

        /**
         * Apply default formatting to a line of [lore]
         *
         * @see unformatLore
         */
        @Contract("!null -> !null, null -> null")
        public fun formatLore(lore: Component?): Component? {
            return lore?.color(
                lore.color() ?: NamedTextColor.GRAY
            )?.decorationIfAbsent(
                TextDecoration.ITALIC, TextDecoration.State.FALSE
            )
        }

        /**
         * Remove default formatting to a line of [lore].
         *
         * @see formatLore
         */
        @Contract("!null -> !null, null -> null")
        public fun unformatLore(lore: Component?): Component? {
            return lore?.color(lore.color().takeIf { it != NamedTextColor.GRAY })?.decoration(
                TextDecoration.ITALIC, if (lore.decoration(TextDecoration.ITALIC) == TextDecoration.State.TRUE) {
                    TextDecoration.State.TRUE
                } else {
                    TextDecoration.State.NOT_SET
                }
            )
        }
    }
}