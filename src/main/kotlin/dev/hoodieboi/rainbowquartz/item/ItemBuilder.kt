package dev.hoodieboi.rainbowquartz.item

import dev.hoodieboi.rainbowquartz.craft.Recipe
import dev.hoodieboi.rainbowquartz.event.EventPredicate
import dev.hoodieboi.rainbowquartz.event.PredicatedEventHandler
import dev.hoodieboi.rainbowquartz.event.EventHandler
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

open class ItemBuilder(val key: NamespacedKey, result: ItemStack, recipes: List<Recipe>, handlers: MutableMap<Class<out Event>, MutableSet<PredicatedEventHandler<*>>>) {
    constructor(key: NamespacedKey, itemStack: ItemStack) : this(key, itemStack, mutableListOf(), mutableMapOf())
    constructor(key: NamespacedKey, material: Material) : this(key, ItemStack(material))
    constructor(key: NamespacedKey, itemStack: ItemStack, recipes: List<Recipe>) : this(key, itemStack, recipes, mutableMapOf())
    constructor(key: NamespacedKey, material: Material, recipes: List<Recipe>) : this(key, ItemStack(material), recipes, mutableMapOf())
    constructor(key: NamespacedKey, itemStack: ItemStack, handlers: MutableMap<Class<out Event>, MutableSet<PredicatedEventHandler<*>>>) : this(key, itemStack, mutableListOf(), handlers)
    constructor(key: NamespacedKey, material: Material, handlers: MutableMap<Class<out Event>, MutableSet<PredicatedEventHandler<*>>>) : this(key, ItemStack(material), mutableListOf(), handlers)
    constructor(item: Item) : this(item.key, item.item, item.recipes)
    constructor(builder: ItemBuilder) : this(builder.key, builder.result, builder.recipes)

    protected val result: ItemStack
    protected val recipes: MutableList<Recipe>
    protected val handlers: MutableMap<Class<out Event>, MutableSet<PredicatedEventHandler<*>>>

    init {
        this.result = ItemStack(result)
        this.recipes = recipes.toMutableList()
        this.handlers = handlers.map {
            it.key to it.value.toMutableSet()
        }.toMap().toMutableMap()
    }

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

    fun getName(): Component? {
        return result.itemMeta.displayName()
    }

    fun setName(name: Component?): ItemBuilder {
        // Modify item
        val itemMeta = result.itemMeta
        itemMeta.displayName(Item.formatName(name))
        result.itemMeta = itemMeta
        return this
    }

    fun setName(name: String): ItemBuilder {
        return setName(Component.text(name))
    }

    fun hasName(): Boolean {
        return getName() != null
    }

    fun getLore(): List<Component>? {
        return result.itemMeta.lore()
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

    fun hasLore(): Boolean {
        return getLore() != null
    }

    fun getEnchants(): Map<Enchantment, Int> {
        return result.itemMeta.enchants
    }

    /**
     * Checks for the level of the specified enchantment.
     *
     * @param enchantment The enchantment to check.
     * @return The level the specified enchantment has, or 0 if none.
     */
    fun getEnchantLevel(enchantment: Enchantment): Int {
        return result.itemMeta.getEnchantLevel(enchantment)
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

    fun getItemFlags(): Set<ItemFlag> {
        return result.itemMeta.itemFlags
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

    fun getUnbreakable(): Boolean {
        return result.itemMeta.isUnbreakable
    }

    fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
        val itemMeta = result.itemMeta
        itemMeta.isUnbreakable = unbreakable
        result.itemMeta = itemMeta
        return this
    }

    fun recipes(): List<Recipe> {
        return recipes.toList()
    }

    fun addRecipe(recipe: Recipe): ItemBuilder {
        recipes.add(recipe)
        return this
    }

    fun removeRecipe(recipe: Recipe): ItemBuilder {
        recipes.remove(recipe)
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

    fun clearRecipes(): ItemBuilder {
        recipes.clear()
        return this
    }

    /**
     * Register an [EventHandler] to be executed when an [EventPredicate] is successful
     *
     * @param eventType The event class
     * @param predicate The predicate to check before calling the handler
     * @param handler What should happen when the predicate is successful
     */
    fun <T : Event> addEventHandler(eventType: Class<T>, predicate: EventPredicate<T>, handler: EventHandler<T>): ItemBuilder {
        if (!handlers.containsKey(eventType)) {
            handlers[eventType] = mutableSetOf()
        }
        handlers[eventType]!!.add(PredicatedEventHandler(predicate, handler))
        return this
    }

    /**
     * Register an [EventHandler] to be executed when an [EventPredicate] is successful
     *
     * @param eventType The event class
     * @param predicate The predicate to check before calling the handler
     * @param handler What should happen when the predicate is successful
     */
    fun <T : Event> removeEventHandler(eventType: Class<T>, predicate: EventPredicate<T>, handler: EventHandler<T>) {
        if (!handlers.containsKey(eventType)) {
            handlers[eventType] = mutableSetOf()
        }
        handlers[eventType]!!.add(PredicatedEventHandler(predicate, handler))
    }

    fun build(): Item = Item(key, result, recipes, handlers)

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
}