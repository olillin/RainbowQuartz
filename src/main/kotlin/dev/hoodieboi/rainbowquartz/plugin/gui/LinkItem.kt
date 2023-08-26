package dev.hoodieboi.rainbowquartz.plugin.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.persistence.PersistentDataType

object LinkItem {
    private val keyResourceLocation = NamespacedKey.fromString("rainbowquartz_i:gui_link")!!

    fun makeLink(
        key: String,
        material: Material,
        amount: Int = 1,
        name: Component?,
        lore: List<Component>?
    ): ItemStack {
        val item = ItemStack(material, amount)
        val meta = item.itemMeta
        // Decorate item
        meta.displayName(decorateName(name))
        meta.lore(decorateLore(lore))
        meta.addItemFlags(*ItemFlag.values())
        item.itemMeta = meta

        return setLinkKey(item, key)
    }

    fun makeLink(key: String, material: Material, name: Component?, lore: List<Component>?): ItemStack {
        return makeLink(key, material, 1, name, lore)
    }

    fun makeLink(key: String, material: Material, name: String, lore: List<Component>?): ItemStack {
        return makeLink(key, material, 1, Component.text(name), lore)
    }
    fun makeLink(key: String, material: Material, name: Component?): ItemStack {
        return makeLink(key, material, name, null)
    }

    fun makeLink(key: String, material: Material, name: String): ItemStack {
        return makeLink(key, material, Component.text(name), null)
    }

    fun setLinkKey(stack: ItemStack, key: String?): ItemStack {
        val item = ItemStack(stack)
        val meta = item.itemMeta
        if (key == null) {
            meta.persistentDataContainer.remove(keyResourceLocation)
        } else {
            meta.persistentDataContainer.set(keyResourceLocation, PersistentDataType.STRING, key)
        }
        item.itemMeta = meta
        return item
    }

    fun getLinkKey(stack: ItemStack): String? {
        val container = stack.itemMeta?.persistentDataContainer ?: return null
        return container.get(keyResourceLocation, PersistentDataType.STRING)
    }

    private fun decorateName(name: Component?): Component? {
        if (name == null) return null
        return name
            .color(name.color() ?: NamedTextColor.WHITE)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
    }

    private fun decorateLore(lore: List<Component>?): List<Component>? {
        if (lore == null) return null
        return lore.map {
            it.color(it.color() ?: NamedTextColor.GRAY)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        }
    }

    fun isMenuItemClick(clickEvent: InventoryClickEvent): Boolean {
        if (clickEvent.slotType == InventoryType.SlotType.OUTSIDE
            || clickEvent.currentItem == null
            || clickEvent.clickedInventory is PlayerInventory) return false
        // Only normal left and right click allowed
        return when (clickEvent.click) {
            ClickType.LEFT, ClickType.RIGHT -> true
            else -> false
        }
    }

    val BACK get() = makeLink("back", Material.ARROW, Component.text("Back").color(NamedTextColor.RED))
    val CLOSE get() = makeLink("close", Material.BARRIER, Component.text("Close menu").color(NamedTextColor.RED))
    val SUBMIT get() = makeLink("submit", Material.LIME_GLAZED_TERRACOTTA, Component.text("Submit").color(NamedTextColor.GREEN))
    val CANCEL get() = makeLink("cancel", Material.BARRIER, Component.text("Cancel").color(NamedTextColor.RED)
    )
}

fun ItemStack.enchanted(): ItemStack {
    val item = ItemStack(this)
    val meta = item.itemMeta
    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
    item.itemMeta = meta
    return item
}