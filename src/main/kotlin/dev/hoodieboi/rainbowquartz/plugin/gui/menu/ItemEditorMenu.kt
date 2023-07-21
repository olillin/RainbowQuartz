package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem.EditItemGeneralMenu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.Plugin
import kotlin.math.ceil

class ItemEditorMenu(override val viewer: HumanEntity, private val plugin: Plugin, val page: Int) : ImmutableMenu() {
    constructor(viewer: HumanEntity, plugin: Plugin) : this(viewer, plugin, 0)

    override val inventory: Inventory = Bukkit.createInventory(viewer, 54, Component.text("Item Editor"))

    init {
        // Stationary icons
        inventory.setItem(0, LinkItem.makeLink(
            "create_item",
            Material.NETHER_STAR,
            Component.text("New item").color(NamedTextColor.AQUA),
            listOf(
                Component.text("Create a new Item")
            )
        ))
        inventory.setItem(1, EMPTY_PANEL)
        inventory.setItem(9, EMPTY_PANEL)
        inventory.setItem(10, EMPTY_PANEL)
        inventory.setItem(18, LinkItem.makeLink(
            "search",
            Material.OAK_SIGN,
            Component.text("Search").color(NamedTextColor.LIGHT_PURPLE),
            listOf(
                Component.text("Find an item by id, name,"),
                Component.text("item type or namespace")
            )
        ))
        inventory.setItem(19, EMPTY_PANEL)
        inventory.setItem(27, EMPTY_PANEL)
        inventory.setItem(28, EMPTY_PANEL)
        inventory.setItem(36, EMPTY_PANEL)
        inventory.setItem(37, EMPTY_PANEL)
        inventory.setItem(45, LinkItem.makeLink(
            "back",
            Material.ARROW,
            Component.text("Back").color(NamedTextColor.RED)
        ))
        inventory.setItem(46, EMPTY_PANEL)

        // Paginator
        val paginatorWidth = 6
        val paginatorHeight = 6
        val paginatorVolume = paginatorWidth * paginatorHeight
        renderPaginator(inventory, page, 2, 0, paginatorWidth, paginatorHeight)
        val itemCount = RainbowQuartz.itemManager.itemKeys.size
        val pages = ceil(itemCount.toDouble() / paginatorVolume).toInt()
        val itemsShown = if(page < pages-1) paginatorVolume else itemCount % paginatorVolume

        inventory.setItem(8, if(page > 0) LinkItem.makeLink(
            "previous_page",
            Material.ARROW,
            Component.text("↑ Previous Page ↑").color(NamedTextColor.GREEN),
            listOf(
                Component.text("Page ")
                    .append(Component.text("$page"))
                    .append(Component.text("/$pages"))
            )
        ) else EMPTY_PANEL)
        inventory.setItem(17, LinkItem.makeLink("page_indicator",
            Material.PAPER,
            Component.text("Page ${page+1}/$pages").color(NamedTextColor.YELLOW),
            listOf(
                Component.text("Showing $itemsShown/$itemCount items")
            )
        ))
        inventory.setItem(26, if(page < pages-1) LinkItem.makeLink(
            "next_page",
            Material.ARROW,
            Component.text("↓ Next Page ↓").color(NamedTextColor.GREEN),
            listOf(
                Component.text("Page ")
                    .append(Component.text("${page+2}"))
                    .append(Component.text("/$pages"))
            )
        ) else EMPTY_PANEL)
        inventory.setItem(35, EMPTY_PANEL)
        inventory.setItem(44, EMPTY_PANEL)
        inventory.setItem(53, EMPTY_PANEL)
    }

    private fun renderPaginator(inventory: Inventory, page: Int, x: Int, y: Int, width: Int, height: Int) {
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

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.slotType == InventoryType.SlotType.OUTSIDE
            || event.currentItem == null) return
        // Ignore player inventory
        if (event.clickedInventory is PlayerInventory) {
            // Allow player inventory manipulation
            if (!event.isShiftClick) {
                event.isCancelled = false
            }
            return
        }

        val item = event.currentItem ?: return
        val rainbowQuartzItem = RainbowQuartz.itemManager.getItem(item) ?: return
        event.isCancelled = true
        viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
        EditItemGeneralMenu(event.whoClicked, plugin, ItemBuilder(rainbowQuartzItem)).show()
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "back" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                MainMenu(event.whoClicked, plugin).show()
                return
            }

            "next_page" -> {
                viewer.playSound(Sound.ITEM_BOOK_PAGE_TURN)
                ItemEditorMenu(event.whoClicked, plugin, page+1).show()
            }
            "previous_page" -> {
                viewer.playSound(Sound.ITEM_BOOK_PAGE_TURN)
                ItemEditorMenu(event.whoClicked, plugin, page-1).show()
            }
        }
    }
}