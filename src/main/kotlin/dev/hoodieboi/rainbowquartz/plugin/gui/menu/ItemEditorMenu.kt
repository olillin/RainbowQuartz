package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.item.Item
import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem.EditItemGeneralMenu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * @param viewer The viewer of the inventory
 * @param page The 0-based
 */
class ItemEditorMenu(override val viewer: HumanEntity, private var page: Int,
                     override val previousMenu: Menu?
) : ImmutableMenu() {
    constructor(viewer: HumanEntity, previousMenu: Menu?) : this(viewer, 0, previousMenu)

    override var inventory: Inventory = Bukkit.createInventory(viewer, 54, Component.text("Item Editor"))

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
        renderPaginator()
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (!LinkItem.isMenuItemClick(event)) return

        val item = event.currentItem ?: return
        val rainbowQuartzItem = RainbowQuartz.itemManager.getItem(item) ?: return
        event.isCancelled = true
        viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
        EditItemGeneralMenu(event.whoClicked, ItemBuilder(rainbowQuartzItem), this).open()
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "back" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                back()
                return
            }
            "next_page" -> {
                viewer.playSound(Sound.ITEM_BOOK_PAGE_TURN)
                page++
                renderPaginator()
            }
            "previous_page" -> {
                viewer.playSound(Sound.ITEM_BOOK_PAGE_TURN)
                page--
                renderPaginator()
            }
        }
    }

    private fun renderPaginator() {
        Paginator.render(
            inventory,
            RainbowQuartz.itemManager.itemKeys.sortedBy { it.toString() },
            { listedItem(RainbowQuartz.itemManager.getItem(it)!!) },
            page,
            7,
            6,
            2,
            0
        )
    }

    private fun listedItem(item: Item): ItemStack {
        val result = ItemStack(item.item)
        val meta = result.itemMeta
        val lore = meta.lore() ?: ArrayList()
        lore.add(0, Component.text(item.key.toString()).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false))
        meta.lore(lore)
        result.itemMeta = meta
        return result
    }
}