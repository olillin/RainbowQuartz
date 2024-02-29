package com.olillin.rainbowquartz.plugin.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

public object LinkItem {
    internal val keyResourceLocation = NamespacedKey.fromString("rainbowquartz_i:gui_link")!!

    public fun makeLink(
        key: String,
        material: Material,
        amount: Int = 1,
        name: Component?,
        lore: List<Component>?
    ): ItemStack {
        val item = ItemStack(material, amount).apply {
            itemMeta = itemMeta.apply {
                displayName(decorateName(name))
                lore(decorateLore(lore))
                addItemFlags(*ItemFlag.values())
                linkKey = key
            }
        }
        return item
    }

    public fun makeLink(key: String, material: Material, name: Component?, lore: List<Component>?): ItemStack {
        return makeLink(key, material, 1, name, lore)
    }

    public fun makeLink(key: String, material: Material, name: String, lore: List<Component>?): ItemStack {
        return makeLink(key, material, 1, Component.text(name), lore)
    }

    public fun makeLink(key: String, material: Material, name: Component?): ItemStack {
        return makeLink(key, material, name, null)
    }

    public fun makeLink(key: String, material: Material, name: String): ItemStack {
        return makeLink(key, material, Component.text(name), null)
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

    @JvmStatic
    public val BACK: ItemStack
        get() = makeLink(
            "back",
            Material.ARROW,
            Component.text("Back").color(NamedTextColor.RED),
            listOf(Component.text("Click to return to the previous menu"))
        )
    @JvmStatic
    public val CLOSE: ItemStack
        get() = makeLink(
            "close",
            Material.BARRIER,
            Component.text("Close menu").color(NamedTextColor.RED),
            listOf(Component.text("Click to close the menu"))
        )
    @JvmStatic
    public val SUBMIT: ItemStack
        get() = makeLink(
            "submit",
            Material.LIME_GLAZED_TERRACOTTA,
            Component.text("Submit").color(NamedTextColor.GREEN)
        )
    @JvmStatic
    public val CANCEL: ItemStack
        get() = makeLink(
            "cancel",
            Material.BARRIER,
            Component.text("Cancel").color(NamedTextColor.RED),
            listOf(Component.text("Click to cancel"))
        )
}

public var ItemMeta.linkKey: String?
    get() = persistentDataContainer.get(LinkItem.keyResourceLocation, PersistentDataType.STRING)
    set(value) {
        if (value == null) {
            persistentDataContainer.remove(LinkItem.keyResourceLocation)
        } else {
            persistentDataContainer.set(LinkItem.keyResourceLocation, PersistentDataType.STRING, value)
        }
    }
public var ItemStack.linkKey: String?
    get() = itemMeta.linkKey
    set(value) {
        itemMeta = itemMeta.apply {
            linkKey = value
        }
    }

/** Add enchantment glow to an item. */
public fun ItemStack.enchanted(): ItemStack {
    val item = ItemStack(this)
    val meta = item.itemMeta
    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
    item.itemMeta = meta
    return item
}