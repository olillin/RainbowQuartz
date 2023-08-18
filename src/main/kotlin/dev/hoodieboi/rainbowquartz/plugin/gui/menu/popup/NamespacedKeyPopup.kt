package dev.hoodieboi.rainbowquartz.plugin.gui.menu.popup

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.onlyIf
import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.ImmutableMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Menu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.TextDecoration.ITALIC
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryCloseEvent.Reason
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class NamespacedKeyPopup(
    override val viewer: HumanEntity, private val placeholder: NamespacedKey?, override val previousMenu: Menu?, override val callback: (NamespacedKey) -> Unit
) : ImmutableMenu(), Popup<NamespacedKey> {
    override var inventory: Inventory = Bukkit.createInventory(null, InventoryType.CHEST)
    override fun open() {
        viewer.openAnvil(null, true)
        inventory = viewer.openInventory.topInventory
        RainbowQuartz.guiEventDispatcher.registerMenu(this)
        val anvilInventory = inventory as AnvilInventory
        // Set item in first slot
        val stack = ItemStack(if (placeholder == null) Material.MAP else Material.FILLED_MAP)
        val meta = stack.itemMeta
        meta.displayName(Component.text(placeholder?.toString() ?: "").color(YELLOW).decoration(ITALIC, false))
        meta.lore(listOf(
            Component.text("↑ Previous id ↑").color(GRAY).decoration(ITALIC, false)
        ))
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
        stack.itemMeta = meta
        anvilInventory.firstItem = stack
        // Set item in second slot
        anvilInventory.secondItem = LinkItem.makeLink(
            "cancel",
            Material.BARRIER,
            Component.text("Cancel").color(RED)
        )
        // Update GUI
        onPrepareAnvil(PrepareAnvilEvent(viewer.openInventory, null))
    }

    /**
     * Make a white, non-italicized [TextComponent] from a nullable string.
     *
     * @return the new text component.
     */
    private fun String?.toComponent(): TextComponent = Component.text(this ?: "").color(WHITE).decoration(ITALIC, false)

    @EventHandler
    fun onPrepareAnvil(event: PrepareAnvilEvent) {
        val key: NamespacedKey? = parseText(event.inventory)
        viewer.sendMessage("Parsed: ${key ?: "null"}")
        event.result = if (key != null) {
            LinkItem.makeLink(
                "submit",
                Material.FILLED_MAP,
                Component.text("Submit").color(GREEN),
                listOf(
                    Component.text("New id: ")
                        .append(Component.text(key.toString()).color(WHITE))
                )
            )
        } else {
            INVALID_INPUT
        }
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "submit" -> {
                val anvilInventory = inventory as? AnvilInventory
                if (anvilInventory == null) {
                    viewer.sendMessage(
                        Component.text("An unexpected error occurred").color(RED)
                    )
                    return
                }
                val key: NamespacedKey? = parseText(anvilInventory)
                if (key == null) {
                    // Input is invalid
                    viewer.playSound(Sound.BLOCK_ANVIL_PLACE)
                    return
                }

                viewer.playSound(Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT)
                callback(key)
                // In order to give the callback the freedom
                // to open a different menu we only open the
                // previous menu if this menu is still open.
                if (activeViewers().contains(viewer)) {
                    back()
                }
            }
            "cancel" -> {
                back()
            }
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        inventory.clear() // Stop items from getting refunded into player inventory
        if (event.reason != Reason.OPEN_NEW) {
            back()
        }
    }

    private fun parseText(inventory: AnvilInventory): NamespacedKey? {
        val text: String = (inventory.renameText?.trim() onlyIf { it.isNotEmpty() })
            ?: (placeholder?.toString() onlyIf { it.isNotEmpty() })
            ?: return null
        if (!text.matches(Regex("^([0-9a-z_.-]+:)?[0-9a-z_.-]+(/[0-9a-z_.-]+)*$"))) {
            // Invalid resource location
            return null
        }
        return NamespacedKey.fromString(text)
    }

    companion object {
        val INVALID_INPUT: ItemStack = LinkItem.makeLink(
            "submit",
            Material.BARRIER,
            Component.text("Input is invalid").color(RED)
        )
    }
}