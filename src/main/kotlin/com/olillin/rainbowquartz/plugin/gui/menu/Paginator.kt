package com.olillin.rainbowquartz.plugin.gui.menu

import com.olillin.rainbowquartz.plugin.gui.LinkItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

internal object Paginator {
    private const val INVENTORY_WIDTH = 9
    private val supportedInventoryTypes: Set<InventoryType> = hashSetOf(
        InventoryType.BARREL,
        InventoryType.CHEST,
        InventoryType.ENDER_CHEST,
        InventoryType.PLAYER,
        InventoryType.SHULKER_BOX,
    )

    fun <T> render(
        inventory: Inventory,
        content: List<T>,
        transform: (T) -> ItemStack,
        page: Int,
        width: Int,
        height: Int,
        x: Int = 0,
        y: Int = 0
    ) {
        if (!supportedInventoryTypes.contains(inventory.type)) {
            throw IllegalArgumentException("Unsupported inventory type ${inventory.type}, inventory width must be 9")
        }
        if (width < 2) throw IllegalArgumentException("Width must be at least 2")
        if (height < 3) throw IllegalArgumentException("Height must be at least 3")
        val inventoryHeight = inventory.size / INVENTORY_WIDTH
        if (x < 0 || y < 0
            || width + x > INVENTORY_WIDTH
            || height + y > inventoryHeight
        ) {
            throw IllegalArgumentException("Paginator outside of inventory bounds")
        }

        val itemWidth = width - 1
        val itemsPerPage = itemWidth * height
        val start = itemsPerPage * page
        val end = min(start + itemsPerPage, content.size)
        val shownItems = end - start
        val pages = ceil(content.size.toDouble() / itemsPerPage).toInt()

        val iterator: Iterator<ItemStack> = content.subList(start, end).map(transform).iterator()

        for (slotY in y until y + height) {
            for (slotX in x until x + itemWidth) {
                val item: ItemStack = if (iterator.hasNext()) {
                    iterator.next()
                } else {
                    ItemStack(Material.AIR)
                }

                val slot = slotX + slotY * INVENTORY_WIDTH
                inventory.setItem(slot, item)
            }
        }

        inventory.setItem(
            x + itemWidth + y * INVENTORY_WIDTH,
            if (page > 0) LinkItem.makeLink(
                "previous_page",
                Material.ARROW,
                Component.text("↑ Previous Page ↑").color(NamedTextColor.GREEN),
                listOf(
                    Component.text("Page ")
                        .append(Component.text("$page"))
                        .append(Component.text("/$pages"))
                )
            ) else Menu.EMPTY_PANEL
        )

        inventory.setItem(
            x + itemWidth + (y + 1) * INVENTORY_WIDTH,
            LinkItem.makeLink(
                "page_indicator",
                Material.PAPER,
                Component.text("Page ${page + 1}/${max(pages, 1)}").color(NamedTextColor.YELLOW),
                listOf(
                    Component.text("Showing ${shownItems}/${content.size} items")
                )
            )
        )
        inventory.setItem(
            x + itemWidth + (y + 2) * INVENTORY_WIDTH,
            if (page < pages - 1) LinkItem.makeLink(
                "next_page",
                Material.ARROW,
                Component.text("↓ Next Page ↓").color(NamedTextColor.GREEN),
                listOf(
                    Component.text("Page ")
                        .append(Component.text("${page + 2}"))
                        .append(Component.text("/$pages"))
                )
            ) else Menu.EMPTY_PANEL
        )
        for (dy in 3 until height) {
            inventory.setItem(x + itemWidth + (y + dy) * INVENTORY_WIDTH, Menu.EMPTY_PANEL)
        }
    }
}