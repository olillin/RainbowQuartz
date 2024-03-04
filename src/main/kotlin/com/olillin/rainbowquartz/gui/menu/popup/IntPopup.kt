package com.olillin.rainbowquartz.gui.menu.popup

import com.olillin.rainbowquartz.gui.menu.Menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/** [TextPopup] that provides an int. */
public class IntPopup(viewer: HumanEntity, placeholder: Int?, previousMenu: Menu?, callback: (Int) -> Unit) :
    TextPopup<Int>(viewer, placeholder, previousMenu, callback) {

    override fun parseInput(input: String?): Int? = input
        ?.replace(Regex("\\D"), "")
        ?.toIntOrNull()

    override fun firstItem(placeholder: Int?): ItemStack {
        val stack = ItemStack(
            if (placeholder == null) Material.MAP
            else Material.FILLED_MAP
        )
        val meta = stack.itemMeta

        meta.displayName(
            Component.text(placeholder?.toString() ?: "New number")
                .color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
        )
        if (placeholder != null) {
            meta.lore(
                listOf(
                    Component.text("↑ Previous number ↑").color(NamedTextColor.GRAY)
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
                        .append(Component.text(value.toString()).color(NamedTextColor.WHITE))
                )
            )
            addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
        }
        return stack
    }
}