package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.item.rainbowQuartzId
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.linkKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

public object ResultPreview {
    private const val INVENTORY_WIDTH = 9
    public fun render(inventory: Inventory, result: ItemStack, amount: Int, x: Int, y: Int) {
        if (x < 0 || y < 0
            || x + 3 > INVENTORY_WIDTH
            || y + 3 > inventory.size / INVENTORY_WIDTH
        ) throw IllegalArgumentException("Preview outside of inventory bounds")

        val stack = previewItem(result, amount)
        var meta = stack.itemMeta
        meta.rainbowQuartzId = null
        stack.itemMeta = meta
        stack.amount = amount

        // Increase amount buttons
        inventory.setItem(
            x + 0 + y * INVENTORY_WIDTH, LinkItem.makeLink(
                "add_amount_1",
                Material.LIME_STAINED_GLASS_PANE,
                1,
                Component.text("Add 1").color(NamedTextColor.GREEN),
                null
            )
        )
        inventory.setItem(
            x + 1 + y * INVENTORY_WIDTH, LinkItem.makeLink(
                "add_amount_16",
                Material.LIME_STAINED_GLASS_PANE,
                16,
                Component.text("Add 16").color(NamedTextColor.GREEN),
                null
            )
        )
        inventory.setItem(
            x + 2 + y * INVENTORY_WIDTH, LinkItem.makeLink(
                "set_amount_max",
                Material.LIME_STAINED_GLASS_PANE,
                64,
                Component.text("Set max amount").color(NamedTextColor.GREEN),
                null
            )
        )

        // Item preview
        val resultPanel = ItemStack(Material.PURPLE_STAINED_GLASS_PANE)
        meta = resultPanel.itemMeta
        meta.displayName(
            Component.text("Recipe result")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
        )
        resultPanel.itemMeta = meta

        inventory.setItem(x + 0 + (y + 1) * INVENTORY_WIDTH, resultPanel)
        inventory.setItem(x + 1 + (y + 1) * INVENTORY_WIDTH, stack)
        inventory.setItem(x + 2 + (y + 1) * INVENTORY_WIDTH, resultPanel)

        // Decrease amount buttons
        inventory.setItem(
            x + 0 + (y + 2) * INVENTORY_WIDTH, LinkItem.makeLink(
                "remove_amount_1",
                Material.RED_STAINED_GLASS_PANE,
                1,
                Component.text("Remove 1").color(NamedTextColor.RED),
                null
            )
        )
        inventory.setItem(
            x + 1 + (y + 2) * INVENTORY_WIDTH, LinkItem.makeLink(
                "remove_amount_16",
                Material.RED_STAINED_GLASS_PANE,
                16,
                Component.text("Remove 16").color(NamedTextColor.RED),
                null
            )
        )
        inventory.setItem(
            x + 2 + (y + 2) * INVENTORY_WIDTH, LinkItem.makeLink(
                "set_amount_min",
                Material.RED_STAINED_GLASS_PANE,
                1,
                Component.text("Set min amount").color(NamedTextColor.RED),
                null
            )
        )
    }

    private fun previewItem(item: ItemStack, amount: Int): ItemStack {
        val stack = ItemStack(item)
        val meta = stack.itemMeta
        meta.rainbowQuartzId = null
        meta.linkKey = null
        stack.itemMeta = meta
        stack.amount = amount
        return stack
    }
}