package com.olillin.rainbowquartz.plugin.gui.menu.popup

import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.TextDecoration.ITALIC
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemFlag.HIDE_ITEM_SPECIFICS
import org.bukkit.inventory.ItemStack

/** A [TextPopup] menu that provides a [NamespacedKey] */
class NamespacedKeyPopup(
    viewer: HumanEntity,
    placeholder: NamespacedKey? = null,
    private val defaultNamespace: String = "rainbowquartz",
    previousMenu: Menu?,
    callback: (NamespacedKey) -> Unit
) : TextPopup<NamespacedKey>(viewer, placeholder, previousMenu, callback) {
    override fun firstItem(placeholder: NamespacedKey?): ItemStack {
        val stack = ItemStack(
            if (placeholder == null) Material.MAP
            else Material.FILLED_MAP
        )
        val meta = stack.itemMeta

        meta.displayName(
            Component.text(placeholder?.toString() ?: "id")
                .color(YELLOW).decoration(ITALIC, false)
        )
        if (placeholder != null) {
            meta.lore(
                listOf(
                    Component.text("↑ Previous id ↑").color(GRAY).decoration(ITALIC, false)
                )
            )
        }
        meta.addItemFlags(HIDE_ITEM_SPECIFICS)

        stack.itemMeta = meta
        return stack
    }

    override fun resultItem(input: String?): ItemStack {
        val value = parseInput(input) ?: return INVALID_INPUT

        val stack = ItemStack(Material.FILLED_MAP)
        stack.itemMeta = stack.itemMeta.apply {
            displayName(Component.text("Submit").color(GREEN).decoration(ITALIC, false))
            lore(
                listOf(
                    Component.text("Id: ").color(GRAY).decoration(ITALIC, false)
                        .append(Component.text(value.toString()).color(WHITE))
                )
            )
            addItemFlags(HIDE_ITEM_SPECIFICS)
        }
        return stack
    }

    override fun parseInput(input: String?): NamespacedKey? {
        var text: String = (input?.trim().takeIf { it?.isNotEmpty() == true })
            ?: (placeholder?.toString().takeIf { it?.isNotEmpty() == true })
            ?: return null
        if (!text.matches(Regex("^([0-9a-z_.-]+:)?[0-9a-z_.-]+(/[0-9a-z_.-]+)*$"))) {
            // Invalid resource location
            return null
        }
        if (!text.contains(':')) {
            // No namespace provided
            text = "$defaultNamespace:$text"
        }
        return NamespacedKey.fromString(text)
    }
}