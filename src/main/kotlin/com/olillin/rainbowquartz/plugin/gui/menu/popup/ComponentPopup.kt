package com.olillin.rainbowquartz.plugin.gui.menu.popup

import com.olillin.rainbowquartz.item.ItemBuilder
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import com.olillin.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class ComponentPopup(
        override val viewer: HumanEntity, placeholder: Component? = null, override val previousMenu: Menu?, override val callback: (Component) -> Unit
) : TextPopup<Component>(viewer, placeholder, previousMenu, callback) {
    override fun firstItem(placeholder: Component?): ItemStack {
        return ItemStack(Material.NAME_TAG).apply {
            val name: String = if (placeholder != null) {
                serializeName(placeholder)
            } else ""

            val meta = itemMeta
            meta.displayName(
                Component.text(name)
                    .color(WHITE)
                    .decoration(TextDecoration.ITALIC, false)
            )
            itemMeta = meta
        }
    }

    override fun resultItem(input: String?): ItemStack {
        if (input == null) {
            return ItemStack(Material.BARRIER).apply {
                itemMeta = itemMeta.apply {
                    displayName(Component.text("Must be different than current value").color(RED))
                }
            }
        }
        val name = parseInput(input)
        return ItemStack(Material.NAME_TAG).apply {
            itemMeta = itemMeta.apply {
                displayName(name)
            }
        }
    }

    private fun serializeName(name: Component): String {
        return legacySerializer.serialize(ItemBuilder.unformatName(name)!!)
    }

    override fun parseInput(input: String?): Component? {
        input ?: return null
        return ItemBuilder.formatName(legacySerializer.deserialize(input))!!
    }

    companion object {
        val legacySerializer = LegacyComponentSerializer.legacy('&')
    }
}