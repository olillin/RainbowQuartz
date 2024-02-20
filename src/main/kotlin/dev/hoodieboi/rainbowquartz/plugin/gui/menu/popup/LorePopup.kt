package dev.hoodieboi.rainbowquartz.plugin.gui.menu.popup

import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.ImmutableMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Menu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Paginator
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class LorePopup(override val viewer: HumanEntity, val placeholder: List<Component>?, override val previousMenu: Menu?, override val callback: (List<Component>) -> Unit) : ImmutableMenu(), Popup<List<Component>> {
    override val inventory: Inventory = Bukkit.createInventory(viewer, 54, Component.text("Lore editor"))
    val lore: MutableList<Component> = placeholder?.toMutableList() ?: mutableListOf()
    private var page: Int = 0

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onOpen(event: InventoryOpenEvent) = render()

    private fun render() {
        inventory.setItem(0, LinkItem.makeLink(
                "add",
                Material.NETHER_STAR,
                Component.text("Add line").color(NamedTextColor.GREEN)
        ))
        inventory.setItem(9, EMPTY_PANEL)
        inventory.setItem(18, EMPTY_PANEL)
        inventory.setItem(27, EMPTY_PANEL)
        inventory.setItem(36, LinkItem.CANCEL)
        inventory.setItem(45, LinkItem.SUBMIT)
        Paginator.render(
                inventory,
                lore.mapIndexed { i, text -> Pair(i, text) },
                { pair -> lineItem(pair.first+1, pair.second) },
                page,
                PAGINATOR_WIDTH,
                PAGINATOR_HEIGHT,
                PAGINATOR_X,
                PAGINATOR_Y
        )
    }

    private fun lineItem(line: Int, text: Component): ItemStack {
        return LinkItem.makeLink(
                "lore",
                Material.PAPER,
                text,
                listOf(
                        Component.text("Line $line").color(NamedTextColor.DARK_GRAY),
                        Component.translatable("key.mouse.left").color(NamedTextColor.GRAY).append(Component.text(" to edit")),
                        Component.translatable("key.mouse.middle").color(NamedTextColor.GRAY).append(Component.text(" to duplicate")),
                        Component.translatable("key.mouse.right").color(NamedTextColor.GRAY).append(Component.text(" to delete"))
                )
        )
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "add" -> {
                ComponentPopup(viewer, null, this) {
                    lore.add(it)
                    render()
                }.open()
            }

            "cancel" -> {
                back()
            }

            "submit" -> {
                callback(lore)
                if (activeViewers().contains(viewer)) {
                    back()
                }
            }

            "next_page" -> {
                page++
                render()
            }

            "previous_page" -> {
                page--
                render()
            }

            "lore" -> {
                val index = toPaginatorSlot(event.slot)
                if (index >= lore.size) return
                when (event.click) {
                    ClickType.LEFT -> {
                        // Edit
                        viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                        ComponentPopup(viewer, lore[index], this) {
                            lore[index] = it
                            render()
                        }.open()
                    }
                    ClickType.MIDDLE -> {
                        // Duplicate
                        viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                        lore.add(index, lore[index])
                        render()
                    }
                    ClickType.RIGHT -> {
                        // Delete
                        viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                        lore.removeAt(index)
                        val pages = (lore.size-1) / ITEMS_PER_PAGE
                        if (page > pages)
                            page--
                        render()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun toPaginatorSlot(slot: Int): Int {
        val offsetSlot = slot - PAGINATOR_X - PAGINATOR_Y * INVENTORY_WIDTH
        val translatedSlot = offsetSlot - (offsetSlot / INVENTORY_WIDTH) * (INVENTORY_WIDTH - PAGINATOR_ITEM_WIDTH)
        return translatedSlot + page * ITEMS_PER_PAGE
    }

    companion object {
        private const val PAGINATOR_WIDTH = 8
        private const val PAGINATOR_HEIGHT = 6
        private const val PAGINATOR_X = 1
        private const val PAGINATOR_Y = 0

        private const val INVENTORY_WIDTH = 9
        private const val PAGINATOR_ITEM_WIDTH = PAGINATOR_WIDTH-1
        private const val ITEMS_PER_PAGE = PAGINATOR_ITEM_WIDTH * PAGINATOR_HEIGHT
    }
}