package com.olillin.rainbowquartz.gui.menu.popup

import com.olillin.rainbowquartz.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.gui.LinkItem
import com.olillin.rainbowquartz.gui.menu.ImmutableMenu
import com.olillin.rainbowquartz.gui.menu.Menu
import com.olillin.rainbowquartz.gui.menu.fill
import com.olillin.rainbowquartz.gui.menu.popup.ConfirmationPopup.Choice
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory

/** [Popup] that lets a player choose between yes, no and cancel. */
public class ConfirmationPopup(
    override val viewer: HumanEntity,
    message: String = "Are you sure?",
    override val previousMenu: Menu?,
    override val callback: (Choice) -> Unit,
) : ImmutableMenu(), Popup<Choice> {

    override var inventory: Inventory = Bukkit.createInventory(viewer, 9, Component.text(message))

    @Suppress("UNUSED_PARAMETER")
    @EventHandler
    public fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(
            4, LinkItem.makeLink(
                "yes",
                Material.LIME_CONCRETE,
                Component.text("Yes").color(NamedTextColor.GREEN),
            )
        )
        inventory.setItem(
            5, LinkItem.makeLink(
                "no",
                Material.RED_CONCRETE,
                Component.text("No").color(NamedTextColor.RED),
            )
        )
        inventory.setItem(
            6, LinkItem.CANCEL
        )
        inventory.fill(EMPTY_PANEL)
    }

    @EventHandler
    public fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "yes" -> {
                callback(Choice.YES)
                back()
            }

            "no" -> {
                callback(Choice.NO)
                back()
            }

            "cancel" -> {
                callback(Choice.CANCEL)
                back()
            }
        }
    }

    @EventHandler
    public fun onExit(event: InventoryCloseEvent) {
        if (event.reason == InventoryCloseEvent.Reason.OPEN_NEW) return
        callback(Choice.CANCEL)
    }

    public enum class Choice {
        YES,
        NO,
        CANCEL
    }
}