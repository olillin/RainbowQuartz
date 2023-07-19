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
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.Plugin
import kotlin.math.ceil

class ItemEditorMenu(val viewer: HumanEntity, private val plugin: Plugin, val page: Int) : Menu() {
    constructor(viewer: HumanEntity, plugin: Plugin) : this(viewer, plugin, 0)

    override val inventory: Inventory
    init {
        inventory = Bukkit.createInventory(viewer, 54, Component.text("Item Editor"))

        // Stationary icons
        inventory.setItem(0, Item.ItemBuilder(NamespacedKey(plugin, "create_item"), Material.NETHER_STAR)
            .setName(Component.text("New item").color(NamedTextColor.AQUA))
            .addLore("Create a new Item")
            .build().item)
        inventory.setItem(1, EMPTY_PANEL)
        inventory.setItem(9, EMPTY_PANEL)
        inventory.setItem(10, EMPTY_PANEL)
        inventory.setItem(18, Item.ItemBuilder(NamespacedKey(plugin, "search"), Material.OAK_SIGN)
            .setName(Component.text("Search").color(NamedTextColor.LIGHT_PURPLE))
            .addLore("Find an item by id, name,")
            .addLore("item type or namespace")
            .build().item)
        inventory.setItem(19, EMPTY_PANEL)
        inventory.setItem(27, EMPTY_PANEL)
        inventory.setItem(28, EMPTY_PANEL)
        inventory.setItem(36, EMPTY_PANEL)
        inventory.setItem(37, EMPTY_PANEL)
        inventory.setItem(45, Item.ItemBuilder(NamespacedKey(plugin, "back"), Material.ARROW)
            .setName(Component.text("Back").color(NamedTextColor.RED))
            .build().item)
        inventory.setItem(46, EMPTY_PANEL)

        // Paginator
        val paginatorWidth = 6
        val paginatorHeight = 6
        val paginatorVolume = paginatorWidth * paginatorHeight
        renderPaginator(inventory, page, 2, 0, paginatorWidth, paginatorHeight)
        val itemCount = RainbowQuartz.itemManager.itemKeys.size
        val pages = ceil(itemCount.toDouble() / paginatorVolume).toInt()
        val itemsShown = if(page < pages-1) paginatorVolume else itemCount % paginatorVolume

        inventory.setItem(8, if(page > 0) Item.ItemBuilder(NamespacedKey(plugin, "previous_page"), Material.ARROW)
            .setName(Component.text("↑ Previous Page ↑").color(NamedTextColor.GREEN))
            .addLore(Component.text("Page ")
                .append(Component.text("$page"))
                .append(Component.text("/$pages"))
            ).build().item else EMPTY_PANEL)
        inventory.setItem(17, Item.ItemBuilder(NamespacedKey(plugin, "page_indicator"), Material.PAPER)
            .setName(Component.text("Page ${page+1}/$pages").color(NamedTextColor.YELLOW))
            .addLore("Showing $itemsShown/$itemCount items")
            .build().item)
        inventory.setItem(26, if(page < pages-1) Item.ItemBuilder(NamespacedKey(plugin, "next_page"), Material.ARROW)
            .setName(Component.text("↓ Next Page ↓").color(NamedTextColor.GREEN))
            .addLore(Component.text("Page ")
                .append(Component.text("${page+2}"))
                .append(Component.text("/$pages"))
            ).build().item else EMPTY_PANEL)
        inventory.setItem(35, EMPTY_PANEL)
        inventory.setItem(44, EMPTY_PANEL)
        inventory.setItem(53, EMPTY_PANEL)

        viewer.openInventory(inventory)
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
        event.isCancelled = true
        if (event.currentItem == null) return
        // Ignore player inventory
        if (event.clickedInventory is PlayerInventory) {
            // Allow player inventory manipulation
            if (!event.isShiftClick) {
                event.isCancelled = false
            }
            return
        }

        event.isCancelled = true

        val item = event.currentItem ?: return
        val id = item.itemMeta?.rainbowQuartzId ?: return
        event.whoClicked.sendMessage(Component.text("ID: $id"))

        when (id.key) {
            "back" -> {
                MainMenu(event.whoClicked, plugin)
                return
            }
            "next_page", "previous_page" -> {
                val page = (item.itemMeta.lore()!![0].children()[0] as TextComponent).content().toInt() - 1
                ItemEditorMenu(event.whoClicked, plugin, page)
            }
            else -> {
                val rainbowQuartzItem = RainbowQuartz.itemManager.getItem(item) ?: return
                EditItemMenu(event.whoClicked, plugin, Item.ItemBuilder(rainbowQuartzItem))
            }
        }
    }
}