package dev.hoodieboi.rainbowquartz.plugin.gui.menu.popup

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.onlyIf
import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.ImmutableMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.RED
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryCloseEvent.Reason
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.InventoryType.SlotType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack

/** A [Popup] menu that provides a value through text input */
abstract class TextPopup<T>(
        override val viewer: HumanEntity, private val placeholder: T? = null, override val previousMenu: Menu?, override val callback: (T) -> Unit
) : ImmutableMenu(), Popup<T> {
    override var inventory: AnvilInventory = Bukkit.createInventory(null, InventoryType.ANVIL) as AnvilInventory
    final override fun open() {
        inventory = viewer.openAnvil(null, true)!!.topInventory as AnvilInventory
        RainbowQuartz.guiEventDispatcher.registerMenu(this)
        // Set item in first slot
        inventory.firstItem = firstItem(placeholder)
        // Set item in second slot
        inventory.secondItem = LinkItem.CANCEL
    }

    /** Produces a value of type [T] from given [input]. Or returns `null` if the input is invalid. */
    protected abstract fun parseInput(input: String?): T?

    /**
     * Produces an [ItemStack] to be placed in the first slot when opening the popup.
     * It is recommended to set the name of the item to the string representation of the placeholder.
     */
    protected abstract fun firstItem(placeholder: T?): ItemStack

    /** Produces an [ItemStack] to be placed in the result slot when parsing the [input]. */
    protected abstract fun resultItem(input: String?): ItemStack?

    @EventHandler
    fun onPrepareAnvilTextPopup(event: PrepareAnvilEvent) {
        event.result = resultItem(event.inventory.renameText)
    }

    @EventHandler
    open fun onClickTextPopup(event: InventoryClickEvent) {
        if (event.slotType != SlotType.RESULT) return

        val result: T? = parseInput(inventory.renameText)
        if (result == null) {
            viewer.sendMessage(Component.text("Input is invalid.").color(RED))
            return
        }

        callback(result)
        if (activeViewers().contains(viewer)) {
            back()
        }
    }

    @EventHandler
    open fun onLinkTextPopup(event: InventoryClickLinkEvent) {
        if (event.linkKey == "cancel") {
            back()
        }
    }

    @EventHandler
    open fun onCloseTextPopup(event: InventoryCloseEvent) {
        inventory.clear() // Stop items from getting refunded into the player's inventory
        if (event.reason != Reason.OPEN_NEW) {
            back()
        }
    }
}