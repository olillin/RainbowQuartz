package com.olillin.rainbowquartz.plugin.gui.menu.popup

import com.olillin.rainbowquartz.RainbowQuartz
import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.menu.ImmutableMenu
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import com.olillin.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryCloseEvent.Reason
import org.bukkit.event.inventory.InventoryType.SlotType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

/** A [Popup] menu that provides a value through text input */
abstract class TextPopup<T>(
    override val viewer: HumanEntity,
    protected val placeholder: T? = null,
    override val previousMenu: Menu?,
    override val callback: (T) -> Unit
) : ImmutableMenu(), Popup<T> {
    override lateinit var inventory: AnvilInventory
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

    /** Produces an [ItemStack] to be placed in the first slot when opening the popup. */
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
            viewer.playSound(Sound.BLOCK_ANVIL_PLACE)
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

    companion object {
        val INVALID_INPUT: ItemStack
            get() = ItemStack(Material.BARRIER).apply {
                itemMeta = itemMeta.apply {
                    displayName(Component.text("Input is invalid").color(RED).decoration(TextDecoration.ITALIC, false))
                    persistentDataContainer.set(NamespacedKey("rainbowquartz", "uuid"), PersistentDataType.STRING, UUID.randomUUID().toString())
                }
            }
    }
}