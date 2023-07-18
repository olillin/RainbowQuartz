package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.item.Item
import dev.hoodieboi.rainbowquartz.item.rainbowQuartzId
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.Plugin
import kotlin.math.ceil

class ItemEditorMenu(val plugin: Plugin, override val id: Int) : Menu() {
    override fun showMenu(viewer: HumanEntity) {
        showMenu(viewer, 0)
    }

    fun showMenu(viewer: HumanEntity, page: Int) {
        val inventory = Bukkit.createInventory(viewer, 54, title("Item Editor"))

        // Stationary icons
        inventory.setItem(0, Item.ItemBuilder(NamespacedKey(plugin, "create_item"), Material.NETHER_STAR)
            .setName(Component.text("New item").color(NamedTextColor.AQUA))
            .addLore("Create a new Item")
            .build().item)
        inventory.setItem(1, EMPTY_ITEM)
        inventory.setItem(9, EMPTY_ITEM)
        inventory.setItem(10, EMPTY_ITEM)
        inventory.setItem(18, Item.ItemBuilder(NamespacedKey(plugin, "search"), Material.OAK_SIGN)
            .setName(Component.text("Search").color(NamedTextColor.LIGHT_PURPLE))
            .addLore("Find an item by id, name,")
            .addLore("item type or namespace")
            .build().item)
        inventory.setItem(19, EMPTY_ITEM)
        inventory.setItem(27, EMPTY_ITEM)
        inventory.setItem(28, EMPTY_ITEM)
        inventory.setItem(36, EMPTY_ITEM)
        inventory.setItem(37, EMPTY_ITEM)
        inventory.setItem(45, Item.ItemBuilder(NamespacedKey(plugin, "back"), Material.ARROW)
            .setName(Component.text("Back").color(NamedTextColor.RED))
            .build().item)
        inventory.setItem(46, EMPTY_ITEM)

        // Paginator
        val paginatorWidth = 6
        val paginatorHeight = 6
        val paginatorVolume = paginatorWidth * paginatorHeight
        render_paginator(inventory, page, 2, 0, paginatorWidth, paginatorHeight)
        val itemCount = RainbowQuartz.itemManager.itemKeys.size
        val pages = ceil(itemCount.toDouble() / paginatorVolume).toInt()
        val itemsShown = if(page < pages-1) paginatorVolume else itemCount % paginatorVolume

        inventory.setItem(8, if(page > 0) Item.ItemBuilder(NamespacedKey(plugin, "previous_page"), Material.ARROW)
            .setName(Component.text("↑ Previous Page ↑").color(NamedTextColor.GREEN))
            .addLore(Component.text("Page ")
                .append(Component.text("$page"))
                .append(Component.text("/$pages"))
            ).build().item else EMPTY_ITEM)
        inventory.setItem(17, Item.ItemBuilder(NamespacedKey(plugin, "page_indicator"), Material.PAPER)
            .setName(Component.text("Page ${page+1}/$pages").color(NamedTextColor.YELLOW))
            .addLore("Showing $itemsShown/$itemCount items")
            .build().item)
        inventory.setItem(26, if(page < pages-1) Item.ItemBuilder(NamespacedKey(plugin, "next_page"), Material.ARROW)
            .setName(Component.text("↓ Next Page ↓").color(NamedTextColor.GREEN))
            .addLore(Component.text("Page ")
                .append(Component.text("${page+2}"))
                .append(Component.text("/$pages"))
            ).build().item else EMPTY_ITEM)
        inventory.setItem(35, EMPTY_ITEM)
        inventory.setItem(44, EMPTY_ITEM)
        inventory.setItem(53, EMPTY_ITEM)

        viewer.openInventory(inventory)
    }

    fun render_paginator(inventory: Inventory, page: Int, x: Int, y: Int, width: Int, height: Int) {
        val iterator = RainbowQuartz.itemManager.itemKeys.iterator()
        // Skip hidden
        repeat (page * width * height) {
            if (!iterator.hasNext()) return
            iterator.next()
        }

        for (slotY in y until y+height) {
            for (slotX in x until x+width) {
                if (!iterator.hasNext()) return

                val item = RainbowQuartz.itemManager.getItem(iterator.next())!!
                val itemStack = listedItem(item)
                val slot = slotX + slotY*9
                inventory.setItem(slot, itemStack)
            }
        }
    }

    override fun onClick(event: InventoryClickEvent) {
        // Ignore player inventory
        if (event.clickedInventory is PlayerInventory) return
        event.isCancelled = true

        val item = event.inventory.getItem(event.slot) ?: return
        val id = item.itemMeta?.rainbowQuartzId ?: return
        event.whoClicked.sendMessage("ID: $id")

        if (id.key == "back") {
            RainbowQuartz.menuManager.MAIN_MENU.showMenu(event.whoClicked)
            return
        } else if (id.key == "next_page" || id.key == "previous_page") {
            val page = (item.itemMeta.lore()!![0].children()[0] as TextComponent).content().toInt() - 1
            RainbowQuartz.menuManager.ITEM_EDITOR.showMenu(event.whoClicked, page)
        }
    }
}