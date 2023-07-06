package dev.hoodieboi.rainbowquartz.item

import dev.hoodieboi.rainbowquartz.event.Event
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin

class Item (val key: NamespacedKey, val result: ItemStack, private val recipes: List<Recipe>) {
    val listeners : MutableMap<Class<out Event>, MutableList<Listener>> = HashMap()

    /**
     * Registers all the events in the given listener class
     *
     * @param listener Listener to register
     * @param plugin Plugin to register
     */
    fun registerEvents(listener: Listener, plugin: Plugin) {
        TODO()
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
    fun registerEvent(
        event: Class<out org.bukkit.event.Event?>,
        listener: Listener,
        priority: EventPriority,
        executor: EventExecutor,
        plugin: Plugin
    ) {
        TODO()
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
            val itemMeta = result.itemMeta
            itemMeta.displayName(name)
            result.itemMeta = itemMeta
            return this
        }
        fun setName(name: String): ItemBuilder {
            val itemMeta = result.itemMeta
            itemMeta.displayName(Component.text(name).color(NamedTextColor.WHITE).decorate())
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

        init {
            recipes = ArrayList()
        }
        fun build() = Item(key, result, recipes)
    }
}