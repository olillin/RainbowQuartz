package com.olillin.rainbowquartz.plugin.gui.menu.popup

import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/** [TextPopup] that provides a [String]. */
public class StringPopup(viewer: HumanEntity, placeholder: String?, previousMenu: Menu?, callback: (String) -> Unit) :
    TextPopup<String>(viewer, placeholder, previousMenu, callback) {

    override fun parseInput(input: String?): String? = input

    override fun firstItem(placeholder: String?): ItemStack {
        val stack = ItemStack(
            if (placeholder == null) Material.MAP
            else Material.FILLED_MAP
        )
        val meta = stack.itemMeta

        meta.displayName(
            Component.text(placeholder ?: "text")
                .color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
        )
        if (placeholder != null) {
            meta.lore(
                listOf(
                    Component.text("↑ Previous text ↑").color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                )
            )
        }
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)

        stack.itemMeta = meta
        return stack
    }

    override fun resultItem(input: String?): ItemStack {
        val value = parseInput(input) ?: return INVALID_INPUT

        val stack = ItemStack(Material.FILLED_MAP)
        stack.itemMeta = stack.itemMeta.apply {
            displayName(Component.text("Submit").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
            lore(
                listOf(
                    Component.text("Value: ").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(value).color(NamedTextColor.WHITE))
                )
            )
            addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
        }
        return stack
    }
}