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

@Suppress("UNUSED")
open class ItemBuilder(val key: NamespacedKey, result: ItemStack, recipes: List<Recipe>, handlers: List<EventHandlerGroup<*>>) {
    constructor(key: NamespacedKey, itemStack: ItemStack) : this(key, itemStack, listOf(), listOf())
    constructor(key: NamespacedKey, material: Material) : this(key, ItemStack(material))
    constructor(key: NamespacedKey, itemStack: ItemStack, recipes: List<Recipe>) : this(key, itemStack, recipes, listOf())
    constructor(key: NamespacedKey, material: Material, recipes: List<Recipe>) : this(key, ItemStack(material), recipes, listOf())
    constructor(key: NamespacedKey, material: Material, recipes: List<Recipe>, handlers: List<EventHandlerGroup<*>>) : this(key, ItemStack(material), recipes, handlers)
    constructor(item: Item) : this(item.key, item.getItem(), item.recipes)
    constructor(builder: ItemBuilder) : this(builder.key, builder.result, builder.recipes)

    protected val result: ItemStack
    protected val recipes: MutableList<Recipe>
    protected val eventHandlerGroups: MutableList<EventHandlerGroup<*>>

    init {
        this.result = ItemStack(result).also { it.amount = 1 }
        this.recipes = recipes.toMutableList()
        this.eventHandlerGroups = handlers.toMutableList()
    }

    open fun getMaterial() = result.type
    open fun setMaterial(material: Material): ItemBuilder {
        result.type = material
        return this
    }

    open fun getAmount() = result.amount
    open fun setAmount(amount: Int): ItemBuilder {
        result.amount = amount
        return this
    }

    open fun getName(): Component? {
        return result.itemMeta.displayName()
    }

    open fun setName(name: Component?): ItemBuilder {
        // Modify item
        val itemMeta = result.itemMeta
        itemMeta.displayName(formatName(name))
        result.itemMeta = itemMeta
        return this
    }

    open fun setName(name: String): ItemBuilder {
        return setName(Component.text(name))
    }

    open fun hasName(): Boolean {
        return getName() != null
    }

    open fun getLore(): List<Component>? {
        return result.itemMeta.lore()
    }

    open fun setLore(lore: List<Component>?): ItemBuilder {
        result.itemMeta = result.itemMeta.apply {
            lore(lore?.map { formatLore(it) })
        }
        return this
    }

    open fun addLore(vararg lore: Component): ItemBuilder {
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

    open fun addLore(lore: String): ItemBuilder {
        return addLore(Component.text(lore).color(NamedTextColor.GRAY))
    }

    open fun addLore(index: Int, lore: Component): ItemBuilder {
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

    open fun addLore(index: Int, lore: String): ItemBuilder {
        return addLore(index, Component.text(lore))
    }

    open fun removeLore(): ItemBuilder = setLore(null)

    open fun hasLore(): Boolean {
        return getLore() != null
    }

    open fun getEnchants(): Map<Enchantment, Int> {
        return result.itemMeta.enchants
    }

    /**
     * Checks for the level of the specified enchantment.
     *
     * @param enchantment The enchantment to check.
     * @return The level the specified enchantment has, or 0 if none.
     */
    open fun getEnchantLevel(enchantment: Enchantment): Int {
        return result.itemMeta.getEnchantLevel(enchantment)
    }

    open fun addEnchant(enchantment: Enchantment, level: Int): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.addEnchant(enchantment, level, true)
        result.itemMeta = itemMeta
        return this
    }

    open fun addEnchant(enchantment: Enchantment): ItemBuilder {
        return addEnchant(enchantment, 1)
    }

    open fun removeEnchant(enchantment: Enchantment): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.removeEnchant(enchantment)
        result.itemMeta = itemMeta
        return this
    }

    open fun addAttributeModifier(attribute: Attribute, modifier: AttributeModifier): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.addAttributeModifier(attribute, modifier)
        result.itemMeta = itemMeta
        return this
    }

    open fun removeAttributeModifier(attribute: Attribute, modifier: AttributeModifier): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.removeAttributeModifier(attribute, modifier)
        result.itemMeta = itemMeta
        return this
    }

    open fun removeAttributeModifier(attribute: Attribute): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.removeAttributeModifier(attribute)
        result.itemMeta = itemMeta
        return this
    }

    open fun getItemFlags(): Set<ItemFlag> {
        return result.itemMeta.itemFlags
    }

    open fun addItemFlags(vararg itemFlags: ItemFlag): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.addItemFlags(*itemFlags)
        result.itemMeta = itemMeta
        return this
    }

    open fun removeItemFlags(vararg itemFlags: ItemFlag): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.removeItemFlags(*itemFlags)
        result.itemMeta = itemMeta
        return this
    }

    open fun getUnbreakable(): Boolean {
        return result.itemMeta.isUnbreakable
    }

    open fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.isUnbreakable = unbreakable
        result.itemMeta = itemMeta
        return this
    }

    open fun recipes(): List<Recipe> {
        return recipes.toList()
    }

    open fun addRecipe(recipe: Recipe): ItemBuilder {
        recipes.add(recipe)
        return this
    }

    open fun getRecipe(key: NamespacedKey): Recipe? {
        val item = build()
        return recipes.firstOrNull { it.key(item) == key }
    }

    open fun removeRecipe(recipe: Recipe): ItemBuilder {
        recipes.remove(recipe)
        return this
    }

    open fun removeRecipe(recipeType: Class<out Recipe>): ItemBuilder {
        val iterator = recipes.iterator()

        while (iterator.hasNext()) {
            val recipe = iterator.next()

            if (recipeType.isAssignableFrom(recipe.javaClass)) {
                iterator.remove()
            }
        }

        return this
    }

    open fun clearRecipes(): ItemBuilder {
        recipes.clear()
        return this
    }

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
    open fun <T : Event> removeEventHandlers(eventType: Class<T>) {
        val handlers = getEventHandlers(eventType)
        handlers.forEach {
            removeEventHandler(it)
        }
    }

    /**
     * Remove all event handlers for [eventType] with [predicate]
     */
    open fun <T : Event> removeEventHandlers(eventType: Class<T>, predicate: EventPredicate<T>) {
        val handlers = getEventHandlers(eventType, predicate)
        handlers.forEach {
            removeEventHandler(it)
        }
    }

    /**
     * Remove all event handlers for [eventType] with [predicate] and [handler]
     */
    open fun <T : Event> removeEventHandlers(eventType: Class<T>, predicate: EventPredicate<T>, handler: EventHandler<T>) {
        val handlers = getEventHandlers(eventType, predicate, handler)
        handlers.forEach {
            removeEventHandler(it)
        }
    }

    open fun <T : Event> removeEventHandler(eventHandlerGroup: EventHandlerGroup<T>) {
        eventHandlerGroups.remove(eventHandlerGroup)
    }

    fun getEventTypes(): Set<Class<out Event>> {
        return eventHandlerGroups.map { it.eventType }.toSet()
    }

    open fun build(): Item {
        val item = Item(key, result, recipes)
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
        var result = key.hashCode()
        result = 31 * result + result.hashCode()
        result = 31 * result + recipes.hashCode()
        return result
    }

    companion object {
        /**
         * Apply default formatting to an item [name].
         *
         * @see unformatName
         */
        @JvmStatic
        @Contract("!null -> !null, null -> null")
        fun formatName(name: Component?): Component? {
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
        @JvmStatic
        @Contract("!null -> !null, null -> null")
        fun unformatName(name: Component?): Component? {
            return name?.color (
                    name.color().takeIf { it != NamedTextColor.WHITE}
            )?.decoration(
                    TextDecoration.ITALIC,
                    if (name.decoration(TextDecoration.ITALIC) == TextDecoration.State.TRUE) {
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
        @JvmStatic
        @Contract("!null -> !null, null -> null")
        fun formatLore(lore: Component?): Component? {
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
        @JvmStatic
        @Contract("!null -> !null, null -> null")
        fun unformatLore(lore: Component?): Component? {
            return lore?.color (
                    lore.color().takeIf { it != NamedTextColor.GRAY}
            )?.decoration(
                    TextDecoration.ITALIC,
                    if (lore.decoration(TextDecoration.ITALIC) == TextDecoration.State.TRUE) {
                        TextDecoration.State.TRUE
                    } else {
                        TextDecoration.State.NOT_SET
                    }
            )
        }
    }
}